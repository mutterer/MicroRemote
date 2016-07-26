/*
 * Copyright (C) 2015 Bernard Jollans
 * 
 * 	This file is part of MicroRemote.
 *
 *  MicroRemote is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *   
 *  MicroRemote is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You can find a copy of the GNU General Public License along with
 *  the MicroRemote project.  If not, see <http://www.gnu.org/licenses/>.
 */

package global.util;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class IPgetter {
	public static String getPublicIP(){
		try{
			URL oracle = new URL("http://checkip.dyndns.com/");
			BufferedReader in = new BufferedReader(
			new InputStreamReader(oracle.openStream()));

			String inputLine;
			while((inputLine = in.readLine()) != null){
				inputLine = inputLine.replaceAll("[^\\d.]", "");
				if(inputLine != null)
					break;
			}
			in.close();
			if(inputLine == null)
				throw new Exception();
			return inputLine;
		}
		catch(Exception e){
			return "No Connection";
		}
	}
	@SuppressWarnings("rawtypes")
	public static String getLocalIP(){
		try{
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements())
				{
						InetAddress i = (InetAddress) ee.nextElement();
						String addr = i.getHostAddress();
						if(addr.startsWith("192.168"))
							return addr;
				}
			}
				return "No Connection";
		}
		catch(Exception e){
			return "No Conection";
		}
	}
}
