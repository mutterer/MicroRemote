/*
 * Copyright (C) 2015 Bernard Jollans
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *   
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You can find a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.filechooser.FileFilter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class Installer {

	private JFrame frmremoteInstaller;
	private JTextField txtBrowse;
	
	public static final String NAME = "MicroRemote";
	public static final String DONETEXT = "Done";
	public static String MMPATH = "";
	JFileChooser fmAR = new JFileChooser();
	final JButton btnBrowse = new JButton("browse");
	JLabel lblErrorNull = new JLabel("File does not exist");
	JLabel lblErrorBroken = new JLabel("MicroManager version broken");
	JLabel lblPleaseChooseThe = new JLabel("Please choose the location where you have installed MicroManager");
	JLabel lblAccesDenied = new JLabel("Acces Denied");
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Installer window = new Installer();
					window.frmremoteInstaller.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Installer() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmremoteInstaller = new JFrame();
		frmremoteInstaller.setTitle("\u00B5Remote Installer");
		frmremoteInstaller.setBounds(100, 100, 434, 195);
		frmremoteInstaller.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmremoteInstaller.getContentPane().setLayout(null);
		
		InputStream imgStream = Installer.class.getResourceAsStream("icon.png");
		BufferedImage img;
		try {
			img = ImageIO.read(imgStream);
			frmremoteInstaller.setIconImage(img);
		} catch (IOException e1) {}
		
		lblPleaseChooseThe.setBounds(23, 10, 385, 32);
		frmremoteInstaller.getContentPane().add(lblPleaseChooseThe);
		
		txtBrowse = new JTextField();
		txtBrowse.setBounds(10, 54, 293, 20);
		frmremoteInstaller.getContentPane().add(txtBrowse);
		txtBrowse.setColumns(10);
		
		fmAR.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Directories";
			}
			@Override
			public boolean accept(File f) {
				if(f.isDirectory())
					return true;
				return false;
			}
		});
		fmAR.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				lblAccesDenied.setVisible(false);
				int value = fmAR.showOpenDialog(btnBrowse);
				if(value == JFileChooser.APPROVE_OPTION){
					File file = fmAR.getSelectedFile();
					lblErrorNull.setVisible(false);
					lblErrorBroken.setVisible(false);
					MMPATH = file.getAbsolutePath();
					txtBrowse.setText(MMPATH);
				}
			}
		});
		btnBrowse.setBounds(313, 53, 89, 23);
		frmremoteInstaller.getContentPane().add(btnBrowse);
		
		JButton btnInstall = new JButton("install");
		btnInstall.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				lblAccesDenied.setVisible(false);
				JButton thisBtn = (JButton)arg0.getSource();
				if(thisBtn.getText().equals(DONETEXT)){
					System.exit(0);
				}
				String mmPath = txtBrowse.getText();
				File mmFolder = new File(mmPath);
				File mmpluginsFolder = new File(mmPath+"/mmplugins");
				File mmjarsFolder = new File(mmPath+"/plugins/Micro-Manager");
				if(mmFolder.exists()){
					lblErrorNull.setVisible(false);
					if(mmjarsFolder.exists() && mmpluginsFolder.exists()){
						lblErrorBroken.setVisible(false);
						//Installing
						thisBtn.setText("Installing...");
						new File(mmPath+"/mmplugins/"+NAME).mkdirs();
						copyFileFromResource(NAME+".jar", mmPath+"/mmplugins/"+NAME+"/"+NAME+".jar");
						copyFileFromResource("rxtx-2.2.jar", mmPath+"/plugins/Micro-Manager/rxtx-2.2.jar");
						copyFileFromResource("rxtxParallel.dll", mmPath+"/rxtxParallel.dll");
						copyFileFromResource("rxtxSerial.dll", mmPath+"/rxtxSerial.dll");
						copyFileFromResource("librxtxSerial.jnilib", mmPath+"/librxtxSerial.jnilib");
						copyFileFromResource("qrgen-1.2.jar", mmPath+"/plugins/Micro-Manager/qrgen-1.2.jar");
						copyFileFromResource("zxing-1.7-core.jar", mmPath+"/plugins/Micro-Manager/zxing-1.7-core.jar");
						copyFileFromResource("zxing-1.7-javase.jar", mmPath+"/plugins/Micro-Manager/zxing-1.7-javase.jar");
						txtBrowse.setEnabled(false);
						btnBrowse.setEnabled(false);
						lblPleaseChooseThe.setVisible(false);
						thisBtn.setText(DONETEXT);
					}
					else{
						lblErrorBroken.setVisible(true);
					}
				}
				else{
					lblErrorNull.setVisible(true);
				}
			}
		});
		btnInstall.setBounds(253, 115, 155, 32);
		frmremoteInstaller.getContentPane().add(btnInstall);
		
		
		lblErrorNull.setVisible(false);
		lblErrorNull.setBounds(20, 85, 117, 14);
		frmremoteInstaller.getContentPane().add(lblErrorNull);
		
		lblErrorBroken.setVisible(false);
		lblErrorBroken.setBounds(23, 85, 171, 14);
		frmremoteInstaller.getContentPane().add(lblErrorBroken);

		lblAccesDenied.setVisible(false);
		lblAccesDenied.setBounds(20, 85, 117, 14);
		frmremoteInstaller.getContentPane().add(lblAccesDenied);
	}
	public void copyFileFromResource(String res,String aimPath){
		InputStream in = Installer.class.getClassLoader().getResourceAsStream(res);
			FileOutputStream out = null;
			try {
			    out = new FileOutputStream(aimPath);
			    byte[] buf = new byte[2048];
			    int read = in.read(buf);
			    while(read != -1) {
			        out.write(buf, 0, read);
			        read = in.read(buf);
			    }
			} catch (FileNotFoundException e) {
				lblAccesDenied.setVisible(true);
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			        try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			 }
			
	}
}
