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

package arduinoScriptGenerator;

import global.meta.Constants;
import global.util.StartChecker;
import global.windows.StartErrorDialog;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

import org.micromanager.api.MMPlugin;
import org.micromanager.api.ScriptInterface;

public class ArduinoScriptGenPlugin implements MMPlugin {
		public static String menuName = "Generate Arduino Script";
		public static String tooltipDescription = "Generate a script for controlling the Arduino board."
												+ " You will need to do this to have a automatically detectable device.";

		
		JCheckBox[] chckbxDigital = new JCheckBox[Constants.PINNUMBERDIG];
		JCheckBox[] chckbxAnalog = new JCheckBox[Constants.PINNUMBERANAL];
		JLabel informationLbl;
		ArduinoScriptGeneratorWindow dialog;

		public void setApp(ScriptInterface app) {
			if(StartChecker.requestStart(this.getClass().toString())){
				dialog = new ArduinoScriptGeneratorWindow();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
			else{
				StartErrorDialog sted = new StartErrorDialog();
				sted.setVisible(true);
			}
		}

		public void dispose() {
		}

		public void show() {
		}

		public void configurationChanged() {
		}

		public String getInfo() {
			return "Launch button for the Arduino Remote";
		}

		public String getDescription() {
			return tooltipDescription;
		}

		public String getVersion() {
			return "1.3041";
		}

		public String getCopyright() {
			return "Bernard Jollans, 2014";
		}

	}

