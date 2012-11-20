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

package it.unipmn.di.dcs.cloud.core.middleware.service;

import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceScheduler;

/**
 * Base class for service scheduler services.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractServiceSchedulerService implements IServiceSchedulerService
{
	/** The proxied scheduler. */
	private IServiceScheduler scheduler;

	/** A constructor. */
	protected AbstractServiceSchedulerService(IServiceScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	/** Sets the underlying scheduler communication object. */
	protected IServiceScheduler getServiceScheduler()
	{
		return this.scheduler;
	}

	/** Returns the underlying scheduler communication object. */
	protected void setServiceScheduler(IServiceScheduler value)
	{
		this.scheduler = value;
	}

	//@{ IServiceSchedulerService implementation ///////////////////////////

	public boolean isRunning() throws CloudServiceException
	{
		try
		{
			return this.scheduler.isRunning();
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Is Running' service", e);
		}
	}

	public String getId() throws CloudServiceException
	{
		try
		{
			return this.scheduler.getId();
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Server ID' service", e);
		}
	}

	public String getServerProtocolVersion() throws CloudServiceException
	{
		try
		{
			return this.scheduler.getServerProtocolVersion();
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Server Protocol Version' service", e);
		}
	}

	public IServiceHandle submitService(ICloudService svc, IPhysicalMachine machine) throws CloudServiceException
	{
		try
		{
			return this.scheduler.submitService(svc, machine);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Submit Service' service", e);
		}
	}

	public ExecutionStatus getServiceStatus(IServiceHandle shnd) throws CloudServiceException
	{
		try
		{
			return this.scheduler.getServiceStatus(shnd);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Service Status' service", e);
		}
	}

	public boolean stopService(IServiceHandle shnd) throws CloudServiceException
	{
		try
		{
			return this.scheduler.stopService(shnd);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Service Stop' service", e);
		}
	}

	public IServiceHandle getServiceHandle(int shndId) throws CloudServiceException
	{
		try
		{
			return this.scheduler.getServiceHandle(shndId);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Service Handle' service", e);
		}
	}

	//@} IServiceSchedulerService implementation ///////////////////////////
}
