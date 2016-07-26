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

import global.meta.Constants;
import global.util.CryptUtil;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class AndroidDriver extends global.util.Driver{

	private ListenerThread listener;
	ServerSocket serverSocket;
	Socket clientSocket;
	 DataInputStream in;
	 PrintWriter out;
	 boolean flag = true;
	 private String acceptedIP; 
	
	private class ListenerThread extends Thread{
		private int portNumber;
		private ListenerThread listener2;
		
		public ListenerThread(int portNumber){
			this.portNumber = portNumber;
		}
		public void run(){
			try {
					listener = this;
		            serverSocket =
		                new ServerSocket(portNumber);
		            clientSocket = serverSocket.accept();
		            System.out.println(clientSocket.getInetAddress().toString());
		            if(acceptedIP == null){
		            	acceptedIP=clientSocket.getInetAddress().toString();
		            }
		            if(acceptedIP.equals(clientSocket.getInetAddress().toString())){
			            in = new DataInputStream(clientSocket.getInputStream());
			            out =
				                new PrintWriter(clientSocket.getOutputStream(), true);  
	
			            int length = in.readInt();
			            byte[] bA = new byte[length];
			            String[] msg = new String[bA.length];
			            for(int i = 0; i < length; i++){
			            	bA[i] = (byte)in.read();
			            }
			            CryptUtil crypter = new CryptUtil(); 
			            msg =(crypter.decrypt(bA)).split(Constants.QRSEPERATOR);
			            try{
			            	@SuppressWarnings("unused")
							int x = Integer.parseInt(msg[0]);
			            	omitMessage(msg[0]);
			            }
			            catch(Exception e){
			            	//TODO Blacklist IP
			            }
		            }
	            	end();
	            	if(flag){
		            	listener2 = new ListenerThread(portNumber);
		            	listener2.start();
	            	}
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
			return;
		}
		public void end(){
			
			try{
	        	in.close();
			}
			catch(Exception e){
				
			}
			try{
	        	out.close();
			}
			catch(Exception e){
				
			}		
			try{
	        	clientSocket.close();
	        	clientSocket = null;
			}
			catch(Exception e){
			}
			try{
	        	serverSocket.close();
	        	serverSocket = null;
			}
			catch(Exception e){
				
			}
			try{
				listener2.interrupt();
				listener2 = null;
			}
			catch(NullPointerException npe){
			}
		}
	}
	
	@Override
	public void initialize(){
		int portNumber = Constants.PORTNUMBER;
		listener = new ListenerThread(portNumber);
		listener.start();
	}
	

	@Override
	public void dispose(){
		flag = false;
		listener.end();
		listener.interrupt();
	}
    
}