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

package plugin;

import global.meta.Constants;
import global.util.FileHandler;
import global.util.LogStreamer;
import global.windows.ArdWindow;

import java.io.File;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

public class Connector implements Observer {

	private MainPlugin microManager;
	private long initTime = 5000;
	private long initTimeSaver;
	private short[] blockedPins = new short[15];

	final int SMALLSTEPS = 16;
	final int MEDSTEPS = SMALLSTEPS + 1;
	final int BIGSTEPS = MEDSTEPS + 1;
	final int STEPCOMPENSATIONVALUE = SMALLSTEPS - 3;

	int stepSize = MEDSTEPS;
	HashMap<Integer,String[]> map;
	FileHandler fh = new FileHandler();

	public Connector(MainPlugin mm) {
		initTimeSaver = System.currentTimeMillis();
		microManager = mm;
//		reload();
	}
	
	public void reload(){
		FileHandler fh = new FileHandler();
		try {
			map = fh.loadFile(new File(Constants.CONFFILENAME));
			if(!microManager.validateMap(map)){
				JOptionPane.showMessageDialog(null, 
						" Your configuration does not fit your hardware. \n Please change your configuration",
						" Configuration Old",
						JOptionPane.ERROR_MESSAGE);
				microManager.dispose();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
					" You have no configuration file. Nothing will happen! "
					+ "\n To change this, click on \"Configuration\" next to "
					+ "\"MicroRemote\" in the plugins-menu.",
					" No Configuration",
					JOptionPane.ERROR_MESSAGE);
			map = new HashMap<Integer,String[]>();
		}
	}

