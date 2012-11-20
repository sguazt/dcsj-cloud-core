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
 * Execution Status for submitted virtual machine.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public enum ExecutionStatus
{
	/** A VM has been killed by the middleware. */
	ABORTED,

	/** A VM has been cancelled by the user. */
	CANCELLED,

	/** A VM has failed its execution. */
	FAILED,

	/** A VM is done. */
	//FINISHED,
	STOPPED,

	/** A VM is ready to run and is waiting to be scheduled. */
	READY,

	/** A VM is running. */
	RUNNING,

	/** A VM is ready to run but scheduler isn't able to run it. */
	UNSTARTED,

	/**
	 * A service has been submitted and is currently being transfered to
	 * the physical machine that will run it.
	 */
	STAGING_IN,

//	/**
//	 * A running service is going to be completed: its output file are
//	 * being tranfered from the physical machine to the machine where the
//	 * job has been submitted.
//	 */
//	STAGING_OUT,

	/**
	 * A running service is going to be temporarily paused, but it can be
	 * resumed later.
	 */
	SUSPENDED,

	/** Don't know. */
	UNKNOWN
}
