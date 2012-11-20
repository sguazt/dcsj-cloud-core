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

package test.unit;

import it.unipmn.di.dcs.cloud.core.middleware.IMiddlewareEnv;
import it.unipmn.di.dcs.cloud.core.middleware.IMiddlewareManager;
import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;
import it.unipmn.di.dcs.cloud.core.middleware.model.IRepoManagerInfo;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;
import it.unipmn.di.dcs.cloud.middleware.minicloud.MiddlewareEnv;
import it.unipmn.di.dcs.cloud.middleware.minicloud.MiddlewareManager;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.ModelFactory;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceSchedulerThroughRepoMngr;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;

import test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.is.RawSocketInformationProviderServer;
import test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceSchedulerServer;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestMiniCloudRawSocketServiceSchedulerThroughRepoMngr
{
	private static final transient Logger Log = Logger.getLogger( TestMiniCloudRawSocketServiceSchedulerThroughRepoMngr.class.getName() );

	private static final int IS_SERVER_PORT = 7777;
	private static final int SCHED_SERVER_PORT = 7778;
	private RawSocketServiceSchedulerServer schedServer = null;
	private IMiddlewareManager middleware = null;
	private RawSocketInformationProviderServer isServer = null;

	@Before
	public void setUp()
	{
		Log.info("Setting-up test suite...");

		boolean allOk = true;

		try
		{
			// Starts the scheduler server
			this.schedServer = new RawSocketServiceSchedulerServer( SCHED_SERVER_PORT );
			new Thread(
				new Runnable()
				{
					public void run()
					{
						try
						{
							Log.info("[SCHED-Server>> Starting the server...");
							TestMiniCloudRawSocketServiceSchedulerThroughRepoMngr.this.schedServer.start();
						}
						catch (Exception e)
						{
							Log.log(Level.WARNING, "[SCHED-Server>> Server interrupted: ", e);
						}
					}
				}
			).start();

			// Starts the information server
			this.isServer = new RawSocketInformationProviderServer( IS_SERVER_PORT );
			new Thread(
				new Runnable()
				{
					public void run()
					{
						try
						{
							Log.info("[IS-Server>> Starting the server...");
							TestMiniCloudRawSocketServiceSchedulerThroughRepoMngr.this.isServer.start();
						}
						catch (Exception e)
						{
							Log.log(Level.WARNING, "[IS-Server>> Server interrupted: ", e);
						}
					}
				}
			).start();

			// Setup the middleware context
			MiddlewareEnv env = new MiddlewareEnv();
			env.setProperty( IMiddlewareEnv.IS_HOST_PROP, "localhost" );
			env.setProperty( IMiddlewareEnv.IS_PORT_PROP, Integer.toString(this.isServer.getPort()) );
			env.setProperty( IMiddlewareEnv.SVCSCHED_HOST_PROP, "localhost" );
			env.setProperty( IMiddlewareEnv.SVCSCHED_PORT_PROP, Integer.toString(this.schedServer.getPort()) );
			this.middleware = new MiddlewareManager( env );
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Server>> Caught exception: ", e);

			this.schedServer = null;
			this.isServer = null;
			this.middleware = null;

			allOk = false;
		}

		Log.info("Set-up test suite");

		assertTrue( allOk );
	}

	@Test
	public void testIsRunning() throws Exception
	{
		Log.info("Entering the 'Is Server Running?' test...");

		boolean allOk = true;

		RawSocketServiceSchedulerThroughRepoMngr schedClient = new RawSocketServiceSchedulerThroughRepoMngr( this.middleware );

		try
		{
			Log.info("[SCHED-Client>> Requesting if server is running...");

			if ( schedClient.isRunning() )
			{
				Log.info("[SCHED-Client>> Server running.");
			}
			else
			{
				Log.info("[SCHED-Client>> Server not running.");
			}

			Log.info("[SCHED-Client>> Requested if server is running.");

			allOk = true; 
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Client>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Is Server Running?' test...");

		assertTrue( allOk );
	}

	@Test
	public void testIdRequest() throws Exception
	{
		Log.info("Entering the 'Server ID Request' test...");

		boolean allOk = true;

		RawSocketServiceSchedulerThroughRepoMngr schedClient = new RawSocketServiceSchedulerThroughRepoMngr( this.middleware );

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Client>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Client>> Requesting server ID...");

				String id = null;

				id = schedClient.getId();

				Log.info("[SCHED-Client>> Requested server ID. Got id: " + id );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Client>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Client>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Server ID Request' test...");

		assertTrue( allOk );
	}

	@Test
	public void testServerProtocolVersionRequest() throws Exception
	{
		Log.info("Entering the 'Server Protocol Version Request' test...");

		boolean allOk = true;

		RawSocketServiceSchedulerThroughRepoMngr schedClient = new RawSocketServiceSchedulerThroughRepoMngr( this.middleware );

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Client>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Client>> Requesting server protocol version...");

				String protoVer = null;

				protoVer = schedClient.getServerProtocolVersion();

				Log.info("[SCHED-Client>> Requested server protocol version. Got version: " + protoVer );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Client>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Client>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Server Protocol Version Request' test...");

		assertTrue( allOk );
	}

	@Test
	public void testSubmission() throws Exception
	{
		Log.info("Entering the 'Submission' test...");

		boolean allOk = true;

		RawSocketServiceSchedulerThroughRepoMngr schedClient = new RawSocketServiceSchedulerThroughRepoMngr( this.middleware );

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Client>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Client>> Requesting to submit...");

				ICloudService svc = null;
				IPhysicalMachine machine = null;
				IRepoManagerInfo repoMngr = null;

				repoMngr = ModelFactory.Instance().repoManagerInfo();
				repoMngr.setId( 1 );
				repoMngr.setHost( InetAddress.getLocalHost().getHostAddress() );
				repoMngr.setPort( this.schedServer.getPort() );

				svc = ModelFactory.Instance().cloudService();
				svc.setId( 1 );
				svc.setName( "odissey.2001" );
				svc.setRepoManager( repoMngr );

				machine = ModelFactory.Instance().physicalMachine();
				machine.setId( 1 );
				machine.setName( "hal9000" );

				IServiceHandle svcHnd = null;

				svcHnd = schedClient.submitService( svc, machine );

				Log.info("[SCHED-Client>> Requested submission. Got handle: " + svcHnd );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Client>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Client>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Submission' test...");

		assertTrue( allOk );
	}

	@Test
	public void testServiceStatus() throws Exception
	{
		Log.info("Entering the 'Service Status Retrieval' test...");

		boolean allOk = true;

		RawSocketServiceSchedulerThroughRepoMngr schedClient = new RawSocketServiceSchedulerThroughRepoMngr( this.middleware );

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Client>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Client>> Requesting to submit...");

				ICloudService svc = null;
				IPhysicalMachine machine = null;
				IRepoManagerInfo repoMngr = null;

				repoMngr = ModelFactory.Instance().repoManagerInfo();
				repoMngr.setId( 1 );
				repoMngr.setHost( InetAddress.getLocalHost().getHostAddress() );
				repoMngr.setPort( this.schedServer.getPort() );

				svc = ModelFactory.Instance().cloudService();
				svc.setId( 1 );
				svc.setName( "odissey.2001" );
				svc.setRepoManager( repoMngr );

				machine = ModelFactory.Instance().physicalMachine();
				machine.setId( 1 );
				machine.setName( "hal9000" );

				IServiceHandle svcHnd = null;

				svcHnd = schedClient.submitService( svc, machine );

				Log.info("[SCHED-Client>> Requested submission. Got handle: " + svcHnd );

				Log.info("[SCHED-Client>> Requesting status for service handle '" + svcHnd + "'...");

				ExecutionStatus status = null;
				status = schedClient.getServiceStatus( svcHnd );

				Log.info("[SCHED-Client>> Requested status for service handle '" + svcHnd + "': " + status );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Client>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Client>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Service Status Retrieval' test...");

		assertTrue( allOk );
	}

	@Test
	public void testServiceStop() throws Exception
	{
		Log.info("Entering the 'Service Stop' test...");

		boolean allOk = true;

		RawSocketServiceSchedulerThroughRepoMngr schedClient = new RawSocketServiceSchedulerThroughRepoMngr( this.middleware );

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Client>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Client>> Requesting to submit...");

				ICloudService svc = null;
				IPhysicalMachine machine = null;
				IRepoManagerInfo repoMngr = null;

				repoMngr = ModelFactory.Instance().repoManagerInfo();
				repoMngr.setId( 1 );
				repoMngr.setHost( InetAddress.getLocalHost().getHostAddress() );
				repoMngr.setPort( this.schedServer.getPort() );

				svc = ModelFactory.Instance().cloudService();
				svc.setId( 1 );
				svc.setName( "odissey.2001" );
				svc.setRepoManager( repoMngr );

				machine = ModelFactory.Instance().physicalMachine();
				machine.setId( 1 );
				machine.setName( "hal9000" );

				IServiceHandle svcHnd = null;

				svcHnd = schedClient.submitService( svc, machine );

				Log.info("[SCHED-Client>> Requested submission. Got handle: " + svcHnd );

				Log.info("[SCHED-Client>> Requesting to stop the service handle '" + svcHnd + "'...");

				boolean stopped = false;
				stopped = schedClient.stopService( svcHnd );

				Log.info("[SCHED-Client>> Requested to stop the service handle '" + svcHnd + "': " + stopped );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Client>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Client>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Service Stop' test...");

		assertTrue( allOk );
	}

	@Test
	public void testServiceHandle() throws Exception
	{
		Log.info("Entering the 'Service Handle Retrieval' test...");

		boolean allOk = true;

		RawSocketServiceSchedulerThroughRepoMngr schedClient = new RawSocketServiceSchedulerThroughRepoMngr( this.middleware );

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Client>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Client>> Requesting to submit...");

				ICloudService svc = null;
				IPhysicalMachine machine = null;
				IRepoManagerInfo repoMngr = null;

				repoMngr = ModelFactory.Instance().repoManagerInfo();
				repoMngr.setId( 1 );
				repoMngr.setHost( InetAddress.getLocalHost().getHostAddress() );
				repoMngr.setPort( this.schedServer.getPort() );

				svc = ModelFactory.Instance().cloudService();
				svc.setId( 1 );
				svc.setName( "odissey.2001" );
				svc.setRepoManager( repoMngr );

				machine = ModelFactory.Instance().physicalMachine();
				machine.setId( 1 );
				machine.setName( "hal9000" );

				IServiceHandle svcHnd = null;

				svcHnd = schedClient.submitService( svc, machine );

				Log.info("[SCHED-Client>> Requested submission. Got handle: " + svcHnd );

				Log.info("[SCHED-Client>> Requesting service handle for service id '" + svcHnd.getId() + "'...");

				IServiceHandle svcHnd2 = null;
				svcHnd2 = schedClient.getServiceHandle( svcHnd.getId() );

				Log.info("[SCHED-Client>> Requested service handle for service id '" + svcHnd.getId() + "': " + svcHnd2 );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Client>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Client>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Service Status Retrieval' test...");

		assertTrue( allOk );
	}

	@After
	public void tearDown()
	{
		Log.info("Tearing-down test suite...");

		if ( this.schedServer != null )
		{
			this.schedServer.stop();
		}
		if ( this.isServer != null )
		{
			this.isServer.stop();
		}

		Log.info("Torn-down test suite");
	}

	/** Class application entry point. */
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main(
			TestMiniCloudRawSocketServiceSchedulerThroughRepoMngr.class.getName()
		);
	}
}
