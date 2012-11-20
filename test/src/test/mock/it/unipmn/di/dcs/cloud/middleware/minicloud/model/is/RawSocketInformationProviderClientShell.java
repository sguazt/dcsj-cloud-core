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

package test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.is;

import it.unipmn.di.dcs.common.conversion.Convert;
import it.unipmn.di.dcs.common.format.BpsMeasureUnit;
import it.unipmn.di.dcs.common.format.ByteMeasureUnit;
import it.unipmn.di.dcs.common.format.FrequencyMeasureUnit;
import it.unipmn.di.dcs.common.format.SizeParser;

import it.unipmn.di.dcs.cloud.core.middleware.model.ICloudService;
import it.unipmn.di.dcs.cloud.core.middleware.model.IMachineManagerInfo;
import it.unipmn.di.dcs.cloud.core.middleware.model.IPhysicalMachine;
import it.unipmn.di.dcs.cloud.core.middleware.model.IRepoManagerInfo;
import it.unipmn.di.dcs.cloud.core.middleware.model.sched.IServiceHandle;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.is.RawSocketInformationProvider;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.ModelFactory;
import it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.IVirtualMachine;
//import it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.ServiceHandle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a simple shell for querying the Information Server.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class RawSocketInformationProviderClientShell
{
	// Patterns for implemented messages.
	protected static final Pattern GETVM_REQUEST_PATTERN = Pattern.compile( "^GETVM\\s+(\\d+)$" );
	protected static final Pattern GETVMMACHMNGR_REQUEST_PATTERN = Pattern.compile( "^GETVMMACHMNGR\\s+(\\d+)$" );
	protected static final Pattern GETVMSERVICE_REQUEST_PATTERN = Pattern.compile( "^GETVMSERV\\s+(\\d+)$" );
	protected static final Pattern GETVMSTATUS_REQUEST_PATTERN = Pattern.compile( "^GETVMSTATUS\\s+(\\d+)$" );
	protected static final Pattern LISTPHYMACH_REQUEST_PATTERN = Pattern.compile( "^LISTPHYMACH$" );
	protected static final Pattern LISTREPO_REQUEST_PATTERN = Pattern.compile( "^LISTREPO$" );
	protected static final Pattern LISTSERV_REQUEST_PATTERN = Pattern.compile( "^LISTSERV$" );
	protected static final Pattern LISTVMS_REQUEST_PATTERN = Pattern.compile( "^LISTVM\\s+(\\d+)$" );
	protected static final Pattern REGPHYMACH_REQUEST_PATTERN = Pattern.compile( "^REGPHYMACH\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)$" );
	protected static final Pattern REGREPO_REQUEST_PATTERN = Pattern.compile( "^REGREPO\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)$" );
	protected static final Pattern REGSERV_REQUEST_PATTERN = Pattern.compile( "^REGSERV\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)$" );
	protected static final Pattern REGVM_REQUEST_PATTERN = Pattern.compile( "^REGVM\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+(?:\\.\\d+(?:[eE][+-]\\d+)?)?)$" );
	protected static final Pattern SRVPROTOVER_REQUEST_PATTERN = Pattern.compile( "^SRVPROTOVER$" );

	/** Shows an input prompt and waits for input. */
	private static String Prompt(BufferedReader brd) throws IOException
	{
		System.out.print( ">> " );
		return brd.readLine();
	}

	/** Shows a help message. */
	private static void ShowHelp()
	{
		System.out.println( "[Client>> Begin of Help Commands" );
		System.out.println( "[Client>>>> Begin of Server Commands" );
		System.out.println( "[Client>>>> GETVM <VM_ID>:" );
		System.out.println( "\t\tReturns the submitted service identified by <VM_ID>." );
		System.out.println( "[Client>>>> GETVMMACHMNGR <VM_ID>:" );
		System.out.println( "\t\tReturns the machine manager controlling the submitted service identified by <VM_ID>." );
		System.out.println( "[Client>>>> GETVMSERV <VM_ID>:" );
		System.out.println( "\t\tReturns the service associated to the submitted service identified by <VM_ID>." );
		System.out.println( "[Client>>>> GETVMSTATUS <VM_ID>:" );
		System.out.println( "\t\tReturns the execution status of the service identified by <VM_ID>." );
		System.out.println( "[Client>>>> LISTPHYMACH:" );
		System.out.println( "\t\tList of Physical Machines." );
		System.out.println( "[Client>>>> LISTREPO:" );
		System.out.println( "\t\n\tList of Repository Managers." );
		System.out.println( "[Client>>>> LISTVM <S_ID>:" );
		System.out.println( "\t\n\tList of Service Handles for the given service <S_ID>." );
		System.out.println( "[Client>>>> LISTSERV:" );
		System.out.println( "\t\n\tList of Services." );
		System.out.println( "[Client>>>> REGPHYMACH <PHY_IP> Base64(<CPUTYPE>) <NCPU> Base64(<CPUCLOCK>)" );
		System.out.println( "            Base64(<RAMSIZE>) Base64(<HDSIZE>) Base64(<NETSPEED>) <MAX_VM_NUMBER>)" );
		System.out.println( "            Base64(<MACH_USERNAME>) Base64(<MACH_PASSWORD>) Base64(<XM_USERNAME>)" );
		System.out.println( "            Base64(<XM_PASSWORD> MM_PORT:" );
		System.out.println( "\t\tRegister a Physical Machine." );
		System.out.println( "[Client>>>> REGREPO <IP> <PORT> Base64(<USER>) Base64(<PASSWORD>):" );
		System.out.println( "\t\tRegister a Repository Manager." );
		System.out.println( "[Client>>>> REGSERV <RM_ID> Base64(<NAME>) Base64(<CpuSpec>#<ReqMem>#<ReqHD>):" );
		System.out.println( "\t\tRegister a Service." );
		System.out.println( "[Client>>>> REGVM <S_ID> <PHY_ID> <VM_LOCAL_ID> <VIRT_IP> <VIRT_PORT> <ALLOCATED_MEM> <ALLOCATED_CPU>:" );
		System.out.println( "\t\tRegister a Service Handle (a Virtual Machine)." );
		System.out.println( "[Client>>>> SRVPROTOVER:" );
		System.out.println( "\t\tVersion of the Protocol implemented by the server." );
		System.out.println( "[Client>>>> End of Server Commands" );
		System.out.println( "[Client>>>> Begin of Client Commands" );
		System.out.println( "[Client>>>> !HELP:" );
		System.out.println( "\t\tShow this message." );
		System.out.println( "[Client>>>> !QUIT:" );
		System.out.println( "\t\tQuit from the client." );
		System.out.println( "[Client>>>> End of Client Commands" );
		System.out.println( "[Client>> End of Help Commands" );
	}

	/** Application entry point. */
	public static void main(String[] args)
	{
		if ( args.length != 2 )
		{
			System.out.println( "Usage: " + RawSocketInformationProviderClientShell.class.getName() + " <server-address> <server-port>" );
			System.exit(1);
		}

		InetAddress isAddr = null;
		int isPort = Integer.parseInt( args[1] );

		try
		{
			isAddr = InetAddress.getByName( args[0] );
		}
		catch (Exception e)
		{
			System.err.println("Malformed server address: " + e );
			System.exit(1);
		}

		RawSocketInformationProvider client = null;

		client = new RawSocketInformationProvider(
			isAddr,
			isPort
		);

		BufferedReader brd = null;
		brd = new BufferedReader( new InputStreamReader( System.in ) );

		String line = null;
		try
		{
			while ( ( line = Prompt(brd) ) != null )
			{
				line = line.trim();

				if ( line.length() == 0 )
				{
					continue;
				}

				Matcher matcher = null;

				if ( "!QUIT".equalsIgnoreCase( line ) )
				{
					System.out.println( "[Client>> Bye!" );
					break;
				}

				if ( "!HELP".equalsIgnoreCase( line ) )
				{
					ShowHelp();
					continue;
				}

				matcher = GETVM_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int vmId = Integer.parseInt( matcher.group(1) );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						System.out.println( "[Server>> " + client.getServiceHandle( vmId ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = GETVMMACHMNGR_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int vmId = Integer.parseInt( matcher.group(1) );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						IVirtualMachine vm = ModelFactory.Instance().virtualMachine();
						vm.setId( vmId );

						System.out.println( "[Server>> " + client.getMachineManager( vm ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = GETVMSERVICE_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int vmId = Integer.parseInt( matcher.group(1) );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						IVirtualMachine vm = ModelFactory.Instance().virtualMachine();
						vm.setId( vmId );

						System.out.println( "[Server>> " + client.getService( vm ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = GETVMSTATUS_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int vmId = Integer.parseInt( matcher.group(1) );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						IVirtualMachine vm = ModelFactory.Instance().virtualMachine();
						vm.setId( vmId );

						System.out.println( "[Server>> " + client.getServiceStatus( vm ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = LISTPHYMACH_REQUEST_PATTERN.matcher(line);
				if ( matcher.matches() )
				{
					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						for (IPhysicalMachine mach : client.getPhysicalMachines())
						{
							System.out.println( "[Server>> " + mach );
						}
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = LISTREPO_REQUEST_PATTERN.matcher(line);
				if ( matcher.matches() )
				{
					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						for (IRepoManagerInfo repoMngr : client.getRepoManagers())
						{
							System.out.println( "[Server>> " + repoMngr );
						}
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = LISTSERV_REQUEST_PATTERN.matcher(line);
				if ( matcher.matches() )
				{
					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						for (ICloudService svc : client.getServices())
						{
							System.out.println( "[Server>> " + svc );
						}
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = LISTVMS_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int servId = Integer.parseInt( matcher.group(1) );

					ICloudService svc = ModelFactory.Instance().cloudService();
					svc.setId( servId ); //TODO: do a load from DB for a validity check

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						System.out.println( "[Server>> " + client.getServiceHandles( svc ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = REGPHYMACH_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					String ipAddr = matcher.group(1);
					String cpuType = matcher.group(2);
					int nCpu = Integer.parseInt( matcher.group(3) );
					int cpuClock = Math.round(
							SizeParser.ParseFrequencySize(
								matcher.group(4),
								FrequencyMeasureUnit.MEGAHERTZ,
								FrequencyMeasureUnit.MEGAHERTZ 
							)
					);
					int ramSize = Math.round(
							SizeParser.ParseByteSize(
								matcher.group(5),
								ByteMeasureUnit.MEGABYTE,
								ByteMeasureUnit.MEGABYTE 
							)
					);
					int hdSize = Math.round(
							SizeParser.ParseByteSize(
								matcher.group(6),
								ByteMeasureUnit.MEGABYTE,
								ByteMeasureUnit.MEGABYTE 
							)
					);
					int netSpeed = Math.round(
							SizeParser.ParseBpsSize(
								matcher.group(7),
								BpsMeasureUnit.MEGABPS,
								BpsMeasureUnit.MEGABPS 
							)
					);
					int maxVmNumber = Integer.parseInt( matcher.group(8) );
					String machUserName = new String( Convert.Base64ToBytes( matcher.group(9) ) );
					String machPasswd = new String( Convert.Base64ToBytes( matcher.group(10) ) );
					String xmUserName = new String( Convert.Base64ToBytes( matcher.group(11) ) );
					String xmPasswd = new String( Convert.Base64ToBytes( matcher.group(12) ) );
					int mmPort = Integer.parseInt( matcher.group(13) );

					IPhysicalMachine phyMach = ModelFactory.Instance().physicalMachine();
					phyMach.setName( ipAddr );
					phyMach.setCpuType( cpuType );
					phyMach.setNumberOfCpu( nCpu );
					phyMach.setCpuClock( cpuClock );
					phyMach.setRamSize( ramSize );
					phyMach.setHdSize( hdSize );
					phyMach.setNetSpeed( netSpeed );
					phyMach.setMaxVmNumber( maxVmNumber );
					phyMach.setMachineUserName( machUserName );
					phyMach.setMachinePassword( machPasswd );
					phyMach.setVmManagerUserName( xmUserName );
					phyMach.setVmManagerPassword( xmPasswd );
					phyMach.setMachineManagerPort( mmPort );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						System.out.println( "[Server>> " + client.registerPhysicalMachine( phyMach ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = REGREPO_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					String ipAddr = matcher.group(1);
					int port = Integer.parseInt( matcher.group(2) );
					String b64User = matcher.group(3);
					String b64Passwd = matcher.group(4);
					String user = new String( Convert.Base64ToBytes(b64User) );
					String passwd = new String( Convert.Base64ToBytes(b64Passwd) );

					IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
					repoMngr.setHost( ipAddr );
					repoMngr.setPort( port );
					repoMngr.setUser( user );
					repoMngr.setPassword( passwd );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						System.out.println( "[Server>> " + client.registerRepoManager( repoMngr ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = REGSERV_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int repoMngrId = Integer.parseInt( matcher.group(1) );
					String b64Name = matcher.group(2);
					String b64Descr = matcher.group(3);
					String name = new String( Convert.Base64ToBytes(b64Name) );
					String descr = new String( Convert.Base64ToBytes(b64Descr) );

					IRepoManagerInfo repoMngr = ModelFactory.Instance().repoManagerInfo();
					repoMngr.setId( repoMngrId );

					ICloudService svc = ModelFactory.Instance().cloudService();
					svc.setName(name);
					svc.setRepoManager( repoMngr );

					String[] descrParts = descr.split("#", -1);
					svc.setCpuRequirements(descrParts[0]);
					svc.setMemRequirements(descrParts[1]);
					svc.setStorageRequirements(descrParts[2]);

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						System.out.println( "[Server>> " + client.registerService( svc ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = REGVM_REQUEST_PATTERN.matcher( line );
				if ( matcher.matches() )
				{
					int serviceId = Integer.parseInt( matcher.group(1) );
					//String phyIp = matcher.group(2);
					int phyId = Integer.parseInt( matcher.group(2) );
					String localId = matcher.group(3);
					String virtIp = matcher.group(4);
					int virtPort = Integer.parseInt( matcher.group(5) );
					int allocMem = Integer.parseInt( matcher.group(6) );
					float allocCpu = Float.parseFloat( matcher.group(7) );

					IVirtualMachine vm = ModelFactory.Instance().virtualMachine();
					vm.setServiceId( serviceId );
					//vm.setPhysicalHost( phyIp );
					vm.setPhysicalMachineId( phyId );
					vm.setLocalId( localId );
					vm.setVirtualHost( virtIp );
					vm.setVirtualPort( virtPort );
					vm.setRequestedMem( allocMem );
					vm.setRequestedCpu( allocCpu );

					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						System.out.println( "[Server>> " + client.registerServiceHandle( vm ) );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				matcher = SRVPROTOVER_REQUEST_PATTERN.matcher(line);
				if ( matcher.matches() )
				{
					System.out.println( "[Client>> Begin of Server Reply." );
					try
					{
						System.out.println( "[Server>> " + client.getServerProtocolVersion() );
					}
					catch (Exception ex)
					{
						System.err.println( "[Client>> Caught exception while reading from server: " + ex);
						ex.printStackTrace();
					}
					System.out.println( "[Client>> End of Server Reply." );

					continue;
				}

				System.out.println( "[Client>> Unknown or malformed command. Type '!HELP' for a list of possible commands." );
			}
		}
		catch (Exception e)
		{
			System.err.println( "Caught exception while trying to read input: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
