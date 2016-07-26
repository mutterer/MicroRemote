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

public class StartChecker {

	private static String CURRENTPLUGIN = null;
	
	public static boolean requestStart(String requestClass){
		boolean ret = CURRENTPLUGIN==null;
		if(ret){
			CURRENTPLUGIN = requestClass;
		}
		return ret;
	}
	
	public static void requestClose(){
		CURRENTPLUGIN = null;
	}
	
}
