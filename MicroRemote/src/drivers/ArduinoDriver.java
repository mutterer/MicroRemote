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

package drivers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import global.meta.Constants;
import global.meta.DriverInterface;
import global.util.FileHandler;
import global.util.LogStreamer;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.ArrayList;
import java.util.Enumeration;


public class ArduinoDriver extends global.util.Driver implements DriverInterface,SerialPortEventListener{
	SerialPort serialPort;
	private BufferedReader input;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 9600;
	
	private String[] serialInput;

	private Thread statusListenerThread;
	
	private boolean statusWasInterrupted = false;
	
	private boolean autoDetect = false;
	
	private ArrayList<CommPortIdentifier> lastPortIDs = new ArrayList<CommPortIdentifier>(); 
	
	public ArduinoDriver(boolean isAutoDetect){
		autoDetect = isAutoDetect;
	}
	
	@Override
	public void initialize() {

		//Identifies Ports
		CommPortIdentifier portId = null;
		@SuppressWarnings("rawtypes")
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		FileHandler fh = new FileHandler();
		String portIdString = "";
		try {
			portIdString = (String)fh.loadFile(new File(Constants.CONFFILENAME)).get(-1)[0];
		} catch (Exception e1) {
		}

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if (currPortId.getName().equals(portIdString)) {
				portId = currPortId;
				lastPortIDs.add(portId);
				break;
			}
		}

		//First, Find an instance of serial port as set in PORT_NAMES.
		if (portIdString.toLowerCase().equals("detect")) {
			reInitialize();
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));

			// add this as an event listener - Eventhandling is below
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		
		
		
		statusListenerThread=createStatusListenerThread();
		statusListenerThread.start();
//		notifyObservers(serialInput);
		
	}
	
	private void reInitialize(){
		CommPortIdentifier portId = null;
		@SuppressWarnings("rawtypes")
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if (!lastPortIDs.contains(currPortId)) {
				portId = currPortId;
				lastPortIDs.add(portId);
				break;
			}
		}
		
		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));

			// add this as an event listener - Eventhandling is below
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		

		if(autoDetect){
			statusListenerThread=createStatusListenerThread();
			statusListenerThread.start();
		}
	}
	
	private Thread createStatusListenerThread(){
		return new Thread(){
			public void run(){
				try{
					Thread.sleep(5000);
					if(!statusWasInterrupted)
						reInitialize();
				}
				catch(InterruptedException ie){
					Thread.currentThread().interrupt();
					return;
				}
			}
		};
	}
	@Override
	public synchronized void dispose() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			lastPortIDs= new ArrayList<CommPortIdentifier>(); 
		}
	}

	@Override
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				serialInput=input.readLine().split(",");
				serialInput = new String[]{"","",serialInput[0],serialInput[1]};
				if(Integer.parseInt(serialInput[2]) == -1 && autoDetect){
					statusListenerThread.interrupt();
					statusWasInterrupted = true;
					statusListenerThread = createStatusListenerThread();
					statusListenerThread.start();
				}
				else{
					//For Observers
//					setChanged();
					omitMessage(serialInput[2]);
					
				}
			} catch (IOException e1) {
				
			} catch (Exception e) {
				LogStreamer.write(e.toString() + "\n");
			}
		}
		//For Observers
//		notifyObservers(serialInput);
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
}
