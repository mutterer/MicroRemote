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

import global.meta.Constants;
import global.windows.ArdWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class FileHandler {

	public void saveFile(HashMap<Integer,String[]> map, File file){
		String fileName = file.getPath();
		if(!fileName.endsWith(Constants.DATATYPE)){
			fileName += Constants.DATATYPE;
		}
		file = new File(fileName);
		FileOutputStream fileOps;
		ObjectOutputStream objObs;
		try {
			fileOps = new FileOutputStream(file);
			objObs = new ObjectOutputStream(fileOps);
			objObs.writeObject(map);
			objObs.close();
		} catch (FileNotFoundException e) {
			ArdWindow.println("Broken file path. Change where Plugin is to /mmplugins/MicroRemote/MicroRemote.jar");
		} catch (IOException e) {
			LogStreamer.write(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Integer,String[]> loadFile(File file)throws Exception{
		HashMap<Integer,String[]> map = new HashMap<Integer,String[]>();
		String fileName = file.getPath();
		if(!fileName.endsWith(Constants.DATATYPE)){
			fileName += Constants.DATATYPE;
		}
		file = new File(fileName);
		FileInputStream fileIps = new FileInputStream(file);
		ObjectInputStream objIps = new ObjectInputStream(fileIps);
		try {
			fileIps = new FileInputStream(file);
			objIps = new ObjectInputStream(fileIps);
			map = (HashMap<Integer,String[]>) objIps.readObject();
			objIps.close();
		} 
		catch(ClassNotFoundException e){
			JOptionPane.showMessageDialog(null, 
					"Invalid or broken file",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			objIps.close();
			throw(e);
		}
		catch(ClassCastException e){
			JOptionPane.showMessageDialog(null, 
					"Invalid or broken file",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			throw(e);
		}
		catch (Exception e) {
			LogStreamer.write(e.getMessage());
			throw(e);
		} 
		return map;
	}
}
