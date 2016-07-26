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
import global.util.Driver;
import global.util.FileHandler;
import global.util.LogStreamer;
import global.util.ScriptInterfaceWrapper;
import global.util.StartChecker;
import global.windows.ArdWindow;
import global.windows.StartErrorDialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import mmcorej.CMMCore;

import org.micromanager.acquisition.AcquisitionEngine;
import org.micromanager.api.MMPlugin;
import org.micromanager.api.ScriptInterface;
import org.micromanager.MMStudio;

import drivers.*;
import bsh.EvalError;
import bsh.Interpreter;

public class MainPlugin implements MMPlugin {
	public static String menuName = "Start MicroRemote";
	public static String tooltipDescription = "Click here to start controlling "
								+ "MicroManager with your controller device.";
	private CMMCore core_;
	private ScriptInterface gui_;
	private AcquisitionEngine acq_;
	private ArdWindow logWindow;
	private MainWindow window;
	private Connector serialReciever;
	private Driver driver;
	private Interpreter interpreter = new Interpreter();
	
	private HashMap<String, String> lastConfiguration = new HashMap<String, String>();

	public void setApp(ScriptInterface app) {
		if (logWindow == null)
			logWindow = new ArdWindow();
		logWindow.setVisible(Constants.ISARDWINDOWVISIBLE);
		if(StartChecker.requestStart(this.getClass().toString())){
			LogStreamer.startStream(Constants.PLUGINLOGFILE);
			gui_ = app;
			core_ = app.getMMCore();
			acq_ = ((MMStudio)gui_).getAcquisitionEngine();
			ScriptInterfaceWrapper.initialize(gui_, core_);
			if(window == null || !window.isVisible()){
				window = new MainWindow();
				window.addWindowListener(new WindowAdapter(){
					@Override
		            public void windowClosing(WindowEvent e) {
						dispose();
					}
				});
				window.setTitle(Constants.TITLE_MAINWINDOW);
				window.setVisible(true);
			}
			try {
				interpreter.set("mmc", core_);
				interpreter.set("gui", app);
				interpreter.set("acq", acq_);
				interpreter.set("ard",logWindow);
			} catch (EvalError e1) {
				LogStreamer.write(e1.getMessage() + "\n");
			}
			InputStream imgStream = Constants.class.getResourceAsStream(Constants.ICONPATH);
			BufferedImage img;
			try {
				img = ImageIO.read(imgStream);
				window.setIconImage(img);
				logWindow.setIconImage(img);
			} catch (IOException e1) {}
			serialReciever = new Connector(this);
			initDriver();
			driver.initialize();
			serialReciever.reload();
			driver.addObserver(serialReciever);
			try {
				ArdWindow
						.println(core_.getDevicePropertyNames("Arduino").toArray()[0]);
			} catch (Exception e) {
				LogStreamer.write(e.getMessage() + "\n");
			}
		}
		else{
			StartErrorDialog sted = new StartErrorDialog();
			sted.setVisible(true);
		}
		
	}
	
	private void initDriver(){

		HashMap<Integer,String[]> map;
		FileHandler fh = new FileHandler();
		try {
			map = fh.loadFile(new File(Constants.CONFFILENAME));
		} catch (Exception e) {
			map = new HashMap<Integer,String[]>();
		}
		String driverString = map.get(-2)[0];
		String portString = map.get(-1)[0];
		window.setDriver(driverString, portString);
		
		if(driverString.equals(Constants.DRIVERLIST[0])){
			driver = new AndroidDriver();
		}
		else if(driverString.equals(Constants.DRIVERLIST[1])){
			driver = new ArduinoDriver(map.get(-1).equals(Constants.DETECTKEYWORD));
		}
		else
			driver = new AndroidDriver();
		
	}

	/*
	 * this is just there so that we know how to write things public void
	 * doIt(){ try { core.defineConfig("Temp", "-4", "Camera", "CCDTemperature",
	 * "0"); core.setConfig("Temp","-4"); gui.refreshGUI(); } catch (Exception
	 * e) { e.printStackTrace(); } }
	 */

