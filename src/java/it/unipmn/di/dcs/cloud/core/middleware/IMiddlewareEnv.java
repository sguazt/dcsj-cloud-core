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

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IMiddlewareEnv
{
	static final String IS_HOST_PROP = "is.host";
	static final String IS_PORT_PROP = "is.port";
	static final String SVCSCHED_HOST_PROP = "svcsched.host";
	static final String SVCSCHED_PORT_PROP = "svcsched.port";

	//void setProperties(Properties value);

	//void setProperty(String name, String value);

	//void addProperties(Properties value);

	String getProperty(String name);

	String getInformationServiceHost();

	int getInformationServicePort();

	String getServiceSchedulerServiceHost();

	int getServiceSchedulerServicePort();
}
