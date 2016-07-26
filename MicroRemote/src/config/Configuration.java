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

package config;

import global.meta.Constants;
import global.util.Driver;
import global.util.FileHandler;
import global.util.IPgetter;
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
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;

import mmcorej.CMMCore;

import org.micromanager.api.MMPlugin;
import org.micromanager.api.ScriptInterface;

import drivers.AndroidDriver;
import drivers.ArduinoDriver;

public class Configuration implements MMPlugin, Observer {
	
	public static String menuName = "Configuration";
	public static String tooltipDescription = "Map the inputs of your controlling "
										+ "device to the functions of MicroManager";

	private CMMCore core_;
	private ScriptInterface gui_;
	Driver driver;
	ArdWindow logWindow = new ArdWindow();
	ConfigGui im;
	
	@Override
	public String getCopyright() {
		return "Bernard Jollans, 2014";
	}

	@Override
	public String getDescription() {
		return tooltipDescription;
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public void dispose() {
		StartChecker.requestClose();
		im.dispose();
		try{
			driver.dispose();
		}
		catch(Exception e){
			
		}
		logWindow.dispose();
		LogStreamer.close();
	}

	private class IPTitleThread extends Thread{
		public void run(){
			im.setTitle(Constants.TITLE_CONFIGWINDOW + " --- IP: loading...");
			im.setTitle(Constants.TITLE_CONFIGWINDOW + " --- IP: " +IPgetter.getPublicIP());
		}
	}
	
	
	@Override
	public void setApp(ScriptInterface app) {
		if(StartChecker.requestStart(this.getClass().toString())){
			initDriver();
			LogStreamer.startStream(Constants.CONFIGLOGFILE);
			gui_ = app;
			core_ = app.getMMCore();
			ScriptInterfaceWrapper.initialize(gui_, core_);
			logWindow.setVisible(Constants.ISARDWINDOWVISIBLE);
			if(im == null || !im.isVisible()){
				im = new ConfigGui();
				im.addWindowListener(new WindowAdapter(){
					@Override
		            public void windowClosing(WindowEvent e) {
						dispose();
					}
				});
				IPTitleThread iptt = new IPTitleThread();
				iptt.start();
				im.setVisible(true);
			}
			InputStream imgStream = Constants.class.getResourceAsStream(Constants.ICONPATH);
			BufferedImage img;
			try {
				img = ImageIO.read(imgStream);
				im.setIconImage(img);
				logWindow.setIconImage(img);
			} catch (IOException e1) {}
			driver.initialize();
			driver.addObserver(this);
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
		String driverString;
		try{
			driverString = map.get(-2)[0];
		}
		catch(Exception e){
			driverString = Constants.DRIVERLIST[0];
		}
		
		if(driverString.equals(Constants.DRIVERLIST[0])){
			driver = new AndroidDriver();
		}
		else if(driverString.equals(Constants.DRIVERLIST[1])){
			driver = new ArduinoDriver(map.get(-1).equals(Constants.DETECTKEYWORD));
		}
		else
			driver = new AndroidDriver();
		
	}

	@Override
	public void show() {
		
	}

	@Override
	public void update(Observable object, Object signalObject) {
		int buttonNR = -1;
		String signal = "";
		try {
			signal = ((String[]) signalObject)[0];
			buttonNR = Integer.parseInt(signal);
				String btnID = Constants.IDPREFIX + buttonNR;
				ArdWindow.println(btnID);
				im.setLastPressedBtn(btnID);
		} catch (Exception e2) {
			LogStreamer.write(e2.getMessage() + "\n");
			ArdWindow.println("invalid Input");
		}
	}

}
