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
import it.unipmn.di.dcs.cloud.core.middleware.model.IMachineManagerInfo;
import it.unipmn.di.dcs.cloud.core.middleware.model.IRepoManagerInfo;
import it.unipmn.di.dcs.cloud.core.middleware.model.is.IInformationProvider;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;

import java.util.List;

/**
 * Base class for information services.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public abstract class AbstractInformationService implements IInformationService
{
	/** The information provider. */
	private IInformationProvider provider;

	/** A constructor. */
	protected AbstractInformationService(IInformationProvider provider)
	{
		this.provider = provider;
	}

	protected IInformationProvider getInformationProvider()
	{
		return this.provider;
	}

	protected void setInformationProvider(IInformationProvider value)
	{
		this.provider = value;
	}

	//@{ IInformationService implementation ////////////////////////////////

	public IMachineManagerInfo getMachineManager(IServiceHandle shnd) throws CloudServiceException
	{
		try
		{
			return this.provider.getMachineManager(shnd);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Get Machine Manager' service.", e);
		}
	}

	public IPhysicalMachine getPhysicalMachine(int phyMachId) throws CloudServiceException
	{
		try
		{
			return this.provider.getPhysicalMachine(phyMachId);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Get Physical Machine' service.", e);
		}
	}

	public List<IPhysicalMachine> getPhysicalMachines() throws CloudServiceException
	{
		try
		{
			return this.provider.getPhysicalMachines();
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'List of Physical Machines' service.", e);
		}
	}

	public List<IRepoManagerInfo> getRepoManagers() throws CloudServiceException
	{
		try
		{
			return this.provider.getRepoManagers();
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'List of Repository Managers' service.", e);
		}
	}

	public String getServerProtocolVersion() throws CloudServiceException
	{
		try
		{
			return this.provider.getServerProtocolVersion();
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Server Protocol Version' service.", e);
		}
	}

	public ICloudService getService(IServiceHandle shnd) throws CloudServiceException
	{
		try
		{
			return this.provider.getService(shnd);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Get Service' service.", e);
		}
	}

	public  IServiceHandle getServiceHandle(int shndId) throws CloudServiceException
	{
		try
		{
			return this.provider.getServiceHandle(shndId);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Get Service Handle' service.", e);
		}
	}

	public  List<IServiceHandle> getServiceHandles(ICloudService svc) throws CloudServiceException
	{
		try
		{
			return this.provider.getServiceHandles(svc);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'List of Service Handles' service.", e);
		}
	}

	public List<ICloudService> getServices() throws CloudServiceException
	{
		try
		{
			return this.provider.getServices();
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'List of Services' service.", e);
		}
	}

	public ExecutionStatus getServiceStatus(IServiceHandle shnd) throws CloudServiceException
	{
		try
		{
			return this.provider.getServiceStatus(shnd);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Get Service Status' service.", e);
		}
	}

	public boolean isRunning() throws CloudServiceException
	{
		try
		{
			return this.provider.isRunning();
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Is Running' service.", e);
		}
	}

	public IPhysicalMachine registerPhysicalMachine(IPhysicalMachine phyMach) throws CloudServiceException
	{
		try
		{
			return this.provider.registerPhysicalMachine(phyMach);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Register Physical Machine' service.", e);
		}
	}

	public IRepoManagerInfo registerRepoManager(IRepoManagerInfo repoMngr) throws CloudServiceException
	{
		try
		{
			return this.provider.registerRepoManager(repoMngr);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Register Repository Manager' service.", e);
		}
	}

	public IServiceHandle registerServiceHandle(IServiceHandle shnd) throws CloudServiceException
	{
		try
		{
			return this.provider.registerServiceHandle(shnd);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Register Service Handle' service.", e);
		}
	}

	public ICloudService registerService(ICloudService svc) throws CloudServiceException
	{
		try
		{
			return this.provider.registerService(svc);
		}
		catch (Exception e)
		{
			throw new CloudServiceException("Unable to complete the 'Register Service' service.", e);
		}
	}

	//@} IInformationService implementation ////////////////////////////////
}
