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

package global.meta;

import global.util.PasswGen;

import java.util.Random;

public class Constants {
	public static final int MESSAGECAP = 900;
	// so that if you send MESSAGECAP - CAPROUNDAMOUNT you get MESSAGECAP same with 0
	public static final int CAPROUNDAMOUNT = 4;
	
	public static final int PORTNUMBER = 4322;

	public static final int FUNCTION = 0;
	public static final int CERTAINCHANNEL = 1;
	public static final int CHANNELPLUS = 2;
	public static final int CHANNELMINUS = 3;
	public static final int CERTAINPROP = 4;
	public static final int PROPSTEP = 5;
	public static final int PROPDYNAMIC = 6;
	
	//TODO change this:
	public static final int PINNUMBERDIG = 14;
	//Ignore this
	public static final int PINNUMBERANAL = 0;
	
	public static final String DATATYPE = ".ar";
	
	//should not be lower than 0x250000
	public static final int COLOR_ERROR = 0xbb0000;
	public static final int COLOR_ERRORTEXT = 0xffff00;
	
	public static final boolean ISARDWINDOWVISIBLE = false;
	
	public static final String CONFFILENAME = System.getProperty("user.dir")+"/mmplugins/MicroRemote/config";
	public static final String WORKINGDIR = System.getProperty("user.dir");
	public static final String BSHDIR = System.getProperty("user.dir")+"/plugins/Micro-Manager";

	public static final String[] DRIVERLIST = {"Android","Arduino"};
	
	public static final String DETECTKEYWORD = "Auto Detect";
	
	public static String TITLE_MAINWINDOW = "µRemote-Control";
	public static String TITLE_CONFIGWINDOW = "µRemote-Config";
	
	public static final String FUNCTIONSTRINGSNAP = "snap";
	public static final String FUNCTIONSTRINGLIVE = "live";
	public static final String FUNCTIONSTRINGSTEPCHANGE = "step change";
	public static final String FUNCTIONSTRINGSCRIPT = "script";
	public static final String FUNCTIONSTRINGSHUTTER ="toggle shutter";
	public static final String FUNCTIONSTRINGAUTOSHUTTER ="auto shutter";
	
	public static final String METHODCONFIGUP = "Preset Next";
	public static final String METHODCONFIGDOWN = "Preset Previous";
	public static final String METHODCONFIGCERTAIN = "Certain Preset";
	public static final String MEtHODPROPCERTAIN = "Certain Property";
	public static final String METHODPROPSTEP = "Property Step";
	public static final String METHODFUNCTION = "Function";
	
	public static final String[] METHODBOXSTRINGS = {METHODFUNCTION,METHODCONFIGCERTAIN,METHODCONFIGUP,METHODCONFIGDOWN
		,MEtHODPROPCERTAIN,METHODPROPSTEP};
	
	public static final String IDPREFIX = "Input ";
	
	public static final String CONFIGLOGFILE = System.getProperty("user.dir")+"/mmplugins/MicroRemote/config_Errorlog.txt";
	public static final String PLUGINLOGFILE = System.getProperty("user.dir")+"/mmplugins/MicroRemote/main_Errorlog.txt";
	public static final String ICONPATH = "icon.png";
	
	private static final Random RANDOM = new Random();
	public static final long QRENCODER = System.currentTimeMillis()/Math.abs(1000+(RANDOM.nextInt()/5));
	public static final String QRSEPERATOR = ":";
	
	public static final int PASSWDLENGTH = 16;
	public static String PASSWD = PasswGen.genPasswd(PASSWDLENGTH);
	public static String INITVEC = PasswGen.genPasswd(16);
}
