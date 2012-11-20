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
public class PhysicalMachine implements IPhysicalMachine, Serializable
{
	private static final long serialVersionUID = 100L;

	private int id;
	private String name;
	private String cpuType;
	private int ncpu;
	private int cpuClock;
	private int ramSize;
	private int hdSize;
	private int netSpeed;
	private int maxVmNum;
	private String username;
	private String passwd;
	private String vmMngrUsername;
	private String vmMngrPasswd;
	private int mmport;

	//@{ IPhysicalMachine implementation ///////////////////////////////////

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

	public void setCpuType(String value)
	{
		this.cpuType = value;
	}

	public String getCpuType()
	{
		return this.cpuType;
	}

	public void setNumberOfCpu(int value)
	{
		this.ncpu = value;
	}

	public int getNumberOfCpu()
	{
		return this.ncpu;
	}

	public void setCpuClock(int value)
	{
		this.cpuClock = value;
	}

	public int getCpuClock()
	{
		return this.cpuClock;
	}

	public void setRamSize(int value)
	{
		this.ramSize = value;
	}

	public int getRamSize()
	{
		return this.ramSize;
	}

	public void setHdSize(int value)
	{
		this.hdSize = value;
	}

	public int getHdSize()
	{
		return this.hdSize;
	}

	public void setNetSpeed(int value)
	{
		this.netSpeed = value;
	}

	public int getNetSpeed()
	{
		return this.netSpeed;
	}

	public void setMaxVmNumber(int value)
	{
		this.maxVmNum = value;
	}

	public int getMaxVmNumber()
	{
		return this.maxVmNum;
	}

	public void setMachineUserName(String value)
	{
		this.username = value;
	}

	public String getMachineUserName()
	{
		return this.username;
	}

	public void setMachinePassword(String value)
	{
		this.passwd = value;
	}

	public String getMachinePassword()
	{
		return this.passwd;
	}

	public void setVmManagerUserName(String value)
	{
		this.vmMngrUsername = value;
	}

	public String getVmManagerUserName()
	{
		return this.vmMngrUsername;
	}

	public void setVmManagerPassword(String value)
	{
		this.vmMngrPasswd = value;
	}

	public String getVmManagerPassword()
	{
		return this.vmMngrPasswd;
	}

	public void setMachineManagerPort(int value)
	{
		this.mmport = value;
	}

	public int getMachineManagerPort()
	{
		return this.mmport;
	}

	//@{ IPhysicalMachine implementation ///////////////////////////////////

	@Override
	public String toString()
	{
		return "<Id: " + this.getId() + ", Name: " + this.getName() + ">";
	}
}
