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
import global.windows.ArdWindow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class LogStreamer {

	public static BufferedWriter os;
	private static long startTime;
	public static void startStream(String fileName){
		try{
			os.close();
			os = null;
		}
		catch(Exception e){
			
		}
		File file = new File(fileName);
		if(os == null){
			try {
				os = new BufferedWriter(new FileWriter(file));
				startTime = System.currentTimeMillis();
			} catch (IOException e) {
				ArdWindow.println("could not write to "+file.getPath());
			}
		}
	}
	
	public static void write(String string){
		try {
			os.write("["+(System.currentTimeMillis()-startTime)+"] "+ string);
		} catch (Exception e) {
			ArdWindow.println("Could not write to log file location");
		}
	}
	
	public static void close(){
		try{
			os.close();
		}
		catch(Exception e){
			
		}
	}
}
