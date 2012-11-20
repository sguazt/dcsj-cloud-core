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

package it.unipmn.di.dcs.cloud.monitor;

import it.unipmn.di.dcs.common.net.HostProber;
import it.unipmn.di.dcs.common.net.IHostProber;
import it.unipmn.di.dcs.common.net.IHostProberStats;
import it.unipmn.di.dcs.common.net.InetProtocol;

import it.unipmn.di.dcs.cloud.core.middleware.IMiddlewareManager;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ServiceProber
{
	private IMiddlewareManager middleware;

	public ServiceProber(IMiddlewareManager midMngr)
	{
		this.middleware = midMngr;
	}

	public IServiceProberStats probe(IServiceHandle shnd) throws CloudMonitorException
	{
		if ( shnd == null )
		{
			throw new CloudMonitorException("Service handle not specified.");
		}

		ServiceProberStats stats = new ServiceProberStats();

		try
		{
			IHostProber hostProber = null;
			IHostProberStats hostProberStats = null;

			hostProber = new HostProber( shnd.getVirtualHost() );
			hostProber.enablePing();
			hostProber.addInetService( InetProtocol.UDP, shnd.getProbingPort() );
			hostProber.addInetService( InetProtocol.TCP, shnd.getProbingPort() );
//			hostProber.addInetServiceStats(
//				hostProberStats.getInetServiceStats(
//					InetProtocol.UDP,
//					shnd.getVirtualPort()
//				)
//			);
//			hostProber.addInetServiceStats(
//				hostProberStats.getInetServiceStats(
//					InetProtocol.TCP,
//					shnd.getVirtualPort()
//				)
//			);

			stats.setExecutionStatus(
				this.middleware.getServiceSchedulerService().getServiceStatus( shnd )
			);

			hostProberStats = hostProber.probe();
			stats.setHostProberStats( hostProberStats );
		}
		catch (Exception e)
		{
			throw new CloudMonitorException(e);
		}

		return stats;
	}
}
