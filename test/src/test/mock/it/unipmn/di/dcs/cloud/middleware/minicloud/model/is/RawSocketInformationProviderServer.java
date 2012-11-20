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

package test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.is;

import it.unipmn.di.dcs.common.conversion.Convert;

import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IMachineManagerInfo;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;
import it.unipmn.di.dcs.cloud.core.middleware.model.IRepoManagerInfo;
import it.unipmn.di.dcs.cloud.core.middleware.model.is.InformationProviderException;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.ModelFactory;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceSchedulerUtil;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.IVirtualMachine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple multi-threaded information server.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class RawSocketInformationProviderServer
{
	private static final transient Logger Log = Logger.getLogger( RawSocketInformationProviderServer.class.getName() );

	protected static final Pattern GETVM_REQUEST_PATTERN = Pattern.compile( "^GETVM\\s+(\\d+)$" );
	protected static final Pattern GETVMMACHMNGR_REQUEST_PATTERN = Pattern.compile( "^GETVMMACHMNGR\\s+(\\d+)$" );
	protected static final Pattern GETVMSERVICE_REQUEST_PATTERN = Pattern.compile( "^GETVMSERV\\s+(\\d+)$" );
	protected static final Pattern GETVMSTATUS_REQUEST_PATTERN = Pattern.compile( "^GETVMSTATUS\\s+(\\d+)$" );
	protected static final Pattern LISTREPOMNGRS_REQUEST_PATTERN = Pattern.compile( "^LISTREPO$" );
	protected static final Pattern LISTSERVICES_REQUEST_PATTERN = Pattern.compile( "^LISTSERV$" );
	protected static final Pattern LISTPHYSICALMACHINES_REQUEST_PATTERN = Pattern.compile( "^LISTPHYMACH$" );
	protected static final Pattern LISTVMS_REQUEST_PATTERN = Pattern.compile( "^LISTVM$" );
	protected static final Pattern REGPHYMACH_REQUEST_PATTERN = Pattern.compile( "^REGPHYMACH\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)$" );
	protected static final Pattern REGREPO_REQUEST_PATTERN = Pattern.compile( "^REGREPO\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)$" );
	protected static final Pattern REGSERV_REQUEST_PATTERN = Pattern.compile( "^REGSERV\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)$" );
	//protected static final Pattern REGVM_REQUEST_PATTERN = Pattern.compile( "^REGVM\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)$" );
	protected static final Pattern REGVM_REQUEST_PATTERN = Pattern.compile( "^REGVM\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+(?:\\.\\d+(?:[eE][+-]\\d+)?)?)$" );
	protected static final Pattern SRVPROTOVER_REQUEST_PATTERN = Pattern.compile( "^SRVPROTOVER$" );
	protected static final String ERROR_REPLY = "ERR";

	private boolean running = false;
	private int port;
	private int nservices;
	private int nmachines;
	private int repoMngrPort;
	private ServerSocket serverSock = null;
	private ThreadGroup threadGroup = null;
	private Map<Integer,IRepoManagerInfo> repoMngrs = new HashMap<Integer,IRepoManagerInfo>();
	private Map<Integer,ICloudService> services = new HashMap<Integer,ICloudService>();
	private Map<Integer,IPhysicalMachine> phyMachines = new HashMap<Integer,IPhysicalMachine>();
	private Map<Integer,IVirtualMachine> vms = new HashMap<Integer,IVirtualMachine>();
	private int repoMngrSequentialId = 1;
	private int svcSequentialId = 1;
	private int phyMachSequentialId = 1;
	private int vmSequentialId = 1;

	/** A constructor. */
	public RawSocketInformationProviderServer()
	{
		this( 0, 10, 20, 0 ); // port==0 => listen on any free port 
	}

	/** A constructor. */
	public RawSocketInformationProviderServer(int port)
	{
		this( port, 10, 20, 0 );
	}

	/** A constructor. */
	public RawSocketInformationProviderServer(int port, int servicesNum, int machinesNum, int repoMngrPort)
	{
		this.port = port;
		this.nservices = servicesNum;
		this.nmachines = machinesNum;
		this.repoMngrPort = repoMngrPort;

		//this.init();
	}

	/** Initializes the inner state. */
	protected void init()
	{
		if ( this.repoMngrPort == 0 )
		{
			this.repoMngrPort = this.port + 1;
		}

//		// Initializes the services list
////		for (int i = 1; i <= this.nservices; i++)
////		{
////			IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
////			repoMngr.setId( i );
////			repoMngr.setHost( this.serverSock.getInetAddress().getHostAddress() );
////			repoMngr.setPort( this.repoMngrPort );
////
////			ICloudService svc = ModelFactory.Instance().cloudService();
////			svc.setId( i );
////			svc.setName( "Service#" + i );
////			svc.setRepoManager( repoMngr );
////
////			this.services.add( svc );
////		}
//		// Initializes the machines list
////		for (int i = 1; i <= this.nmachines; i++)
////		{
////			IPhysicalMachine phy = ModelFactory.Instance().physicalMachine();
////			phy.setId( i );
////			phy.setName( "Machine#" + i );
////
////			this.phyMachines.add( phy );
////		}
////@{ TEST DCS UPO
//		int dcsTestSvcId = this.nservices+1;
//		int dcsTestPhyId = this.nmachines+1;
//		{ // PC TEOZ
//			IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
//			repoMngr.setId( dcsTestSvcId++ );
//			repoMngr.setHost( "172.22.16.122" );
//			//repoMngr.setHost( "193.206.55.56" );
//			repoMngr.setPort( 12000 );
//
//			ICloudService svc = ModelFactory.Instance().cloudService();
//			svc.setId( 20 );
//			svc.setName( "TEST-DCS-SMALL" );
//			svc.setRepoManager( repoMngr );
//			this.services.add( svc );
//		}
//		{ // PC TEOZ
//			IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
//			repoMngr.setId( dcsTestSvcId++ );
//			repoMngr.setHost( "172.22.16.122" );
//			//repoMngr.setHost( "193.206.55.56" );
//			repoMngr.setPort( 12000 );
//
//			ICloudService svc = ModelFactory.Instance().cloudService();
//			svc.setId( 30 );
//			svc.setName( "TEST-DCS-LARGE" );
//			svc.setRepoManager( repoMngr );
//			this.services.add( svc );
//		}
//		{ // PC TEOZ
//			IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
//			repoMngr.setId( dcsTestSvcId++ );
//			repoMngr.setHost( "172.22.16.122" );
//			//repoMngr.setHost( "193.206.55.56" );
//			repoMngr.setPort( 12000 );
//
//			ICloudService svc = ModelFactory.Instance().cloudService();
//			svc.setId( 40 );
//			svc.setName( "TEST-DCS-SMALL-LOWMEM" );
//			svc.setRepoManager( repoMngr );
//			this.services.add( svc );
//		}
////		{ // PC FRODO
////			IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
////			repoMngr.setId( dcsTestSvcId++ );
////			repoMngr.setHost( "193.206.55.56" );
////			repoMngr.setPort( 12000 );
////
////			ICloudService svc = ModelFactory.Instance().cloudService();
////			svc.setId( 30 );
////			svc.setName( "TEST-DCS-#2" );
////			svc.setRepoManager( repoMngr );
////			this.services.add( svc );
////		}
////		{ // PC SGUAZT
////			IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
////			repoMngr.setId( dcsTestSvcId++ );
////			repoMngr.setHost( "172.22.16.44" );
////			repoMngr.setPort( 12000 );
////
////			ICloudService svc = ModelFactory.Instance().cloudService();
////			svc.setId( 40 );
////			svc.setName( "TEST-DCS-#3" );
////			svc.setRepoManager( repoMngr );
////			this.services.add( svc );
////		}
////		{ // PC SGUAZT LOCAL
////			IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
////			repoMngr.setId( dcsTestSvcId++ );
////			repoMngr.setHost( "0.0.0.0" );
////			repoMngr.setPort( 12000 );
////
////			ICloudService svc = ModelFactory.Instance().cloudService();
////			svc.setId( 40 );
////			svc.setName( "TEST-DCS-LOCAL-LARGE" );
////			svc.setRepoManager( repoMngr );
////			this.services.add( svc );
////
////			svc = ModelFactory.Instance().cloudService();
////			svc.setId( 50 );
////			svc.setName( "TEST-DCS-LOCAL-SMALL" );
////			svc.setRepoManager( repoMngr );
////			this.services.add( svc );
////		}
//
//		IPhysicalMachine phy = ModelFactory.Instance().physicalMachine();
//		phy.setId( dcsTestPhyId++ );
//		phy.setName( "193.206.55.140" );
//
//		this.phyMachines.add( phy );

		int repoId;
		int serviceId;

		repoId = this.registerRepoManager( "172.22.16.122", 12000, null, null );

		serviceId = this.registerService( repoId, "TEST-DCS-SMALL-LOWMEM", null );
		serviceId = this.registerService( repoId, "TEST-DCS-SMALL", null );
		serviceId = this.registerService( repoId, "TEST-DCS-LARGE", null );
////@} TEST DCS UPO

		this.threadGroup = new ThreadGroup( "workers" );
	}

	/** Starts the server. */
	public void start() throws InformationProviderException
	{
		try
		{
			this.serverSock = new ServerSocket( this.port );
			this.port = this.serverSock.getLocalPort();
			this.init();
		}
		catch (Exception e)
		{
			throw new InformationProviderException( "Unable to start the server.", e );
		}

		this.setRunning( true );

		this.startDispatcher( this.serverSock );
//
//		while ( this.isRunning() )
//		{
//			Socket sock = null;
//			try
//			{
//				sock = this.serverSock.accept();
//				new RawSocketInformationProviderServer.WorkerServer(
//					sock,
//					this.services,
//					this.phyMachines
//				).start();
//			}
//			catch (Exception e)
//			{
//				throw new InformationProviderException( "Unable to accept connection to the server.", e );
//			}
//		}
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

		Log.info("[IS-Server-Master>> Stopping master...");

		this.setRunning( false );

		Log.info("[IS-Server-Master>> Signaling interruption...");

		this.threadGroup.interrupt();

		Log.info("[IS-Server-Master>> Waiting for all active threads...");
		try
		{
			Thread[] activeThreads = new Thread[this.threadGroup.activeCount()];
			int nactive = this.threadGroup.enumerate(activeThreads);

			Log.info("[IS-Server-Master>> Got " + activeThreads.length + "/" + nactive + " active threads...");

			for (int i = 0; i < activeThreads.length; i++)
			{
				Log.info("[IS-Server-Master>> Waiting for thread #" + (i+1) + " (" + activeThreads[i].getName() + ")...");
				activeThreads[i].join();
			}
			//Thread.currentThread().join();
		}
		catch (Exception e)
		{
			// ignore
		}

		Log.info("[IS-Server-Master>> Doing clean-up...");
		if ( this.serverSock != null )
		{
			try { this.serverSock.close(); } catch (Exception e) { /* ignore */ }
			this.serverSock = null;
		}

		Log.info("[IS-Server-Master>> Master stopped" );
	}

	@Override
	public void finalize()
	{
		this.stop();
	}

	/** Returns the list of all repo managers. */
	public Collection<IRepoManagerInfo> getRepoManagers()
	{
		return this.repoMngrs.values();
	}

	public IRepoManagerInfo getRepoManager(int repoMngrId)
	{
		return this.repoMngrs.get(repoMngrId);
	}

	public int registerRepoManager(String ipAddr, int port, String user, String password)
	{
		int repoId = this.repoMngrSequentialId++;

		IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
		repoMngr.setId( repoId );
		repoMngr.setHost( ipAddr );
		repoMngr.setPort( port );

		this.repoMngrs.put( repoId, repoMngr );

		return repoId;
	}

	/** Returns the list of all services. */
	public Collection<ICloudService> getServices()
	{
		return this.services.values();
	}

	public ICloudService getService(int serviceId)
	{
		return this.services.get( serviceId );
	}

	public int registerService(int repoMngrId, String name, String descr)
	{
		int serviceId = this.svcSequentialId++;

		IRepoManagerInfo repoMngr = null;
		repoMngr = this.repoMngrs.get( repoMngrId );

		ICloudService svc = ModelFactory.Instance().cloudService();
		svc.setId( serviceId );
		svc.setName( name );
		svc.setRepoManager( repoMngr );

		if ( descr != null )
		{
			String[] descrParts = descr.split("#", -1);
			if ( descrParts.length != 3 )
			{
				//TODO: signal an error
			}
			svc.setCpuRequirements( descrParts[0] );
			svc.setMemRequirements( descrParts[1] );
			svc.setStorageRequirements( descrParts[2] );
		}

		this.services.put( serviceId, svc );

		return serviceId;
	}

	/** Returns the list of all physical machines. */
	public Collection<IPhysicalMachine> getPhysicalMachines()
	{
		return this.phyMachines.values();
	}

	public IPhysicalMachine getPhysicalMachine(int phyMachId)
	{
		return this.phyMachines.get(phyMachId);
	}

	public IPhysicalMachine getPhysicalMachine(String phyMachIp)
	{
		for(IPhysicalMachine phy : this.getPhysicalMachines())
		{
			if ( phy.getName().equals(phyMachIp) )
			{
				return phy;
			}
		}

		return null;
	}

	public int registerPhysicalMachine(String ipAddr)
	{
		int phyMachId = this.phyMachSequentialId++;

		IPhysicalMachine phyMach = ModelFactory.Instance().physicalMachine();
		phyMach.setId( phyMachId );
		phyMach.setName( ipAddr );

		this.phyMachines.put( phyMachId, phyMach );

		return phyMachId;
	}

	public Collection<IVirtualMachine> getVirtualMachines()
	{
		return this.vms.values();
	}

	public IVirtualMachine getVirtualMachine(int vmId)
	{
		return this.vms.get(vmId);
	}

	public int registerVirtualMachine(int serviceId, int phyId, String localId, String virtIp, int virtPort, String allocMem, String allocCpu)
	{
		int vmId = this.vmSequentialId++;

		IVirtualMachine vm = ModelFactory.Instance().virtualMachine();
		vm.setId( vmId );
		vm.setServiceId( serviceId );
		vm.setPhysicalMachineId( phyId );
		vm.setLocalId( localId );

		this.vms.put( vmId, vm );

		return vmId;
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
			new RawSocketInformationProviderServer.DispatcherServer(
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
			new RawSocketInformationProviderServer.WorkerServer(
				this,
				sock
			),
			"worker"
		).start();
	}

	public static String GetProtocolVersion()
	{
		return "2.1.2";
	}

	/**
	 * The dispatcher thread responsable for accepting incoming connections
	 * and dispatching them to worker servers.
	 */
	private class DispatcherServer implements Runnable
	{
		private RawSocketInformationProviderServer masterSrv;
		private ServerSocket serverSock;

		/** A constructor. */
		public DispatcherServer(RawSocketInformationProviderServer masterSrv, ServerSocket serverSock)
		{
			this.masterSrv = masterSrv;
			this.serverSock = serverSock;
		}

		//@{ Runnable implementation ///////////////////////////////////

		public void run()
		{
			Log.info("[IS-Server-Dispatcher>> Starting dispatcher for: " + this.serverSock);

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

					Log.info("[IS-Server-Dispatcher>> Accepting connections...");

					sock = this.serverSock.accept();

					Log.info("[IS-Server-Dispatcher>> Accepted connection from: " + sock );

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
					Log.info("[IS-Server-Dispatcher>> Timeout expired. Retrying...");
				}
				catch (Exception e)
				{
					//throw new InformationProviderException( "Unable to accept connection to the server.", e );
					Log.warning("[IS-Server-Dispatcher>> Caught exception at dispatcher: " + e );
					e.printStackTrace();
				}
			}

			Log.info("[IS-Server-Dispatcher>> Stopping dispatcher for: " + this.serverSock);

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
	 * The worker thread responsable for communicating with a client.
	 */
	private class WorkerServer implements Runnable
	{
		private RawSocketInformationProviderServer masterSrv;
		private Socket socket;
		private Random execStatusRnd;

		/** A constructor. */
		public WorkerServer(RawSocketInformationProviderServer masterSrv, Socket socket)
		{
			this.masterSrv = masterSrv;
			this.socket = socket;
			this.execStatusRnd = new Random();
		}

		//@{ Runnable implementation ///////////////////////////////////

		public void run()
		{
			Log.info("[IS-Server-Worker>> Staring worker on: " + this.socket);

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

					Log.info("[IS-Server-Worker>> Received message: '" + inputLine + "'");

					// Chekcs for empty lines
					if ( inputLine.length() == 0 )
					{
						// Skips empty lines
						continue;
					}

					Matcher matcher = null;

					// Checks for a "Get Submitted Service" request
					matcher = GETVM_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						int vmId = Integer.parseInt( matcher.group(1) );

						IVirtualMachine vm = this.masterSrv.getVirtualMachine( vmId );
						if ( vm != null )
						{
							out.println(
								"OK "
								+ vm.getServiceId()
								//+ " " + vm.getPhysicalHost()
								+ " " + vm.getPhysicalMachineId()
								+ " " + vm.getLocalId()
								+ " " + vm.getVirtualHost()
								+ " " + vm.getVirtualPort()
								+ " " + vm.getStatus()
							);
						}
						else
						{
							out.println( ERROR_REPLY + " 1" );
						}

						continue;
					}
					// Checks for a "Get Machine Manager for a Submitted Service" request
					matcher = GETVMMACHMNGR_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						int vmId = Integer.parseInt( matcher.group(1) );

						IVirtualMachine vm = this.masterSrv.getVirtualMachine( vmId );
						if ( vm != null )
						{
							//String phyHost = vm.getPhysicalHost();
							int phyId = vm.getPhysicalMachineId();
							IPhysicalMachine phy = this.masterSrv.getPhysicalMachine( phyId );
							if ( phy != null )
							{
								out.println(
									"OK "
									+ phy.getId()
									+ " " + phy.getName()
									+ " " + phy.getMachineManagerPort()
									+ " " + vm.getLocalId()
								);
							}
							else
							{
								out.println( ERROR_REPLY + " 1" );
							}
						}
						else
						{
							out.println( ERROR_REPLY + " 1" );
						}

						continue;
					}
					// Checks for a "Get Service for a Submitted Service" request
					matcher = GETVMSERVICE_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						int vmId = Integer.parseInt( matcher.group(1) );

						IVirtualMachine vm = this.masterSrv.getVirtualMachine( vmId );
						if ( vm != null )
						{
							int svcId = vm.getServiceId();
							ICloudService svc = this.masterSrv.getService( svcId );
							if ( svc != null )
							{
								out.println(
									"OK "
									+ svc.getId()
									+ " " + Convert.BytesToBase64( svc.getName().getBytes() )
									+ " " + svc.getRepoManager().getId()
									+ " " + svc.getRepoManager().getHost()
									+ " " + svc.getRepoManager().getPort()
								);
							}
							else
							{
								out.println( ERROR_REPLY + " 1" );
							}
						}
						else
						{
							out.println( ERROR_REPLY + " 1" );
						}

						continue;
					}
					// Checks for a "Service Execution Status" request
					matcher = GETVMSTATUS_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						int vmId = Integer.parseInt( matcher.group(1) );
						//out.println("OK " + this.execStatusRnd.nextInt(10) );
						out.println("OK " + Integer.toString( RawSocketServiceSchedulerUtil.ExecutionStatusToSchedulerExecStatus( ExecutionStatus.RUNNING ) ) );

						continue;
					}
					// Checks for a "List Repository Manager" request
					matcher = LISTREPOMNGRS_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						out.print( "OK " );
						for (IRepoManagerInfo repoMngr : this.masterSrv.getRepoManagers())
						{
							out.println(
								repoMngr.getId()
								+ " " + repoMngr.getHost()
								+ " " + repoMngr.getPort()
								+ " " + Convert.BytesToBase64( repoMngr.getUser().getBytes() )
								+ " " + Convert.BytesToBase64( repoMngr.getPassword().getBytes() )
							);
						}
						out.println(".");

						continue;
					}
					// Checks for a "List Running Services" request
					matcher = LISTVMS_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						int servId = Integer.parseInt( matcher.group(1) );

						out.print( "OK " );
						for (IVirtualMachine vm : this.masterSrv.getVirtualMachines())
						{
							if (vm.getServiceId() == servId)
							{
								out.println(
									+ vm.getId()
									//+ " " + vm.getPhysicalHost()
									+ " " + vm.getPhysicalMachineId()
									+ " " + vm.getVirtualHost()
									+ " " + vm.getVirtualPort()
								);
							}
						}
						out.println(".");

						continue;
					}
					// Checks for a "List Services" request
					matcher = LISTSERVICES_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						out.print( "OK " );
						for (ICloudService svc : this.masterSrv.getServices())
						{
							out.println(
								+ svc.getId()
								+ " " + Convert.BytesToBase64( svc.getName().getBytes() )
								+ " " + svc.getRepoManager().getId()
								+ " " + svc.getRepoManager().getHost()
								+ " " + svc.getRepoManager().getPort()
							);
						}
						out.println(".");

						continue;
					}
					// Checks for a "List Physical Machines" request
					matcher = LISTPHYSICALMACHINES_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						out.print( "OK " );
						for (IPhysicalMachine phy : this.masterSrv.getPhysicalMachines())
						{
							out.println(
								phy.getId()
								+ " " + phy.getName()
								+ " " + phy.getMachineManagerPort()
							);
						}
						out.println(".");

						continue;
					}
					// Checks for a "Physical Machine Registration" request
					matcher = REGPHYMACH_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						String ipAddr = matcher.group(1);
						String cpuType = matcher.group(2);
						int nCpu = Integer.parseInt( matcher.group(3) );
						int cpuClock = Integer.parseInt( new String( Convert.Base64ToBytes( matcher.group(4) ) ) );
						int ramSize = Integer.parseInt( new String( Convert.Base64ToBytes( matcher.group(5) ) ) );
						int hdSize = Integer.parseInt( new String( Convert.Base64ToBytes( matcher.group(6) ) ) );
						int netSpeed = Integer.parseInt( new String( Convert.Base64ToBytes( matcher.group(7) ) ) );
						int maxVmNumber = Integer.parseInt( matcher.group(8) );
						String machUserName = new String( Convert.Base64ToBytes( matcher.group(9) ) );
						String machPasswd = new String( Convert.Base64ToBytes( matcher.group(10) ) );
						String xmUserName = new String( Convert.Base64ToBytes( matcher.group(11) ) );
						String xmPasswd = new String( Convert.Base64ToBytes( matcher.group(12) ) );
						int mmPort = Integer.parseInt( matcher.group(13) );

						int phyMachId = this.masterSrv.registerPhysicalMachine( ipAddr );

						out.println( "OK " + Integer.toString( phyMachId ) );
						continue;
					}
					// Checks for a "Repo Manager Registration" request
					matcher = REGREPO_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						String ipAddr = matcher.group(1);
						int port = Integer.parseInt( matcher.group(2) );
						String b64User = matcher.group(3);
						String b64Passwd = matcher.group(4);

						int repoId = this.masterSrv.registerRepoManager(
									ipAddr,
									port,
									new String(Convert.Base64ToBytes(b64User)),
									new String(Convert.Base64ToBytes(b64Passwd))
						);

						out.println( "OK " + Integer.toString( repoId ) );
						continue;
					}
					// Checks for a "Service Registration" request
					matcher = REGSERV_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						int repoMngrId = Integer.parseInt( matcher.group(1) );
						String b64Name = matcher.group(2);
						String b64Descr = matcher.group(3);

						if ( this.masterSrv.getRepoManager( repoMngrId ) != null )
						{
							int serviceId = this.masterSrv.registerService(
										repoMngrId,
										new String(Convert.Base64ToBytes(b64Name)),
										new String(Convert.Base64ToBytes(b64Descr))
							);

							out.println( "OK " + Integer.toString( serviceId ) );
						}
						else
						{
							out.println( ERROR_REPLY + " 1" );
						}

						continue;
					}
					// Checks for a "Virtual Machine Registration" request
					matcher = REGVM_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						int serviceId = Integer.parseInt( matcher.group(1) );
						//String phyIP = matcher.group(2);
						int phyId = Integer.parseInt( matcher.group(2) );
						String localId = matcher.group(3);
						String virtIp = matcher.group(4);
						int virtPort = Integer.parseInt( matcher.group(5) );
						String allocMem = matcher.group(6);
						String allocCPU = matcher.group(7);

						if ( this.masterSrv.getService( serviceId ) != null )
						{
							int vmId = this.masterSrv.registerVirtualMachine(
									serviceId,
									phyId,
									localId,
									virtIp,
									virtPort,
									allocMem,
									allocCPU
							);

							//out.println("OK " + this.execStatusRnd.nextInt(10) );
							out.println( "OK " + Integer.toString( vmId ) );
						}
						else
						{
							out.println( ERROR_REPLY + " 1" );
						}

						continue;
					}
					// Checks for a "Server Protocol Version" request
					matcher = SRVPROTOVER_REQUEST_PATTERN.matcher( inputLine );
					if ( matcher.matches() )
					{
						out.println( "OK" + " " + RawSocketInformationProviderServer.GetProtocolVersion() );

						continue;
					}

					// Unknown request: reply with error
					out.println( ERROR_REPLY + " 1" );
				}
				out.close();
				in.close();
			}
			catch (Exception e)
			{
				Log.warning("[IS-Server-Worker>> Caught exception at worker: " + e );
				e.printStackTrace();
			}

			Log.info("[IS-Server-Worker>> Stopping worker on: " + this.socket);

			if ( this.socket != null )
			{
				try { this.socket.close(); } catch (Exception e) { /* ignore */ }
				this.socket = null;
			}

			Thread.currentThread().interrupt();
		}

		//@} Runnable implementation ///////////////////////////////////
	}

	/** Class application entry-point. */
	public static void main(String[] args)
	{
		int port;
		int nservices;
		int nmachines;
		int repoMngrPort;

		if ( args.length > 0 )
		{
			port = Integer.parseInt( args[0] );
		}
		else
		{
			port = 7777;
		}
		if ( args.length > 1 )
		{
			nservices = Integer.parseInt( args[1] );
		}
		else
		{
			nservices = 10;
		}
		if ( args.length > 2 )
		{
			nmachines = Integer.parseInt( args[2] );
		}
		else
		{
			nmachines = 10;
		}
		if ( args.length > 3 )
		{
			repoMngrPort = Integer.parseInt( args[3] );
		}
		else
		{
			repoMngrPort = port + 1;
		}

		RawSocketInformationProviderServer server = null;
		server = new RawSocketInformationProviderServer(port, nservices, nmachines, repoMngrPort);

		try
		{
			server.start();

			Thread.currentThread().join();
		}
		catch (Exception e)
		{
			Log.log(Level.SEVERE, "Error while executing the Information Server", e);
		}

		System.exit(0);
	}
}
