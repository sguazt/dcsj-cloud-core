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

import it.unipmn.di.dcs.common.net.IHostPingerStats;
import it.unipmn.di.dcs.common.net.IHostProberStats;
import it.unipmn.di.dcs.common.net.IInetServiceStats;
import it.unipmn.di.dcs.common.net.InetProtocol;

import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;

import java.util.Collection;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ServiceProberStats implements IServiceProberStats
{
	private ExecutionStatus status;
	private IHostProberStats hostStats;

	public void setExecutionStatus(ExecutionStatus value)
	{
		this.status = value;
	}

	public void setHostProberStats(IHostProberStats value)
	{
		this.hostStats = value;
	}

	//@{ IServiceProberStats implementation ////////////////////////////////

	public ExecutionStatus getExecutionStatus()
	{
		return this.status;
	}

	public IHostPingerStats getHostPingStats()
	{
		return this.hostStats.getPingStats();
	}

	public Collection<IInetServiceStats> getServiceStats()
	{
		return this.hostStats.getInetServicesStats();
	}

	public IInetServiceStats getServiceStats(InetProtocol proto, int port)
	{
		return this.hostStats.getInetServiceStats(proto, port);
	}

	//@} IServiceProberStats implementation ////////////////////////////////
}
