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

package it.unipmn.di.dcs.cloud.core.middleware.model;

import java.io.Serializable;

/**
 * Contains informations about a service scheduler.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ServiceSchedulerInfo implements IServiceSchedulerInfo, Serializable
{
	private static final long serialVersionUID = 10L;

	private int id;
	private String host;
	private int port;

	//@{ IServiceScheduler implementation ///////////////////////////////////////

	public void setId(int value)
	{
		this.id = value;
	}

	public int getId()
	{
		return this.id;
	}

	public void setHost(String value)
	{
		this.host = value;
	}

	public String getHost()
	{
		return this.host;
	}

	public void setPort(int value)
	{
		this.port = value;
	}

	public int getPort()
	{
		return this.port;
	}

	//@} IServiceScheduler implementation ///////////////////////////////////////

	@Override
	public String toString()
	{
		return "<Id: " + this.getId() + ", Host: " + this.getHost() + ", Port: " + this.getPort() + ">";
	}
}
