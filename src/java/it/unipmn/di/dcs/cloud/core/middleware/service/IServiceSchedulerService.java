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

/**
 * Interface for service scheduler services.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IServiceSchedulerService
{
//TODO:
//	IServiceHandle submitService(ICloudService svc) throws CloudServiceException;

	/** Tells if the scheduler service is currently running. */
	boolean isRunning() throws CloudServiceException;

	/** Returns the identifier of this scheduler. */
	String getId() throws CloudServiceException;

	/**
	 * Returns the version of the protocol implemented by the scheduler
	 * server.
	 */
	String getServerProtocolVersion() throws CloudServiceException;

	/**
	 * Starts the given service on the given physical machine (if possible).
	 */
	IServiceHandle submitService(ICloudService svc, IPhysicalMachine machine) throws CloudServiceException;

	/**
	 * Retrieves the execution status of the given service.
	 */
	ExecutionStatus getServiceStatus(IServiceHandle shnd) throws CloudServiceException;

	/**
	 * Stops the execution of the given service.
	 */
	boolean stopService(IServiceHandle shnd) throws CloudServiceException;

	/**
	 * Retrieves the service handle associated to the given service id.
	 */
	IServiceHandle getServiceHandle(int shndId) throws CloudServiceException;

//TODO:
//	void submitServiceAsynch(IVirtualMachine vm, Callback cback);

//TODO:
//	void submitServiceAsynch(IVirtualMachine vm, Stringp[] preferredHosts, Callback cback);
}
