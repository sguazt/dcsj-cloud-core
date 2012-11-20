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

import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;
import it.unipmn.di.dcs.cloud.core.middleware.service.IServiceSchedulerService;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.ModelFactory;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceScheduler;
import it.unipmn.di.dcs.cloud.middleware.minicloud.service.ServiceSchedulerService;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;

import test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceSchedulerServer;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestMiniCloudRawSocketServiceSchedulerService
{
	private static final transient Logger Log = Logger.getLogger( TestMiniCloudRawSocketServiceSchedulerService.class.getName() );

	private static final int SERVER_PORT = 7778;
	private RawSocketServiceSchedulerServer schedServer = null;

	@Before
	public void setUp()
	{
		Log.info("Setting-up test suite...");

		boolean allOk = true;

		this.schedServer = new RawSocketServiceSchedulerServer( SERVER_PORT );

		try
		{
			new Thread(
				new Runnable()
				{
					public void run()
					{
						try
						{
							Log.info("[SCHED-Server>> Starting the server...");
							TestMiniCloudRawSocketServiceSchedulerService.this.schedServer.start();
						}
						catch (Exception e)
						{
							Log.log(Level.WARNING, "[SCHED-Server>> Server interrupted: ", e);
						}
					}
				}
			).start();
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Server>> Caught exception: ", e);

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

		IServiceSchedulerService schedClient = new ServiceSchedulerService(
			new RawSocketServiceScheduler( this.schedServer.getPort() )
		);

		try
		{
			Log.info("[SCHED-Service-Consumer>> Requesting if server is running...");

			if ( schedClient.isRunning() )
			{
				Log.info("[SCHED-Service-Consumer>> Server running.");
			}
			else
			{
				Log.info("[SCHED-Service-Consumer>> Server not running.");
			}

			Log.info("[SCHED-Service-Consumer>> Requested if server is running.");

			allOk = true;
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Service-Consumer>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Is Server Running?' test...");

		assertTrue( allOk );
	}

	@Test
	public void testServerIdRequest() throws Exception
	{
		Log.info("Entering the 'Server ID Request' test...");

		boolean allOk = true;

		IServiceSchedulerService schedClient = new ServiceSchedulerService(
			new RawSocketServiceScheduler( this.schedServer.getPort() )
		);

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Service-Consumer>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Service-Consumer>> Requesting server ID...");

				String id = null;

				id = schedClient.getId();

				Log.info("[SCHED-Service-Consumer>> Requested server ID. Got id: " + id );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Service-Consumer>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Service-Consumer>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Server ID Request' test...");

		assertTrue( allOk );
	}

	@Test
	public void testServerProtocolVersionRequest() throws Exception
	{
		Log.info("Entering the 'Server Protocol Version Request' test...");

		boolean allOk = true;

		IServiceSchedulerService schedClient = new ServiceSchedulerService(
			new RawSocketServiceScheduler( this.schedServer.getPort() )
		);

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Service-Consumer>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Service-Consumer>> Requesting server protocol version...");

				String protoVer = null;

				protoVer = schedClient.getServerProtocolVersion();

				Log.info("[SCHED-Service-Consumer>> Requested server protocol version. Got version: " + protoVer );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Service-Consumer>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Service-Consumer>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Server Protocol Version Request' test...");

		assertTrue( allOk );
	}

	@Test
	public void testSubmission() throws Exception
	{
		Log.info("Entering the 'Submission' test...");

		boolean allOk = true;

		IServiceSchedulerService schedClient = new ServiceSchedulerService(
			new RawSocketServiceScheduler( this.schedServer.getPort() )
		);

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Service-Consumer>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Service-Consumer>> Requesting to submit...");

				ICloudService svc = null;
				IPhysicalMachine machine = null;

				svc = ModelFactory.Instance().cloudService();
				svc.setId( 1 );
				svc.setName( "odissey.2001" );

				machine = ModelFactory.Instance().physicalMachine();
				machine.setId( 1 );
				machine.setName( "hal9000" );

				IServiceHandle svcHnd = null;

				svcHnd = schedClient.submitService( svc, machine );

				Log.info("[SCHED-Service-Consumer>> Requested submission. Got handle: " + svcHnd );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Service-Consumer>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Service-Consumer>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Submission' test...");

		assertTrue( allOk );
	}

	@Test
	public void testServiceStatus() throws Exception
	{
		Log.info("Entering the 'Service Status Retrieval' test...");

		boolean allOk = true;

		IServiceSchedulerService schedClient = new ServiceSchedulerService(
			new RawSocketServiceScheduler( this.schedServer.getPort() )
		);

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Service-Consumer>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Service-Consumer>> Requesting to submit...");

				ICloudService svc = null;
				IPhysicalMachine machine = null;

				svc = ModelFactory.Instance().cloudService();
				svc.setId( 1 );
				svc.setName( "odissey.2001" );

				machine = ModelFactory.Instance().physicalMachine();
				machine.setId( 1 );
				machine.setName( "hal9000" );

				IServiceHandle svcHnd = null;

				svcHnd = schedClient.submitService( svc, machine );

				Log.info("[SCHED-Service-Consumer>> Requested submission. Got handle: " + svcHnd );

				Log.info("[SCHED-Service-Consumer>> Requesting status for service handle '" + svcHnd + "'...");

				ExecutionStatus status = null;
				status = schedClient.getServiceStatus( svcHnd );

				Log.info("[SCHED-Service-Consumer>> Requested status for service handle '" + svcHnd + "': " + status );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Service-Consumer>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Service-Consumer>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Service Status Retrieval' test...");

		assertTrue( allOk );
	}

	@Test
	public void testStopService() throws Exception
	{
		Log.info("Entering the 'Service Stop' test...");

		boolean allOk = true;

		IServiceSchedulerService schedClient = new ServiceSchedulerService(
			new RawSocketServiceScheduler( this.schedServer.getPort() )
		);

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Service-Consumer>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Service-Consumer>> Requesting to submit...");

				ICloudService svc = null;
				IPhysicalMachine machine = null;

				svc = ModelFactory.Instance().cloudService();
				svc.setId( 1 );
				svc.setName( "odissey.2001" );

				machine = ModelFactory.Instance().physicalMachine();
				machine.setId( 1 );
				machine.setName( "hal9000" );

				IServiceHandle svcHnd = null;

				svcHnd = schedClient.submitService( svc, machine );

				Log.info("[SCHED-Service-Consumer>> Requested submission. Got handle: " + svcHnd );

				Log.info("[SCHED-Service-Consumer>> Requesting to stop the service handle '" + svcHnd + "'...");

				boolean stopped = false;
				stopped = schedClient.stopService( svcHnd );

				Log.info("[SCHED-Service-Consumer>> Requested to stop the service handle '" + svcHnd + "': " + stopped );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Service-Consumer>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Service-Consumer>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Service Stop' test...");

		assertTrue( allOk );
	}

	@Test
	public void testServiceHandle() throws Exception
	{
		Log.info("Entering the 'Service Handle Retrieval' test...");

		boolean allOk = true;

		IServiceSchedulerService schedClient = new ServiceSchedulerService(
			new RawSocketServiceScheduler( this.schedServer.getPort() )
		);

		try
		{
		       for (int i = 0; i < 3 && !this.schedServer.isRunning(); i++)
			{
				Log.info("[SCHED-Service-Consumer>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.schedServer.isRunning() )
			{
				Log.info("[SCHED-Service-Consumer>> Requesting to submit...");

				ICloudService svc = null;
				IPhysicalMachine machine = null;

				svc = ModelFactory.Instance().cloudService();
				svc.setId( 1 );
				svc.setName( "odissey.2001" );

				machine = ModelFactory.Instance().physicalMachine();
				machine.setId( 1 );
				machine.setName( "hal9000" );

				IServiceHandle svcHnd = null;

				svcHnd = schedClient.submitService( svc, machine );

				Log.info("[SCHED-Service-Consumer>> Requested submission. Got handle: " + svcHnd );

				Log.info("[SCHED-Service-Consumer>> Requesting service handle for service id '" + svcHnd.getId() + "'...");

				IServiceHandle svcHnd2 = null;
				svcHnd2 = schedClient.getServiceHandle( svcHnd.getId() );

				Log.info("[SCHED-Service-Consumer>> Requested service handle for service id '" + svcHnd.getId() + "': " + svcHnd2 );

				allOk = true;
			}
			else
			{
				Log.info("[SCHED-Service-Consumer>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[SCHED-Service-Consumer>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Service Handle Retrieval' test...");

		assertTrue( allOk );
	}

	@After
	public void tearDown()
	{
		Log.info("Tearing-down test suite...");

		this.schedServer.stop();

		Log.info("Torn-down test suite");
	}

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main( TestMiniCloudRawSocketServiceSchedulerService.class.getName() );
	}
}
