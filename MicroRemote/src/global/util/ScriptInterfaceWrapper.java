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


import java.util.ArrayList;

import javax.swing.JOptionPane;

import mmcorej.StrVector;

public class ScriptInterfaceWrapper {

	private static mmcorej.CMMCore core_;

	public static void initialize(org.micromanager.api.ScriptInterface gui,
			mmcorej.CMMCore core) {
		core_ = core;
	}

	public static String[] getDevicePropertyNames(String label)
			throws Exception {
		StrVector vec = core_.getDevicePropertyNames(label);
		return strVectorToStrArray(vec);
	}
	
	public static String[] getNumberDeviceNames() throws Exception{
		String[] devicesArray = getDeviceNames();
		ArrayList<String> devices = new ArrayList<String>();
		for(int i = 0; i < devicesArray.length; i++){
			String[] properties = getDeviceNumberPropertyNames(devicesArray[i]);
			if(properties.length>0){
				devices.add(devicesArray[i]);
			}
		}
		devicesArray = new String[devices.size()];
		for(int i = 0; i < devicesArray.length; i++){
			devicesArray[i] = devices.get(i);
		}
		return devicesArray;
	}

	public static String getPropertyValue(String label, String propName)
			throws Exception {
		return core_.getProperty(label, propName);
	}

	public static String[] getDeviceNumberPropertyNames(String label)
			throws Exception {
		String[] names = getDevicePropertyNames(label);
		ArrayList<String> namesList = new ArrayList<String>();
		for (int i = 0; i < names.length; i++) {
			if (propertyTypeIsANumber(label, names[i])) {
				namesList.add(names[i]);
			}
		}
		String[] returnArray = new String[namesList.size()];
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = namesList.get(i);
		}
		return returnArray;
	}

	public static String[] getDeviceNames() throws Exception {
		// fixme : Alles ist leer... ...core methoden sind buggy
		/*
		 * String[] namesAll =
		 * strVectorToStrArray(core_.getDeviceAdapterNames()); ArrayList<String>
		 * names = new ArrayList<String>(); for(int i = 0; i < namesAll.length;
		 * i++){ try{ core_.getDevicePropertyNames(namesAll[i]);
		 * names.add(namesAll[i]);
		 * ArdWindow.println("name geaddet in scriptinterfacewrapper"); }
		 * catch(Exception e){
		 * ArdWindow.println("Error bei scriptinterfacewrapper: ");
		 * ArdWindow.print(e.getMessage()); } } return (String[])
		 * names.toArray();
		 */
		StrVector vec = core_.getLoadedDevices();
		return strVectorToStrArray(vec);
	}

	private static String[] strVectorToStrArray(StrVector vec) {
		String[] vecArray = new String[(int) vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			vecArray[i] = vec.get(i);
		}
		return vecArray;
	}

	public static boolean propertyTypeIsANumber(String label, String propName) {
		// PropertyType properType;
		boolean returnValue= false;
		try {
			Double.parseDouble(core_.getProperty(label, propName));
			returnValue = true;
		} catch (Exception e) {
			returnValue = false;
		}
		int maxVal = 1;
		int minVal = 1;
		try{
			maxVal = (int)Math.floor(core_.getPropertyUpperLimit(label, propName));
			minVal = (int)Math.floor(core_.getPropertyLowerLimit(label, propName));
		}
		catch(Exception e){
			returnValue = false;
		}
		if(maxVal == minVal){
			returnValue = false;
		}
		return returnValue;
		
		/*
		 * try { properType = core_.getPropertyType(label, propName); String
		 * propertyType = properType.toString(); boolean numberTypeBool =
		 * (propertyType.toLowerCase().equals("float")||
		 * propertyType.toLowerCase().equals("integer")); return numberTypeBool
		 * && (propertyMaxValue(label, propName) != propertyMinValue(label,
		 * propName)); } catch (Exception e) { return false; }
		 */

	}

	public static String[] getGroupChannelNames(String group) {
		return strVectorToStrArray(core_.getAvailableConfigs(group));
	}

	public static String[] getGroupNames() {
		return strVectorToStrArray(core_.getAvailableConfigGroups());
	}

	public static double propertyMaxValue(String label, String propName)
			throws Exception {
		return core_.getPropertyUpperLimit(label, propName);
	}

	public static double propertyMinValue(String label, String propName)
			throws Exception {
		return core_.getPropertyLowerLimit(label, propName);
	}

	public static boolean isAProperty(String label, String propName) {
		try {
			core_.getProperty(label, propName);
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
					e.getMessage(),
					"ERROR",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}
