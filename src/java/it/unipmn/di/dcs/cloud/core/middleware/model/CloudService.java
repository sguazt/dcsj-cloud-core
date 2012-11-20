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
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class CloudService implements ICloudService, Serializable
{
	private static final long serialVersionUID = 42L;

	private int id;
	private String name;
	private IRepoManagerInfo repo;
	private String cpuReqs;
	private String memReqs;
	private String diskReqs;

	//@{ ICloudService implementation //////////////////////////////////////

	public void setId(int value)
	{
		this.id = value;
	}

	public int getId()
	{
		return this.id;
	}

	public void setName(String value)
	{
		this.name = value;
	}

	public String getName()
	{
		return this.name;
	}

	public void setRepoManager(IRepoManagerInfo value)
	{
		this.repo = value;
	}

	public IRepoManagerInfo getRepoManager()
	{
		return this.repo;
	}

        public void setCpuRequirements(String value)
	{
		this.cpuReqs = value;
	}

        public String getCpuRequirements()
	{
		return this.cpuReqs;
	}

        public void setMemRequirements(String value)
	{
		this.memReqs = value;
	}

        public String getMemRequirements()
	{
		return this.memReqs;
	}

        public void setStorageRequirements(String value)
	{
		this.diskReqs = value;
	}

        public String getStorageRequirements()
	{
		return this.diskReqs;
	}

	//@} ICloudService implementation //////////////////////////////////////

	@Override
	public String toString()
	{
		return	"<"
			+ "Id: " + this.getId()
			+ ", Name: " + this.getName()
			+ ", Repo Manager: " + this.getRepoManager()
			+ ", CPU Req.: " + this.getCpuRequirements()
			+ ", Mem. Req.: " + this.getMemRequirements()
			+ ", Storage. Req.: " + this.getStorageRequirements()
			+ ">";
	}
}
