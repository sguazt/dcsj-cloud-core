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
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.ExecutionStatus;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;
//import it.unipmn.di.dcs.cloud.core.middleware.model.IServiceSchedulerInfo;

import java.util.List;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IInformationService
{
	/** Returns the machine manager controlling the given submitted service. */
	IMachineManagerInfo getMachineManager(IServiceHandle shnd) throws CloudServiceException;

        /**
	 * Returns the physical machine associated to the given id and that can
	 * work as host for one or more virtual machines.
	 */
        IPhysicalMachine getPhysicalMachine(int phyMachId) throws CloudServiceException;

	/**
	 * Returns the list of all physical machines that can work as host
	 * for one or more virtual machines.
	 */
	List<IPhysicalMachine> getPhysicalMachines() throws CloudServiceException;

	/**
	 * Returns the list of all available repository managers.
	 */
	List<IRepoManagerInfo> getRepoManagers() throws CloudServiceException;

	/**
	 * Returns the protocol version implemented by the information server.
	 */
	String getServerProtocolVersion() throws CloudServiceException;

	/** Returns the service associated to the given submitted service. */
	ICloudService getService(IServiceHandle shnd) throws CloudServiceException;

	/**
	 * Returns the service handle identifier by the given identifier.
	 *
	 * @param shndId The service handle identifier.
	 *
	 * @return The service handle identified by the given identifier.
	 */
        IServiceHandle getServiceHandle(int shndId) throws CloudServiceException;

	/**
	 * Returns the list of service handles associated to the given service.
	 *
	 * @param svc The service object for which related service handles are
	 * to be returned.
	 *
	 * @return A list of service handles representing a submitted instance
	 * of the given service.
	 */
        List<IServiceHandle> getServiceHandles(ICloudService svc) throws CloudServiceException;

	/**
	 * Returns the list of all available services.
	 */
	List<ICloudService> getServices() throws CloudServiceException;

//	/**
//	 * Returns the list of all virtual machines.
//	 */
//	List<IServiceHandle> getServiceHandles() throws CloudServiceException;

	/** Returns the execution status of the given submmited service. */
	ExecutionStatus getServiceStatus(IServiceHandle shnd) throws CloudServiceException;

	/** Tells if the information server is currently running. */
	boolean isRunning() throws CloudServiceException;

	/**
	 * Register the given physical machine and returns the physical machine
	 * object with a new physical machine identifier assigned.
	 *
	 * @param phyMach The service to be registered.
	 *
	 * @return The registered service with a new service identifier.
	 *
	 * In the input service argument, the service identifier should be null.
	 */
	IPhysicalMachine registerPhysicalMachine(IPhysicalMachine phyMach) throws CloudServiceException;

	/**
	 * Register the given repository manager and returns the repository
	 * manager object with a new repository manager identifier assigned.
	 *
	 * @param repoMngr The repository manager to be registered.
	 *
	 * @return The registered repository manager with a new repository
	 * manager identifier.
	 *
	 * In the input repository manager argument, the repository manager
	 * identifier should be null.
	 */
	IRepoManagerInfo registerRepoManager(IRepoManagerInfo repoMngr) throws CloudServiceException;

	/**
	 * Register the given service handle and returns the service handle
	 * object with a new service identifier assigned.
	 *
	 * @param shnd The service handle to be registered.
	 *
	 * @return The registered service handle with a new service identifier.
	 *
	 * In the input service handle argument, the service handle identifier
	 * should be null.
	 */
	IServiceHandle registerServiceHandle(IServiceHandle shnd) throws CloudServiceException;

	/**
	 * Register the given service and returns the service object with a
	 * new service identifier assigned.
	 *
	 * @param svc The service to be registered.
	 *
	 * @return The registered service with a new service identifier.
	 *
	 * In the input service argument, the service identifier should be null.
	 */
	ICloudService registerService(ICloudService svc) throws CloudServiceException;

//	/**
//	 * Returns informations about the given service scheduler.
//	 */
//	IServiceSchedulerInfo getServiceSchedulerInfo(String schedulerId) throws CloudServiceException;
}