	public void setProperty(String label, String propName, String propValue) {

		try {
			core_.setProperty(label, propName, propValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		gui_.refreshGUI();
	}

	public void setConfig(String label, String value) {

		try {
			core_.setConfig(label, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		gui_.refreshGUI();
	}

	public void goUpConfig(String label) {
		try{
			String[] configs = core_.getAvailableConfigs(label).toArray();
			String currentConfig = "";
			try {
				currentConfig = core_.getCurrentConfig(label);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(currentConfig == null || currentConfig.equals("")){
				try{
					currentConfig = lastConfiguration.get(label);
				}
				catch(Exception e){
				}
			}
			int configIndex = -3;
			if(currentConfig == null || currentConfig.equals("")){
				configIndex = 0;
			}
			else{
				for (int i = 0; i < configs.length; i++) {
					if (configs[i].equals(currentConfig)) {
						configIndex = i;
						break;
					}
				}
				// If out of bounds
				configIndex++;
			}
			if (configIndex == configs.length) {
				configIndex = 0;
			}
			try {
				lastConfiguration.put(label, configs[configIndex]);
				core_.setConfig(label, configs[configIndex]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch(Exception e){
			
		}
		gui_.refreshGUI();
	}

	public void goDownConfig(String label) {try{
			String[] configs = core_.getAvailableConfigs(label).toArray();
			String currentConfig = "";
			try {
				currentConfig = core_.getCurrentConfig(label);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(currentConfig == null || currentConfig.equals("")){
				try{
					currentConfig = lastConfiguration.get(label);
				}
				catch(Exception e){
				}
			}
			int configIndex = -3;
			if(currentConfig == null || currentConfig.equals("")){
				configIndex = 0;
			}
			else{
				for (int i = 0; i < configs.length; i++) {
					if (configs[i].equals(currentConfig)) {
						configIndex = i;
						break;
					}
				}
				// If out of bounds
				configIndex--;
			}
			if (configIndex < 0) {
				configIndex = configs.length-1;
			}
			try {
				lastConfiguration.put(label, configs[configIndex]);
				core_.setConfig(label, configs[configIndex]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch(Exception e){
			
		}
		gui_.refreshGUI();
	}

	public void stepProperty(String label, String propName, double amount) {
		try {
			double newVal = Double.parseDouble(core_.getProperty(label,
					propName)) + amount;
			if ((newVal) < core_.getPropertyLowerLimit(label, propName)) {
				core_.setProperty(label, propName,
						core_.getPropertyLowerLimit(label, propName));
			} else if (newVal > core_
					.getPropertyUpperLimit(label, propName)) {
				core_.setProperty(label, propName,
						core_.getPropertyUpperLimit(label, propName));
			} else {
				core_.setProperty(
						label,
						propName,
						newVal);
			}
		} catch (NumberFormatException e) {
			LogStreamer.write(e.getMessage());
		} catch (Exception e) {
			LogStreamer.write(e.getMessage());
		}
		gui_.refreshGUI();
	}

	public void snapImage() {
		try {
			gui_.snapSingleImage();
		} catch (Exception e) {
			LogStreamer.write(e.getMessage());
		}
	}

	public void live() {
		if (gui_.isLiveModeOn()) {
			gui_.enableLiveMode(false);
		} else {
			gui_.enableLiveMode(true);
		}
	}
	
	public boolean isLiveModeOn(){
		return gui_.isLiveModeOn();
	}
	
	public void executeBsh(String script){
		try {
			System.setProperty("user.dir", Constants.BSHDIR);
			interpreter.eval(script);
			System.setProperty("user.dir", Constants.WORKINGDIR);
		} catch (EvalError e) {
			ArdWindow.print(e.getMessage()+"\n");
			LogStreamer.write(e.getMessage());
		}
	}
	
	public void toggleShutter() {
		try {
			core_.setShutterOpen(!core_.getShutterOpen());
		} catch (Exception e) {
				LogStreamer.write(e.getMessage()+"\n");
			e.printStackTrace();
		}
		gui_.refreshGUI();
	}
	
	public void toggleAutoShutter() {
		try {
			core_.setAutoShutter(!core_.getAutoShutter());
		} catch (Exception e) {
				LogStreamer.write(e.getMessage()+"\n");
			e.printStackTrace();
		}
		gui_.refreshGUI();
	}

	public void dispose() {
		StartChecker.requestClose();
		logWindow.dispose();
		try{
			driver.dispose();
		}
		catch(Exception e){
		}
		window.dispose();
		LogStreamer.close();
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

	public boolean validateMap(HashMap<Integer, String[]> map) {
		boolean ret = true;
		String[] configGroups = core_.getAvailableConfigGroups().toArray();
		String[] devices = {};
		try {
			devices = core_.getDeviceAdapterNames().toArray();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for(Integer key: map.keySet()){
			if(!ret){
				return ret;
			}
			String[] mapString = map.get(key);
			boolean contains = false;
			int swInt;
			try{
				swInt = Integer.parseInt(mapString[0]);
			}
			catch(Exception e){
				swInt = -1;
			}
			switch(swInt){
					
				case Constants.CERTAINCHANNEL:
					for(int i = 0; i < configGroups.length; i++){
						String[] presets = core_.getAvailableConfigs(configGroups[i]).toArray();
						for(int j = 0; j <presets.length; j++){
							if(contains){
								break;
							}
							contains = presets[i].equals(mapString[2]);
						}
					}
				case Constants.CHANNELMINUS:
				case Constants.CHANNELPLUS:
					for(int i = 0; i < configGroups.length; i++){
						if(contains){
							break;
						}
						contains = configGroups[i].equals(mapString[1]);
					}
					ret = contains;
					break;
				case Constants.CERTAINPROP:
				case Constants.PROPSTEP:
					for(int i = 0; i < devices.length; i++){
						if(contains){
							break;
						}
						contains = devices[i].equals(mapString[1]);
					}
					ret = contains;
					for(int i = 0; i < devices.length; i++){
						if(contains){
							break;
						}
						try {
							contains = core_.hasProperty(mapString[1], mapString[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
			}
		}
		
		return ret;
	}

	
}
