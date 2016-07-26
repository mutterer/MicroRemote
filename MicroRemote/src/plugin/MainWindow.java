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
import global.util.IPgetter;
import global.util.QRCodeGenerator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -4616033259673504593L;

	private JPanel contentPane;

	JLabel lblDriver = new JLabel("loading...");
	JLabel lblIP = new JLabel("loading...");
	IPThread ipthread = new IPThread();
	JLabel lblPort = new JLabel("loading...");
	JLabel lblCurrentPort = new JLabel("Port:");
	JLabel lblYourPublcIp = new JLabel("Scan Code");
	JButton btnReload = new JButton("reload");

	public MainWindow() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 322, 243);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblYourPublcIp.setBounds(10, 55, 86, 14);
		contentPane.add(lblYourPublcIp);

		lblIP.setBounds(106, 11, 190, 103);
		contentPane.add(lblIP);
		
		JLabel lblYourCurrentDriver = new JLabel("Current driver:");
		lblYourCurrentDriver.setBounds(10, 125, 86, 14);
		contentPane.add(lblYourCurrentDriver);
		
		lblDriver.setBounds(106, 125, 190, 14);
		contentPane.add(lblDriver);
		
		btnReload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(!ipthread.isAlive()){
					ipthread = new IPThread();
					ipthread.start();
				}
			}
		});
		btnReload.setBounds(10, 171, 63, 23);
		contentPane.add(btnReload);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(10, 146, 46, 14);
		contentPane.add(lblStatus);
		
		JLabel lblRunning = new JLabel("Running");
		lblRunning.setBounds(106, 146, 46, 14);
		contentPane.add(lblRunning);
		
		lblPort.setBounds(106, 55, 172, 14);
		contentPane.add(lblPort);
		
		lblCurrentPort.setBounds(10, 55, 86, 14);
		contentPane.add(lblCurrentPort);
		
	}
	private class IPThread extends Thread{
		public void run(){
			setIPlbl("loading...");
//			setLocalIP(IPgetter.getLocalIP());
			setIP(IPgetter.getPublicIP());
			return;
		}
	}
	
	
	public void setIP(String ip){
		if(ip.startsWith("NO")){
			ipthread = new IPThread();
			ipthread.start();
		}
		else{
			contentPane.remove(lblIP);
			QRCodeGenerator qrg = new QRCodeGenerator(""+Constants.INITVEC+
					Constants.QRSEPERATOR+Constants.PASSWD +
					Constants.QRSEPERATOR+ ip);
			lblIP = new JLabel(new ImageIcon(qrg.getCodeAsImage()));
			lblIP.setBounds(106, 11, 190, 103);
			lblIP.setVisible(true);
			contentPane.add(lblIP);
			contentPane.repaint();
		}
	}
	
	private void setIPlbl(String text){
		contentPane.remove(lblIP);
		lblIP = new JLabel(text);
		lblIP.setBounds(106, 11, 190, 103);
		lblIP.setVisible(true);
		contentPane.add(lblIP);
		contentPane.repaint();
	}
	
	public void setDriver(String driver,String port){
		lblDriver.setText(driver);
		lblPort.setText(port);
		if(driver.equals(Constants.DRIVERLIST[1])){
			lblYourPublcIp.setVisible(false);
			lblIP.setVisible(false);
			btnReload.setVisible(false);
			btnReload.setEnabled(false);
			lblPort.setVisible(true);
			lblCurrentPort.setVisible(true);
		}
		else{
			lblYourPublcIp.setVisible(true);
			lblIP.setVisible(true);
			btnReload.setVisible(true);
			btnReload.setEnabled(true);
			lblPort.setVisible(false);
			lblCurrentPort.setVisible(false);
			ipthread.start();
		}
	}
}
