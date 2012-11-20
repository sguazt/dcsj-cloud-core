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

/**
 * Interface for physical machines usage statistics.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IPhysicalMachineUsageStats
{
	/** Set the Physical Machine identifier. */
	void setPhysicalMachineId(int value);

	/** Return the Physical Machine identifier. */
	int getPhysicalMachineId();

	/** Set the available CPU fraction. */
	void setAvailableCpuFraction(float value);

	/** Return the available CPU fraction. */
	float getAvailableCpuFraction();

	/** Set the available RAM fraction. */
	void setAvailableRamFraction(float value);

	/** Return the available RAM fraction. */
	float getAvailableRamFraction();

	/** Set the available disk space fraction. */
	void setAvailableDiskFraction(float value);

	/** Return the available disk space fraction. */
	float getAvailableDiskFraction();
}
