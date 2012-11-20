/*
 * Copyright (C) 2008  Distributed Computing System (DCS) Group, Computer
 * Science Department - University of Piemonte Orientale, Alessandria (Italy).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.unipmn.di.dcs.cloud.core.middleware.model.sched;

/**
 * Handle of a submitted service.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IServiceHandle
{
	/** Returns the service identifier. */
	int getId();

	/** Returns the mnemonic name. */
	String getName();

	/** Returns the service id. */
	int getServiceId();

	/** Returns the physical machine id. */
	int getPhysicalMachineId();

//	/**
//	 * Returns the physical host address (or name) where the service is
//	 * running.
//	 */
//	@Deprecated
//	String getPhysicalHost();

	/** Returns the virtual host address (or name) of the service. */
	String getVirtualHost();

	/**
	 * Returns the port usable for probing the service.
	 * By convention:
	 * <ul>
	 * <li>a number greater than zero is treated as a valid port number</li>
	 * <li>the value zero means that all ports are probable.</li>
	 * <li>a value less than zero is treated as no port is available for
	 * probing.</li>
	 * </ul>
	 */
	int getProbingPort();

	/** Returns the fraction of CPU numbers allocated to this service. */
	float getAllocatedCpuFraction();

	/** Returns the fraction of RAM size allocated to this service. */
	float getAllocatedRamFraction();

	/** Returns the fraction of disk space allocated to this service. */
	float getAllocatedDiskFraction();

	/** Returns the execution status of this submitted service. */
	ExecutionStatus getStatus();
}
