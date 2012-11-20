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

package it.unipmn.di.dcs.cloud.core.middleware;

import it.unipmn.di.dcs.cloud.core.middleware.service.IInformationService;
import it.unipmn.di.dcs.cloud.core.middleware.service.IServiceSchedulerService;
//import it.unipmn.di.dcs.cloud.core.middleware.model.is.IInformationProvider;
//import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceScheduler;

/**
 * Interface for middleware facade classes.
 *
 * Provides support for retrieving instance of middleware service components,
 * like the <em>Information Service</em>, the
 * <em>Service Scheduler Service</em>, and so on.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IMiddlewareManager
{
	/** Sets the environment used for retrieving instance components. */
	void setEnv(IMiddlewareEnv value);

	/** Get the current environment. */
	IMiddlewareEnv getEnv();

//	/**
//	 * Returns an instance of the <em>Information Provider</em> component.
//	 */
//	IInformationProvider getInformationProvider();

//	/**
//	 * Returns an instance of the <em>Service Scheduler</em> component.
//	 */
//	IServiceScheduler getServiceScheduler();

	/**
	 * Returns an instance of the <em>Information Service</em> component.
	 */
	IInformationService getInformationService();

	/**
	 * Returns an instance of the <em>Service Scheduler Service</em>
	 * component.
	 */
	IServiceSchedulerService getServiceSchedulerService();
}
