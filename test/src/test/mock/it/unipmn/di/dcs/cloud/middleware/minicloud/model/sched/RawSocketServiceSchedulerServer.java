/*
 * Copyright (C) 2008  Distributed Computing System (DCS) Group, Computer
 * Science Department - University of Piemonte Orientale, Alessandria (Italy).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched;

import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;
import it.unipmn.di.dcs.cloud.core.middleware.service.CloudServiceException;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.ModelFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple multi-threaded scheduler server.
 *
 * This is a toy server, with no memory of submitted services.
 * Nevertheless it is very useful for testing purpose.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class RawSocketServiceSchedulerServer
{
	private static final transient Logger Log = Logger.getLogger( RawSocketServiceSchedulerServer.class.getName() );

	// "Generic Error" reply message.
	protected static final String ERROR_REPLY = "ERR";
	// "Server ID" messages.
	protected static final Pattern SRVID_REQUEST_PATTERN = Pattern.compile( "^SRVID$" );
	protected static final String SRVID_OK_REPLY = "OK";
	protected static final String SRVID_ERROR_REPLY = "ERR";
	// "Server Protocol Version" messages.
	protected static final Pattern SRVPROTOVER_REQUEST_PATTERN = Pattern.compile( "^SRVPROTOVER$" );
	protected static final String SRVPROTOVER_OK_REPLY = "OK";
	protected static final String SRVPROTOVER_ERROR_REPLY = "ERR";
	// "Service Status" messages.
	protected static final Pattern SERVICESTATUS_REQUEST_PATTERN = Pattern.compile( "^GETVMSTATUS\\s+(\\d+)$" );
	protected static final String SERVICESTATUS_OK_REPLY = "OK";
	protected static final String SERVICESTATUS_ERROR_REPLY = "ERR";
	// "Service Stop" messages.
	protected static final Pattern STOPSERVICE_REQUEST_PATTERN = Pattern.compile( "^STOPVM\\s+(\\d+)$" );
	protected static final String STOPSERVICE_OK_REPLY = "OK";
	protected static final String STOPSERVICE_ERROR_REPLY = "ERR";
	// "Service Submit" messages.
	protected static final Pattern SUBMITSERVICE_REQUEST_PATTERN = Pattern.compile( "^SUBMITVM\\s+(\\d+)\\s+(\\d+)$" );
	protected static final String SUBMITSERVICE_OK_REPLY = "OK";
	protected static final String SUBMITSERVICE_ERROR_REPLY = "ERR";

	protected static String SchedulerId; // The scheudler ID

	private boolean running = false;
	private int port;
	private ServerSocket serverSock = null;
	private ThreadGroup threadGroup = null;
	private int serviceId = 1;

	static
	{
		String ip = null;
		try
		{
			ip = InetAddress.getLocalHost().getHostAddress();
		}
		catch (Exception e)
		{
			ip = "0.0.0.0";
		}
		SchedulerId = "raw-socket#" + ip;
	}

	/** A constructor. */
	public RawSocketServiceSchedulerServer()
	{
		this( 0 ); // port==0 => listen on any free port 
	}

	/** A constructor. */
	public RawSocketServiceSchedulerServer(int port)
	{
		this.port = port;

		this.init();
	}

	/** Initializes the inner state. */
	protected void init()
	{
		this.threadGroup = new ThreadGroup( "workers" );
	}

	/** Starts the server. */
	public void start() throws CloudServiceException
	{
		try
		{
			this.serverSock = new ServerSocket( this.port );
			this.port = this.serverSock.getLocalPort();
		}
		catch (Exception e)
		{
			throw new CloudServiceException( "Unable to start the server.", e );
		}

		this.setRunning( true );

		this.startDispatcher( this.serverSock );
	}

	/** Returns true is the server is actually running. */
	public synchronized boolean isRunning()
	{
		return (
				this.running
				&& this.serverSock != null
				&& !this.serverSock.isClosed()
				&& this.serverSock.isBound()
		);
	}

	/** Set the running status of this server. */
	protected synchronized void setRunning(boolean value)
	{
		this.running = value;
	}

	/** Stops the server. */
	public void stop()
	{
		if ( !this.isRunning() )
		{
			return;
		}

		Log.info("[SCHED-Server-Master>> Stopping master...");

		this.setRunning( false );

		Log.info("[SCHED-Server-Master>> Signaling interruption...");

		this.threadGroup.interrupt();

		Log.info("[SCHED-Server-Master>> Waiting for all active threads...");
		try
		{
			Thread[] activeThreads = new Thread[this.threadGroup.activeCount()];
			int nactive = this.threadGroup.enumerate(activeThreads);

			Log.info("[SCHED-Server-Master>> Got " + activeThreads.length + "/" + nactive + " active threads...");

			for (int i = 0; i < activeThreads.length; i++)
			{
				Log.info("[SCHED-Server-Master>> Waiting for thread #" + (i+1) + " (" + activeThreads[i].getName() + ")...");

				activeThreads[i].join();

				Log.info("[SCHED-Server-Master>> Thread #" + (i+1) + " (" + activeThreads[i].getName() + ") exited.");
			}
			//Thread.currentThread().join();
		}
		catch (Exception e)
		{
			// ignore
		}

		Log.info("[SCHED-Server-Master>> Doing clean-up...");
		if ( this.serverSock != null )
		{
			try { this.serverSock.close(); } catch (Exception e) { /* ignore */ }
			this.serverSock = null;
		}

		Log.info("[SCHED-Server-Master>> Master stopped" );
	}

	@Override
	public void finalize()
	{
		this.stop();
	}

	/** Returns the port where this server is listening. */
	public int getPort()
	{
		return this.port;
	}

	/** Create and start a new server dispatcher thread. */
	public void startDispatcher(ServerSocket sock)
	{
		new Thread(
			this.threadGroup,
			new RawSocketServiceSchedulerServer.DispatcherServer(
				this,
				sock
			),
			"dispatcher"
		).start();
	}

	/** Create and start a new server worker thread. */
	public void startWorker(Socket sock)
	{
		new Thread(
			this.threadGroup,
			new RawSocketServiceSchedulerServer.WorkerServer(
				this,
				sock
			),
			"worker"
		).start();
	}

	/**
	 * Returns the next available identifier assignable to a submitted
	 * service.
	 */
	public synchronized int nextServiceId()
	{
		return this.serviceId++;
	}

	/**
	 * The dispatcher thread responsable for accepting incoming connections
	 * and dispatching them to worker servers.
	 */
	private class DispatcherServer implements Runnable
	{
		private RawSocketServiceSchedulerServer masterSrv;
		private ServerSocket serverSock;

		/** A constructor. */
		public DispatcherServer(RawSocketServiceSchedulerServer masterSrv, ServerSocket serverSock)
		{
			this.masterSrv = masterSrv;
			this.serverSock = serverSock;
		}

		//@{ Runnable implementation ///////////////////////////////////

		public void run()
		{
			Log.info("[SCHED-Server-Dispatcher>> Starting dispatcher for: " + this.serverSock);

			// Set a timeout for allowing the current thread to interrupt itself.
			// Without a timeout the thread might be blocked (possible indefinitely)
			// on the "accept" and hence it nevers could take care about interruption
			// coming from master thread.
			try
			{
				this.serverSock.setSoTimeout(10 * 1000);
			}
			catch (Exception e)
			{
				// ignore
			}

			while ( this.masterSrv.isRunning() && !Thread.currentThread().isInterrupted() )
			{
				Socket sock = null;
				try
				{
					//FIXME: handle maximum acceptable connections

					Log.info("[SCHED-Server-Dispatcher>> Accepting connections...");

					sock = this.serverSock.accept();

					Log.info("[SCHED-Server-Dispatcher>> Accepted connection from: " + sock );

					this.masterSrv.startWorker( sock );
				}
//				catch (InterruptedException ie)
//				{
//					this.interrupt();
//					break;
//				}
				catch (SocketTimeoutException ste)
				{
					// ignore: gives the current thread the oppurtunity to check for interruption
					Log.info("[SCHED-Server-Dispatcher>> Timeout expired. Retrying...");
				}
				catch (Exception e)
				{
					//throw new CloudServiceException( "Unable to accept connection to the server.", e );
					Log.warning("[SCHED-Server-Dispatcher>> Caught exception at dispatcher: " + e );
					e.printStackTrace();
				}
			}

			Log.info("[SCHED-Server-Dispatcher>> Stopping dispatcher for: " + this.serverSock);

//			if ( this.serverSock != null )
//			{
//				try { this.serverSock.close(); } catch (Exception e) { /* ignore */ }
//				this.serverSock = null;
//			}

			Thread.currentThread().interrupt();
		}

		//@} Runnable implementation ///////////////////////////////////
	}

	/**
	 * The worker thread responsible for communicating with a client.
	 */
	private class WorkerServer implements Runnable
	{
		private RawSocketServiceSchedulerServer masterSrv;
		private Socket socket;

		/** A constructor. */
		public WorkerServer(RawSocketServiceSchedulerServer masterSrv, Socket socket)
		{
			this.masterSrv = masterSrv;
			this.socket = socket;
		}

		//@{ Runnable implementation ///////////////////////////////////

		public void run()
		{
			Log.info("[SCHED-Server-Worker>> Staring worker on: " + this.socket);

			try
			{
				PrintWriter out = new PrintWriter(
						this.socket.getOutputStream(),
						true
				);
				BufferedReader in = new BufferedReader(
					new InputStreamReader(
						this.socket.getInputStream()
					)
				);

				String inputLine = null;
				String outputLine = null;

				while ( (inputLine = in.readLine()) != null )
				{
					inputLine = inputLine.trim();

					// Chekcs for empty lines
					if ( inputLine.length() == 0 )
					{
						// Skips empty lines
						continue;
					}

					Matcher matcher = null;

					// Checks for a "Server ID" request
					matcher = SRVID_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						Log.info("[SCHED-Server-Worker>> Serving 'Server ID' request... for: " + this.socket);

						out.println( SRVID_OK_REPLY + " " + RawSocketServiceSchedulerServer.GetId() );

						Log.info("[SCHED-Server-Worker>> Served 'Service ID' request... for: " + this.socket);

						continue;
					}

					// Checks for a "Server Protocol Version" request
					matcher = SRVPROTOVER_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						Log.info("[SCHED-Server-Worker>> Serving 'Server Protocol Version' request... for: " + this.socket);

						out.println( SRVPROTOVER_OK_REPLY + " " + RawSocketServiceSchedulerServer.GetProtocolVersion() );

						Log.info("[SCHED-Server-Worker>> Served 'Service Protocol Version' request... for: " + this.socket);

						continue;
					}

					// Checks for a "Service Status" request
					matcher = SERVICESTATUS_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						int servId = Integer.parseInt( matcher.group(1) );

						Log.info("[SCHED-Server-Worker>> Serving 'Service Status' (service #" + servId + ") request... for: " + this.socket);

						out.println( SERVICESTATUS_OK_REPLY + " " + ExecutionStatusToSchedulerExecStatus(ExecutionStatus.RUNNING) );

						Log.info("[SCHED-Server-Worker>> Served 'Service Status' request... for: " + this.socket);

						continue;
					}

					// Checks for a "Service Stop" request
					matcher = STOPSERVICE_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						int vmId = Integer.parseInt( matcher.group(1) );

						Log.info("[SCHED-Server-Worker>> Serving 'Service Stop' (running service #" + vmId + ") request... for: " + this.socket);

						out.println( STOPSERVICE_OK_REPLY + " " + vmId );

						Log.info("[SCHED-Server-Worker>> Served 'Service Stop' request... for: " + this.socket);

						continue;
					}

					// Checks for a "Service Submit" request
					matcher = SUBMITSERVICE_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						String servId = matcher.group(1);
						String machineId = matcher.group(2);

						Log.info("[SCHED-Server-Worker>> Serving 'Service Submission' request... for: " + this.socket);

						out.println( SUBMITSERVICE_OK_REPLY + " " + this.masterSrv.nextServiceId() );

						Log.info("[SCHED-Server-Worker>> Served 'Service Submission' request... for: " + this.socket);

						continue;
					}

					Log.info("[SCHED-Server-Worker>> Unknow command request... for: " + this.socket);

					// Unknown request: reply with error
					out.println( ERROR_REPLY + " 1" );

					Log.info("[SCHED-Server-Worker>> Returned Error reply... for: " + this.socket);

					break;
				}
				out.close();
				in.close();
				this.socket.close();
			}
			catch (Exception e)
			{
				Log.warning("[SCHED-Server-Worker>> Caught exception at worker: " + e );
				e.printStackTrace();
			}


                        Log.info("[SCHED-Server-Worker>> Stopping worker on: " + this.socket);

                        if ( this.socket != null )
                        {
                                try { this.socket.close(); } catch (Exception e) { /* ignore */ }
                                this.socket = null;
                        }

                        Thread.currentThread().interrupt();
		}

		//@} Runnable implementation ///////////////////////////////////
	}

	public static String GetId()
	{
		return SchedulerId;
	}

	/** Returns the protocol version implemented by this scheduler. */
	public static String GetProtocolVersion()
	{
		return "2.1.2";
	}

        /**
	 * Converts the execution status enumeration value into an integer
	 * value suitable to be returned by the scheduler.
	 */
	protected static int ExecutionStatusToSchedulerExecStatus(ExecutionStatus status)
	{
		switch (status)
		{
			case UNSTARTED:
				return 1;
			case READY:
				return 2;
			case RUNNING:
				return 3;
			case FINISHED:
				return 4;
			case CANCELLED:
				return 5;
			case FAILED:
				return 6;
			case ABORTED:
				return 7;
		}

		return 0;
	}

	/** Class entry point. */
	public static void main(String[] args)
	{
		int port;

		if ( args.length > 0 )
		{
			port = Integer.parseInt( args[0] );
		}
		else
		{
			port = 7778;
		}

		RawSocketServiceSchedulerServer server = null;
		server = new RawSocketServiceSchedulerServer(port);

		try
		{
			server.start();

			Thread.currentThread().join();
		}
		catch (Exception e)
		{
			Log.log(Level.SEVERE, "Error while executing the Service Scheduler Server", e);
		}

		System.exit(0);
	}
}
