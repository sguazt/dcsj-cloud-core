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
import it.unipmn.di.dcs.cloud.core.middleware.service.IInformationService;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.is.RawSocketInformationProvider;
import it.unipmn.di.dcs.cloud.middleware.minicloud.service.InformationService;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;

import test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.is.RawSocketInformationProviderServer;

/**
 * Test unit for the MiniCloud information service
 * using a RawSocket information provider.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class TestMiniCloudRawSocketInformationService
{
	private static final transient Logger Log = Logger.getLogger( TestMiniCloudRawSocketInformationService.class.getName() );

	private static final int SERVER_PORT = 7777;
	private RawSocketInformationProviderServer isServer = null;

	@Before
	public void setUp()
	{
		Log.info("Setting-up test suite...");

		boolean allOk = true;

		this.isServer = new RawSocketInformationProviderServer( SERVER_PORT );

		try
		{
			new Thread(
				new Runnable()
				{
					public void run()
					{
						try
						{
							Log.info("[IS-Server>> Starting the server...");
							TestMiniCloudRawSocketInformationService.this.isServer.start();
						}
						catch (Exception e)
						{
							Log.log(Level.WARNING, "[IS-Server>> Server interrupted: ", e);
						}
					}
				}
			).start();
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[IS-Server>> Caught exception: ", e);

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

		IInformationService isClient = new InformationService(
				new RawSocketInformationProvider( this.isServer.getPort() )
		);

		try
		{
			Log.info("[IS-Service-Consumer>> Requesting if server is running...");

			if ( isClient.isRunning() )
			{
				Log.info("[IS-Service-Consumer>> Server running.");
			}
			else
			{
				Log.info("[IS-Service-Consumer>> Server not running.");
			}

			Log.info("[IS-Service-Consumer>> Requested if server is running.");

			allOk = true;
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[IS-Service-Consumer>> Caught exception: ", e);
		}

		Log.info("Exiting the 'Is Server Running?' test...");

		assertTrue( allOk );
	}

	@Test
	public void testServerProtocolVersionRequest()
	{
		Log.info("Entering the 'Server Protocol Version Request' test...");

		boolean allOk = true;

		IInformationService isClient = new InformationService(
				new RawSocketInformationProvider( this.isServer.getPort() )
		);

		try
		{
			for (int i = 0; i < 3 && !this.isServer.isRunning(); i++)
			{
				Log.info("[IS-Service-Consumer>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.isServer.isRunning() )
			{
				Log.info("[IS-Service-Consumer>> Requesting server protocol version...");

				String protoVer = null;

				protoVer = isClient.getServerProtocolVersion();

				Log.info("Got version: " + protoVer);

				Log.info("[IS-Service-Consumer>> Requested server protocol version.");
			}
			else
			{
				Log.info("[IS-Service-Consumer>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[IS-Service-Consumer>> Caught exception: ", e);

			allOk = false;
		}

		Log.info("Exiting the 'Server Protocol Version Request' test...");

		assertTrue( allOk );
	}

	@Test
	public void testListServices()
	{
		Log.info("Entering the 'List of Services' test...");

		boolean allOk = true;

		IInformationService isClient = new InformationService(
				new RawSocketInformationProvider( this.isServer.getPort() )
		);

		try
		{
			for (int i = 0; i < 3 && !this.isServer.isRunning(); i++)
			{
				Log.info("[IS-Service-Consumer>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.isServer.isRunning() )
			{
				Log.info("[IS-Service-Consumer>> Requesting all services...");

				for (ICloudService service : isClient.getServices())
				{
					Log.info("Got service: " + service);
				}

				Log.info("[IS-Service-Consumer>> Requested all services.");
			}
			else
			{
				Log.info("[IS-Service-Consumer>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[IS-Service-Consumer>> Caught exception: ", e);

			allOk = false;
		}

		Log.info("Exiting the 'List of Services' test...");

		assertTrue( allOk );
	}

	@Test
	public void testListPhysicalMachines()
	{
		Log.info("Entering the 'List of Physical Machines' test...");

		boolean allOk = true;

		IInformationService isClient = new InformationService(
				new RawSocketInformationProvider( this.isServer.getPort() )
		);

		try
		{
			for (int i = 0; i < 3 && !isServer.isRunning(); i++)
			{
				Log.info("[IS-Service-Consumer>> Server seems not to be running (trial #" + (i+1) + "). Sleeping...");
				Thread.sleep(5);
			}

			if ( this.isServer.isRunning() )
			{
				Log.info("[IS-Service-Consumer>> Requesting all physical machines...");

				for (IPhysicalMachine machine : isClient.getPhysicalMachines())
				{
					Log.info("Got physical machine: " + machine);
				}

				Log.info("[IS-Service-Consumer>> Requested all physical machines.");
			}
			else
			{
				Log.info("[IS-Service-Consumer>> Server not running...");

				allOk = false;
			}
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "[IS-Service-Consumer>> Caught exception: ", e);

			allOk = false;
		}

		Log.info("Exiting the 'List of Physical Machines' test...");

		assertTrue( allOk );
	}

	@After
	public void tearDown()
	{
		this.isServer.stop();
	}

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main( TestMiniCloudRawSocketInformationService.class.getName() );
	}
}