	/**
	 * Map works like this: Integer is the Number of the input device: 0 - 13 ->
	 * 0 - 9 -- 13:LED BigSteps , 12: LED MedSteps , 11:LED SmallSteps,
	 * 10:StepsBtn A0 - A5 -> 10 - 15
	 * 
	 * String Array: 0: 0Function / 1CertainChannel / 2Channel+ / 3Channel - /
	 * 4CertainProp / 5PropStep / 6PropDynamic 
	 * Function: 1 Function
	 * CertainChannel: 1 GroupName 2 ChannelName 
	 * Channel +/-: 1 GroupName
	 * CertainProp: 1 DeviceName(label) 2 PropertyName 3 Value 
	 * PropUp/Down: 1 DeviceName(label) 2 PropertyName 3 SmallValue 4 MedValue 5 BigValue
	 * PropDynamic: 1 DeviceName(label) 2 PropertyName 3 MinValue 4 MaxValue
	 * 
	 * Values SenDed: ButtonMapValue*1000 + (Value if Analog) value geHt vOn 0 -
	 * MESSGAGECAP in Constants
	 * 
	 * The Transmission Code:
	 * Short Array:
	 * 2 BtnNR
	 * 3 Value
	 */
	public void update(Observable object, Object signalObject) {
		String message = "1";
		message = ((String[])signalObject)[0];
		//If Message equals("") then that is the initial message that isnt heard. to start things
		if(map.isEmpty() && !message.equals("")){
			reload();
		}
		else{
			HashMap<Integer, String[]> mappings = map;
			// if you get a signal convert it with the hashmap
			//SignalObject should be a String Array
			int commandInt = -1;
			int buttonNR = -1;
			String signal = "";
			try {
				signal = ((String[])signalObject)[0];
				buttonNR = Integer.parseInt(signal);
				if(initTimeSaver+initTime > System.currentTimeMillis()){
					blockedPins[buttonNR]++;
				}
				if(!(blockedPins[buttonNR]>1)){
					String btnID = Constants.IDPREFIX + buttonNR;
					ArdWindow.println(btnID);
				}
			} catch (Exception e2) {
				LogStreamer.write(e2.getMessage() + "\n");
				ArdWindow.println("Invalid Input. Broken hardware.");
				// make Signal a Number if it isn't
				signal = "1";
			}
			try {
				// Retrieve Function of Button from HashMap
				commandInt = Integer.parseInt(mappings.get(buttonNR)[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String[] args = mappings.get(buttonNR);
			switch (commandInt) {
			case -1:
				break;
			case Constants.FUNCTION:
				String function = args[1];
				if (function.toLowerCase().equals(Constants.FUNCTIONSTRINGSNAP)) {
					microManager.snapImage();
				}
				if (function.toLowerCase().equals(Constants.FUNCTIONSTRINGLIVE)) {
					microManager.live();
				}
				if(function.toLowerCase().equals(Constants.FUNCTIONSTRINGSTEPCHANGE)){
					if(stepSize == BIGSTEPS)
						stepSize = SMALLSTEPS;
					else
						stepSize++;
				}
				if(function.toLowerCase().equals(Constants.FUNCTIONSTRINGSCRIPT)){
					File file =new File(args[2]);
					/*String script = "";
					try {
						BufferedReader br = new BufferedReader(new FileReader(file));
						String line = "";
						while((line = br.readLine())!= null){
							script += line;
						}
						br.close();
					} catch (Exception e) {
						LogStreamer.write(e.getMessage() + "\n");
					}
					microManager.executeBsh(script);*/
					microManager.executeBsh("source("+file.getAbsolutePath()+");");
				}
				if (function.toLowerCase().equals(Constants.FUNCTIONSTRINGSHUTTER)) {
					microManager.toggleShutter();
				}
				if (function.toLowerCase().equals(Constants.FUNCTIONSTRINGAUTOSHUTTER)) {
					microManager.toggleAutoShutter();
				}
				break;
			case Constants.CERTAINCHANNEL:
				microManager.setConfig(args[1], args[2]);
				break;
			case Constants.CHANNELPLUS:
				microManager.goUpConfig(args[1]);
				break;
			case Constants.CHANNELMINUS:
				microManager.goDownConfig(args[1]);
				break;
			case Constants.CERTAINPROP:
				microManager.setProperty(args[1], args[2], args[3]);
				break;
			case Constants.PROPSTEP:
				double stepVal = 0;
				if(stepSize == 16){
					stepVal = Double.parseDouble(args[3]);
				}
				else if(stepSize == 17){
					stepVal = Double.parseDouble(args[4]);
				}
				else if(stepSize == 18){
					stepVal = Double.parseDouble(args[5]);
				}
				microManager.stepProperty(args[1], args[2], stepVal);
				break;
			/* obsolete code
			  case Constants.PROPDYNAMIC:
				// input Value
				try{
					boolean wasLive = false;
					if(microManager.isLiveModeOn()){
						microManager.live();
						wasLive = true;
					}
					int valueSignal = Integer.parseInt(signal[3]);
					// border handling
					if (valueSignal <= Constants.CAPROUNDAMOUNT)
						valueSignal = 0;
					else if (valueSignal >= Constants.MESSAGECAP
							- Constants.CAPROUNDAMOUNT)
						valueSignal = Constants.MESSAGECAP;
					double factor = Double.parseDouble(args[4])
							- Double.parseDouble(args[3]);
					factor /= Constants.MESSAGECAP;
					// output Value
					double valueMM = (valueSignal * factor) + Double.parseDouble(args[3]);
					microManager.setProperty(args[1], args[2], "" + valueMM);
					if(!microManager.isLiveModeOn() && wasLive){
						microManager.live();
					}
				}
				catch(Exception e){
					LogStreamer.write(e.getMessage() + "\n");
				}
				break;*/
			case BIGSTEPS:
				stepSize = BIGSTEPS - STEPCOMPENSATIONVALUE;
				break;
			case MEDSTEPS:
				stepSize = MEDSTEPS - STEPCOMPENSATIONVALUE;
				break;
			case SMALLSTEPS:
				stepSize = SMALLSTEPS - STEPCOMPENSATIONVALUE;
				break;
			}
	
		}
	}
}
