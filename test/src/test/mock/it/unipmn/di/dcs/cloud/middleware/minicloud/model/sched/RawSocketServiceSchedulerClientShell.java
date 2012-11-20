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

import it.unipmn.di.dcs.cloud.core.middleware.IMiddlewareEnv;
import it.unipmn.di.dcs.cloud.core.middleware.IMiddlewareManager;
import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;
import it.unipmn.di.dcs.cloud.core.middleware.model.IRepoManagerInfo;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceScheduler;
import it.unipmn.di.dcs.cloud.middleware.minicloud.MiddlewareEnv;
import it.unipmn.di.dcs.cloud.middleware.minicloud.MiddlewareManager;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceScheduler;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceSchedulerThroughRepoMngr;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.ModelFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a simple shell for querying the Scheduler Server.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class RawSocketServiceSchedulerClientShell
{
	protected static final Pattern SERVICESTATUS_REQUEST_PATTERN = Pattern.compile( "^GETVMSTATUS\\s+(\\d+)$" );
	protected static final Pattern STOPSERVICE_REQUEST_PATTERN = Pattern.compile( "^STOPVM\\s+(\\d+)$" );
	protected static final Pattern SUBMITSERVICE_REQUEST_PATTERN = Pattern.compile( "^SUBMITVM\\s+(\\d+)\\s+(\\d+)$" );

	/** Shows an input prompt and waits for input. */
	private static String Prompt(BufferedReader brd) throws IOException
	{
		System.out.print( ">> " );
		return brd.readLine();
	}

	/** Shows a help message. */
	private static void ShowHelp()
	{
		System.out.println( "[Client>> Begin of Help Commands" );
		System.out.println( "[Client>>>> Begin of Server Commands" );
		System.out.println( "[Client>>>> GETVMSTATUS <VM_ID>: Returns the execution status of the service SID." );
		System.out.println( "[Client>>>> SRVPROTOVER: Version of the Protocol implemented by the server." );
		System.out.println( "[Client>>>> STOPVM <VM_ID>: Stops the execution of the service SID." );
		System.out.println( "[Client>>>> SUBMITVM <SID> <PHY_ID>: Submit the service SID on the machine PHY_ID." );
		System.out.println( "[Client>>>> End of Server Commands" );
		System.out.println( "[Client>>>> Begin of Client Commands" );
		System.out.println( "[Client>>>> !HELP:" );
		System.out.println( "\t\tShow this message." );
		System.out.println( "[Client>>>> !QUIT:" );
		System.out.println( "\t\tQuit from the client." );
		System.out.println( "[Client>>>> End of Client Commands" );
		System.out.println( "[Client>> End of Help Commands" );
	}

	/** Shows a usage message. */
	private static void ShowUsage()
	{
		System.out.println( "Usage: " + RawSocketServiceSchedulerClientShell.class.getName() + " <type> <server-address> <server-port> [<repomngr-server-addr> <repomngr-server-port>]" );
		System.out.println( "\t<type>: 1 for RawSocketServiceScheduler. 2 for RawSocketServiceSchedulerThroughRepoMngr.\n" );
		System.out.println( "\t<server-addr>: The address of the service scheduler server if <type>==1. The address of the information server if <type>==2.\n" );
		System.out.println( "\t<server-port>: The port of the service scheduler server if <type>==1. The port of the information server if <type>==2.\n" );
		System.out.println( "\t<repomngr-server-addr>: The address of the service scheduler server. Only used if <type>==2.\n" );
		System.out.println( "\t<repomngr-server-port>: The port of the service scheduler server. Only used if <type>==2.\n" );
	}

	/** Application entry point. */
	public static void main(String[] args)
	{
		if ( args.length < 3 )
		{
			ShowUsage();
			System.exit(1);
		}

		InetAddress addr = null;
		int type = Integer.parseInt( args[0] );
		int port = Integer.parseInt( args[2] );
		InetAddress repoMngrAddr = null;
		int repoMngrPort = 0;

		try
		{
			addr = InetAddress.getByName( args[1] );
		}
		catch (Exception e)
		{
			System.err.println("Malformed server address: " + e );
			System.exit(1);
		}

		//RawSocketServiceScheduler client = null;
		IServiceScheduler client = null;

		if ( type == 1 )
		{
			client = new RawSocketServiceScheduler(
				addr,
				port
			);
		}
		else if ( type == 2 )
		{
			if ( args.length < 5 )
			{
				ShowUsage();
				System.exit(1);
			}

			repoMngrPort = Integer.parseInt( args[4] );
			try
			{
				repoMngrAddr = InetAddress.getByName( args[3] );
			}
			catch (Exception e)
			{
				System.err.println("Malformed repo manager server address: " + e );
				System.exit(1);
			}

			MiddlewareEnv env = new MiddlewareEnv();
			env.setProperty( IMiddlewareEnv.IS_HOST_PROP, args[1] );
			env.setProperty( IMiddlewareEnv.IS_PORT_PROP, args[2] );
			IMiddlewareManager middleware = new MiddlewareManager(env);
			client = new RawSocketServiceSchedulerThroughRepoMngr(middleware);
		}
		else
		{
			System.err.println("Unknown value for argument <type>." );
			System.exit(1);
		}

		BufferedReader brd = null;
		brd = new BufferedReader( new InputStreamReader( System.in ) );

		String line = null;
		try
		{
			while ( ( line = Prompt(brd) ) != null )
			{
				line = line.trim();

				if ( line.length() == 0 )
				{
					continue;
				}

				if ( "!QUIT".equalsIgnoreCase( line ) )
				{
					System.out.println( "[Client>> Bye!" );
					break;
				}

				if ( "!HELP".equalsIgnoreCase( line ) )
				{
					ShowHelp();
					continue;
				}

				Matcher matcher = null;

				matcher = SERVICESTATUS_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int servId = Integer.parseInt( matcher.group(1) );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						IServiceHandle shnd;
						shnd = client.getServiceHandle( servId );

						System.out.println( "[Server>> " + client.getServiceStatus( shnd ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = STOPSERVICE_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int servId = Integer.parseInt( matcher.group(1) );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						IServiceHandle shnd;
						shnd = client.getServiceHandle( servId );

						System.out.println( "[Server>> " + client.stopService( shnd ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = SUBMITSERVICE_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int servId = Integer.parseInt( matcher.group(1) );
					int machId = Integer.parseInt( matcher.group(2) );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						IRepoManagerInfo repo = ModelFactory.Instance().repoManagerInfo();
						repo.setId( 0 );
						if ( type == 1 )
						{
							repo.setHost( "dummy-repo" );
							repo.setPort( 0 );
						}
						else
						{
							repo.setHost( repoMngrAddr.getHostAddress() );
							repo.setPort( repoMngrPort );
						}

						ICloudService service = ModelFactory.Instance().cloudService();
						service.setId( servId );
						service.setName( "dummy-service" );
						service.setRepoManager( repo );

						IPhysicalMachine machine = ModelFactory.Instance().physicalMachine();
						machine.setId( machId );
						machine.setName( "MACHINE#" + Integer.toString(machId) );

						IServiceHandle shnd;
						shnd = client.submitService( service, machine );

						System.out.println( "[Server>> " + shnd );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				if ( "SRVPROTOVER".equals(line) )
				{
					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						System.out.println( "[Server>> " + client.getServerProtocolVersion() );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				System.out.println( "[Client>> Unknown command." );
			}
		}
		catch (Exception e)
		{
			System.err.println( "Caught exception while trying to read input: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
