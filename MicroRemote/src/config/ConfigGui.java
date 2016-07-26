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

import global.meta.ConfigGUIInterface;
import global.meta.Constants;
import global.util.FileHandler;
import global.util.ScriptInterfaceWrapper;
import global.windows.ArdWindow;
import gnu.io.CommPortIdentifier;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import java.awt.Color;
import java.awt.GridLayout;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;

public class ConfigGui extends JFrame implements ConfigGUIInterface{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panel;
	
	public UndoRedoHandler undrdh = new UndoRedoHandler();

	ArrayList<JComboBox> methodBoxDigList;
	ArrayList<JComboBox> deviceGroupFunctionBoxDigList;
	ArrayList<JComboBox> propChanBoxDigList;
	ArrayList<JTextField> smValueFieldDigList;
	ArrayList<JTextField> medValueFieldDigList;
	ArrayList<JTextField> bigValueFieldDigList;
	ArrayList<JTextField> pinLblList;
	ArrayList<JButton> dltBtnList;
	JButton applyButton;
	JButton okButton;
	JButton closeButton;
	ArrayList<JLabel> statLblDigList;
	JLabel[] lblDig;
	JButton saveBtn;
	JButton loadBtn;
	boolean isLoading = false;
	JButton addBtn;
	JButton newBtn;
	
	JButton portOKBtn;
	JComboBox portBox;
	JLabel portLabel;
	
	JFileChooser fmAR = new JFileChooser();
	JFileChooser fmBSH = new JFileChooser();
	FileHandler fh = new FileHandler();

	JLabel lblDriver;
	JComboBox driverBox;
	
	boolean lastConfigValid = true;

	//JButton[] okBtnAnal = new JButton[Constants.PINNUMBERANAL];
	private final String BTNSTRINGAPPLY = "Apply";
	private final String BTNSTRINGAPPLIED = "Applied";
	private final String BTNSTRINGOK = "OK";
	private final String BTNSTRINGCLOSE = "Close";
	private final String BTNSTRINGADD = "Add Input";
	private final String BTNSTRINGNEW = "New Config";
	private final String BTNSTRINGDEL = "X";

	private final int LINECOLINT = 1;
	private final int BTNCOLINT = LINECOLINT+9;
	
	private final String PORTLABELTEXT = "Default Port";
	
	private final String[] functions = new String[] { Constants.FUNCTIONSTRINGSNAP, 
			Constants.FUNCTIONSTRINGLIVE, Constants.FUNCTIONSTRINGSTEPCHANGE, Constants.FUNCTIONSTRINGSCRIPT, 
			Constants.FUNCTIONSTRINGSHUTTER, Constants.FUNCTIONSTRINGAUTOSHUTTER};

	HashMap<Integer, String[]> map;
	HashMap<Integer, String[]> mapTemp;

	private static final int FIRSTBLOCKSTART = 2;
	private JTextField txtFLastPressedBtn;
	
	ItemListener itemListener =new ItemListener(){
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			map = new HashMap<Integer, String[]>();
			lastConfigValid = true; 
			map.put(-1, new String[]{(String)portBox.getSelectedItem()});
			
			for(int index = 0; index < methodBoxDigList.size(); index++){
				int method = methodBoxDigList.get(index).getSelectedIndex();
				String inputVal = smValueFieldDigList.get(index).getText();
				String device = (String) deviceGroupFunctionBoxDigList.get(index)
						.getSelectedItem();
				String prop = (String) propChanBoxDigList.get(index)
						.getSelectedItem();
				String[] mapString = new String[] {};
				switch (method) {

				case Constants.FUNCTION:
					String function = (String) deviceGroupFunctionBoxDigList.get(index)
							.getSelectedItem();
					mapString = new String[] { "" + method, function };
					break;
				case Constants.CERTAINCHANNEL:
					mapString = new String[] { "" + method, device,
							prop };
					break;
				case Constants.CHANNELPLUS:
					mapString = new String[] { "" + method, device };
					break;
				case Constants.CHANNELMINUS:
					mapString = new String[] { "" + method, device };
					break;
				case Constants.CERTAINPROP:
					mapString = new String[] { "" + method, device,
							prop, inputVal };
					break;
				case Constants.PROPSTEP:
						mapString = new String[] { "" + method, device,
								prop,
								"" + smValueFieldDigList.get(index).getText(),
								"" + medValueFieldDigList.get(index).getText(),
								"" + bigValueFieldDigList.get(index).getText() };
					break;
				}
				int inputNr;
				try{
					inputNr = Integer.parseInt(pinLblList.get(index).getText());
					pinLblList.get(index).setBackground(new Color(0xffffff));
					pinLblList.get(index).setForeground(new Color(0x000000));
				}
				catch(Exception e){
					inputNr = -1;
				}
				map.put(inputNr, mapString);
			}
			undrdh.actionPerformed(map);
			guiToMap();
		}
	};
	DocumentListener documentListener = new DocumentListener() {

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			run();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			run();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			run();
		}
		
		private void run(){
			map = new HashMap<Integer, String[]>();
			lastConfigValid = true; 
			map.put(-1, new String[]{(String)portBox.getSelectedItem()});
			
			for(int index = 0; index < methodBoxDigList.size(); index++){
				int method = methodBoxDigList.get(index).getSelectedIndex();
				String inputVal = smValueFieldDigList.get(index).getText();
				String device = (String) deviceGroupFunctionBoxDigList.get(index)
						.getSelectedItem();
				String prop = (String) propChanBoxDigList.get(index)
						.getSelectedItem();
				String[] mapString = new String[] {};
				switch (method) {

				case Constants.FUNCTION:
					String function = (String) deviceGroupFunctionBoxDigList.get(index)
							.getSelectedItem();
					mapString = new String[] { "" + method, function };
					break;
				case Constants.CERTAINCHANNEL:
					mapString = new String[] { "" + method, device,
							prop };
					break;
				case Constants.CHANNELPLUS:
					mapString = new String[] { "" + method, device };
					break;
				case Constants.CHANNELMINUS:
					mapString = new String[] { "" + method, device };
					break;
				case Constants.CERTAINPROP:
					mapString = new String[] { "" + method, device,
							prop, inputVal };
					break;
				case Constants.PROPSTEP:
						mapString = new String[] { "" + method, device,
								prop,
								"" + smValueFieldDigList.get(index).getText(),
								"" + medValueFieldDigList.get(index).getText(),
								"" + bigValueFieldDigList.get(index).getText() };
					break;
				}
				int inputNr;
				try{
					inputNr = Integer.parseInt(pinLblList.get(index).getText());
					pinLblList.get(index).setBackground(new Color(0xffffff));
					pinLblList.get(index).setForeground(new Color(0x000000));
				}
				catch(Exception e){
					inputNr = -1;
				}
				map.put(inputNr, mapString);
			}
			undrdh.actionPerformed(map);
			guiToMap();
		}
		
	};

	public ConfigGui() {
		map = new HashMap<Integer, String[]>();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 1107, 699);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		

		panel = new JPanel();
		
		panel.getActionMap().put("control z", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				ArdWindow.println("control z");
				HashMap<Integer, String[]> helpMap =undrdh.actionUndo();
				if(helpMap != null)
					map = helpMap;
				txtFLastPressedBtn.setText("z");
            }
        });
		panel.getActionMap().put("control y", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				ArdWindow.println("control y");
				HashMap<Integer, String[]> helpMap =undrdh.actionRedo();
				if(helpMap != null)
					map = helpMap;
				txtFLastPressedBtn.setText("y");
            }
        });
		panel.getActionMap().put("refresh", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
            }
        });
		InputMap inputMap = panel.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke(Character.valueOf((char)0x1a), 0), "control z");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK ), "control y");
		
		contentPane.add(panel);
		panel.setLayout(new MigLayout(
				"",
				"[][35.00][82.00,grow][grow][][]",
				"[grow][24.00][24.00][24.00][24.00][24.00][24.00][24.00][24.00][24.00][][24.00][24.00][24.00][24.00][24.00][24.00][24.00][24.00]"));
		
		fmBSH.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "BeanShell Scripts";
			}
			@Override
			public boolean accept(File f) {
				if(f.getName().toLowerCase().endsWith(".bsh")|| f.isDirectory())
					return true;
				return false;
			}
		});
		fmAR.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Arduino Remote Files";
			}
			@Override
			public boolean accept(File f) {
				if(f.getName().toLowerCase().endsWith(Constants.DATATYPE)|| f.isDirectory())
					return true;
				return false;
			}
		});
		saveBtn = new JButton("Save Config");
		saveBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int value = fmAR.showSaveDialog(ConfigGui.this);
				if(value == JFileChooser.APPROVE_OPTION){
					File file = fmAR.getSelectedFile();
					fh.saveFile(map,file);
				}
			}
		});
		
		JLabel lblLastPressedButton = new JLabel("Last InputNr:");
		panel.add(lblLastPressedButton, "flowx,cell "+(LINECOLINT+1)+" 0");
		panel.add(saveBtn,"cell "+BTNCOLINT+" 2");
		saveBtn.setVisible(true);
		

		addBtn = new JButton(BTNSTRINGADD);
		addBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				addNewLine();
			}
		});
		panel.add(addBtn,"cell "+BTNCOLINT+" 5");
		addBtn.setVisible(true);
		
		newBtn = new JButton(BTNSTRINGNEW);
		newBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				newConfig();
			}
		});
		panel.add(newBtn,"cell "+BTNCOLINT+" 6");
		newBtn.setVisible(true);
		
		loadBtn = new JButton("Load Config");
		loadBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				int value = fmAR.showOpenDialog(ConfigGui.this);
				if(value == JFileChooser.APPROVE_OPTION){
					File file = fmAR.getSelectedFile();
					try {
						map = fh.loadFile(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
					isLoading = true;
					guiToMap();
				}
			}
		});
		panel.add(loadBtn,"cell "+BTNCOLINT+" 3");
		loadBtn.setVisible(true);
		
		portLabel = new JLabel(PORTLABELTEXT);
		panel.add(portLabel,"cell 8 0");
		{
			@SuppressWarnings("rawtypes")
			Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
			ArrayList<String> portIDs = new ArrayList<String>();
			portIDs.add(Constants.DETECTKEYWORD);
			while(portEnum.hasMoreElements()){
				CommPortIdentifier element = (CommPortIdentifier)portEnum.nextElement();
				portIDs.add(element.getName());
			}
			
			portBox = new JComboBox(portIDs.toArray());
			portBox.setVisible(false);
			portLabel.setVisible(false);
			
		}
		portBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent item) {
				setApplied(false);
			}
		});
		panel.add(portBox,"cell 9 0,growx,span 2");
		
		lblDriver = new JLabel("Selected Driver");
		panel.add(lblDriver,"cell 4 0");
		driverBox = new JComboBox(Constants.DRIVERLIST);
		driverBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent item) {
				JComboBox box = (JComboBox)item.getSource();
				if(box.getSelectedItem().equals(Constants.DRIVERLIST[1])){
					portBox.setVisible(true);
					panel.remove(portLabel);
					panel.revalidate();
					panel.repaint();
					portLabel = new JLabel(PORTLABELTEXT);
					panel.add(portLabel,"cell 8 0");
					portLabel.setVisible(true);
				}
				else{
					portBox.setVisible(false);
					portLabel.setVisible(false);
//					IPThread ipt = new IPThread();
//					ipt.start();
				}
				setApplied(false);
			}
			
		});
		panel.add(driverBox,"cell 5 0,growx,span 2");
		
		txtFLastPressedBtn = new JTextField();
		txtFLastPressedBtn.setEnabled(false);
		panel.add(txtFLastPressedBtn, "cell "+(LINECOLINT+1)+" 0");
		txtFLastPressedBtn.setColumns(10);
		
		/**
		 * Pin Part Initialization
		 */


		/**
		 * Digial Initialization
		 */
		methodBoxDigList = new ArrayList<JComboBox>();

		deviceGroupFunctionBoxDigList = new ArrayList<JComboBox>();

		pinLblList = new ArrayList<JTextField>();
		propChanBoxDigList = new ArrayList<JComboBox>();
		smValueFieldDigList = new ArrayList<JTextField>();
		medValueFieldDigList = new ArrayList<JTextField>();
		bigValueFieldDigList = new ArrayList<JTextField>();
		dltBtnList = new ArrayList<JButton>();
		lblDig = new JLabel[7];
		lblDig[0] = new JLabel(" InputNr ");
		lblDig[1] = new JLabel(" Method ");
		lblDig[2] = new JLabel(" Device/Group ");
		lblDig[3] = new JLabel(" Property/Channel ");
		lblDig[4] = new JLabel(" (Small) Value ");
		lblDig[5] = new JLabel(" Medium Value ");
		lblDig[6] = new JLabel(" Big Value ");
		for (int i = 0; i < lblDig.length; i++) {
			lblDig[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			panel.add(lblDig[i], "cell " + (LINECOLINT+ i+1) + " "
					+ (FIRSTBLOCKSTART));
		}
		
		applyButton = new JButton(BTNSTRINGAPPLY);
			applyButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					apply();
				}
			});
			panel.add(applyButton, "cell "+BTNCOLINT+" " + "8" + ",growx");
			applyButton.setVisible(true);
		
		okButton = new JButton(BTNSTRINGOK);
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				apply();
				JButton b = (JButton)arg0.getSource();
				ConfigGui w = (ConfigGui)SwingUtilities.getRoot(b);
				w.dispatchEvent(new WindowEvent(w,WindowEvent.WINDOW_CLOSING));
			}
		});
//		panel.add(okButton, "cell 5 " + (Constants.PINNUMBERDIG+SECONDBLOCKSTART+1) + ",growx");
//		okButton.setVisible(true);
		
		closeButton = new JButton(BTNSTRINGCLOSE);
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JButton b = (JButton)arg0.getSource();
				ConfigGui w = (ConfigGui)SwingUtilities.getRoot(b);
				w.dispatchEvent(new WindowEvent(w,WindowEvent.WINDOW_CLOSING));
			}
		});
		panel.add(closeButton, "cell "+BTNCOLINT+" " + "9" + ",growx");
		closeButton.setVisible(true);

		try {
			map = fh.loadFile(new File(Constants.CONFFILENAME));
		} catch (Exception e) {
			e.printStackTrace();
		}
		guiToMap();
			
		}
		
	
	/**
	 * this is Arduino Uno specific
	 */
	
	private void newConfig(){
		int size = methodBoxDigList.size();
		for(int i = 0; i < size; i++){
			methodBoxDigList.get(i).setVisible(false);
			panel.remove(methodBoxDigList.get(i));
			deviceGroupFunctionBoxDigList.get(i).setVisible(false);
			panel.remove(deviceGroupFunctionBoxDigList.get(i));
			propChanBoxDigList.get(i).setVisible(false);
			panel.remove(propChanBoxDigList.get(i));
			smValueFieldDigList.get(i).setVisible(false);
			panel.remove(smValueFieldDigList.get(i));
			medValueFieldDigList.get(i).setVisible(false);
			panel.remove(medValueFieldDigList.get(i));
			bigValueFieldDigList.get(i).setVisible(false);
			panel.remove(bigValueFieldDigList.get(i));
			pinLblList.get(i).setVisible(false);
			panel.remove(pinLblList.get(i));
			dltBtnList.get(i).setVisible(false);
			panel.remove(dltBtnList.get(i));
		}
		methodBoxDigList = new ArrayList<JComboBox>();
		deviceGroupFunctionBoxDigList = new ArrayList<JComboBox>();
		propChanBoxDigList = new ArrayList<JComboBox>();
		smValueFieldDigList = new ArrayList<JTextField>();
		medValueFieldDigList = new ArrayList<JTextField>();
		bigValueFieldDigList = new ArrayList<JTextField>();
		pinLblList = new ArrayList<JTextField>();
		dltBtnList = new ArrayList<JButton>();
	}
	
	private void guiToMap(){
		boolean configOld = false;
		newConfig();
		int size =0;
		int sizeCounter = 0;
		for(Integer key: map.keySet()){
			if(key > -1){
				addNewLine();
			}
			size++;
		}
		for(Integer key: map.keySet()){
			if(sizeCounter > size)
				break;
			if(key == -1){
				//port
				try{
					portBox.setSelectedItem((String)map.get(-1)[0]);
				}
				catch(Exception e){
					portBox.setSelectedIndex(0);
				}
			}
			else if(key == -2){
				//driver
				try{
					driverBox.setSelectedIndex(0);
					driverBox.setSelectedIndex(1);
					driverBox.setSelectedItem((String)map.get(-2)[0]);
				}
				catch(Exception e){
					driverBox.setSelectedIndex(1);
					driverBox.setSelectedIndex(0);
				}
			}
			else{
				//digital
				pinLblList.get(sizeCounter).setText(""+key);
				String[] mapString = map.get(key);
				methodBoxDigList.get(sizeCounter).setSelectedIndex(Integer.parseInt(mapString[0]));
				try{
					switch(Integer.parseInt(mapString[0])){
						case Constants.CERTAINCHANNEL:
							deviceGroupFunctionBoxDigList.get(sizeCounter).setSelectedItem(mapString[1]);
							if(!deviceGroupFunctionBoxDigList.get(sizeCounter).getSelectedItem().equals(mapString[1])){
								configOld = true;
							}
							propChanBoxDigList.get(sizeCounter).setSelectedItem(mapString[2]);
							if(!propChanBoxDigList.get(sizeCounter).getSelectedItem().equals(mapString[2])){
								configOld = true;
							}
							break;
						case Constants.CERTAINPROP:
							deviceGroupFunctionBoxDigList.get(sizeCounter).setSelectedItem(mapString[1]);
							if(!deviceGroupFunctionBoxDigList.get(sizeCounter).getSelectedItem().equals(mapString[1])){
								configOld = true;
							}
							propChanBoxDigList.get(sizeCounter).setSelectedItem(mapString[2]);
							if(!propChanBoxDigList.get(sizeCounter).getSelectedItem().equals(mapString[1])){
								configOld = true;
							}
							smValueFieldDigList.get(sizeCounter).setText(mapString[3]);		
							break;
						case Constants.CHANNELMINUS:
							deviceGroupFunctionBoxDigList.get(sizeCounter).setSelectedItem(mapString[1]);	
							if(!deviceGroupFunctionBoxDigList.get(sizeCounter).getSelectedItem().equals(mapString[1])){
								configOld = true;
							}
							break;
						case Constants.CHANNELPLUS:
							deviceGroupFunctionBoxDigList.get(sizeCounter).setSelectedItem(mapString[1]);		
							if(!deviceGroupFunctionBoxDigList.get(sizeCounter).getSelectedItem().equals(mapString[1])){
								configOld = true;
							}
							break;
						case Constants.FUNCTION:
							deviceGroupFunctionBoxDigList.get(sizeCounter).setSelectedItem(mapString[1]);		
							smValueFieldDigList.get(sizeCounter).setText(mapString[2]);
							break;
						case Constants.PROPSTEP:
							deviceGroupFunctionBoxDigList.get(sizeCounter).setSelectedItem(mapString[1]);
							if(!deviceGroupFunctionBoxDigList.get(sizeCounter).getSelectedItem().equals(mapString[1])){
								configOld = true;
							}
							propChanBoxDigList.get(sizeCounter).setSelectedItem(mapString[2]);
							if(!propChanBoxDigList.get(sizeCounter).getSelectedItem().equals(mapString[2])){
								configOld = true;
							}
							smValueFieldDigList.get(sizeCounter).setText(mapString[3]);
							medValueFieldDigList.get(sizeCounter).setText(mapString[4]);
							bigValueFieldDigList.get(sizeCounter).setText(mapString[5]);	
							break;
					}
				}
				catch(NullPointerException npe){
					configOld = true;
				}
				sizeCounter ++;
			}
		}
		setApplied(true);
		if(configOld){
			if(!isLoading){
				int result = JOptionPane.showConfirmDialog(null, 
						" Your configuration file does not fit your connected hardware. "
						+ "\n Do you want to save your old configuration to a different file?"
						+ "\n \n By pressing \"No\" you will overwrite your old configuration."
						+ "\n By pressing \"Cancel\" you will ignore this message.",
						"Old Configuration Conflict",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if(result == JOptionPane.YES_OPTION){
					int value = fmAR.showSaveDialog(ConfigGui.this);
					if(value == JFileChooser.APPROVE_OPTION){
						File file = fmAR.getSelectedFile();
						fh.saveFile(map,file);
						newConfig();
					}
				}
				if(result == JOptionPane.NO_OPTION){
					newConfig();
				}
			}
			if(isLoading){
				JOptionPane.showMessageDialog(null, "Your configuration "
						+ "is not compatible with your connected hardware. "
						+ "\nThis may cause unexpected configuration entries.",
						"Warning!",JOptionPane.OK_OPTION);
				isLoading = false;
			}
			setApplied(false);
		}
		
		
	}
	
	
	
	
	
	public void setLastPressedBtn(String id){
		txtFLastPressedBtn.setText(id);
	}

	private void setApplied(boolean isApplied){
		if(isApplied){
			applyButton.setText(BTNSTRINGAPPLIED);
			applyButton.setEnabled(false);
		}
		else{
			applyButton.setText(BTNSTRINGAPPLY);
			applyButton.setEnabled(true);
		}
	}
	
	public void apply(){
		map = new HashMap<Integer, String[]>();
		String errorString = "";
		lastConfigValid = true; 
		map.put(-1, new String[]{(String)portBox.getSelectedItem()});
		map.put(-2,new String[]{(String)driverBox.getSelectedItem()});
		
		for(int index = 0; index < methodBoxDigList.size(); index++){
			int method = methodBoxDigList.get(index).getSelectedIndex();
			boolean valid = true;
			String inputVal = smValueFieldDigList.get(index).getText();
			String device = (String) deviceGroupFunctionBoxDigList.get(index)
					.getSelectedItem();
			String prop = (String) propChanBoxDigList.get(index)
					.getSelectedItem();
			String[] mapString = new String[] {};
			switch (method) {

			case Constants.FUNCTION:
				String function = (String) deviceGroupFunctionBoxDigList.get(index)
						.getSelectedItem();
				mapString = new String[] { "" + method, function , smValueFieldDigList.get(index).getText()};
				break;
			case Constants.CERTAINCHANNEL:
				mapString = new String[] { "" + method, device,
						prop };
				break;
			case Constants.CHANNELPLUS:
				mapString = new String[] { "" + method, device };
				break;
			case Constants.CHANNELMINUS:
				mapString = new String[] { "" + method, device };
				break;
			case Constants.CERTAINPROP:
				boolean isANumber = ScriptInterfaceWrapper
						.propertyTypeIsANumber(device, prop);
				try {
					Double.parseDouble(inputVal);
						valid = true;
						lastConfigValid = true;
				} catch (Exception e) {
					if (isANumber) {
						valid = false;
						lastConfigValid = false;
					}
				}
				// Special case: Input is a Number but MM doesnt see
				// needed Input as number

				if (!valid) {
					errorString +="\n" + "-Sorry this value is not valid for "+ device + "-" + prop + "." + "\n";
					smValueFieldDigList.get(index).setBackground(new Color(Constants.COLOR_ERROR));
					smValueFieldDigList.get(index).setForeground(new Color(Constants.COLOR_ERRORTEXT));
				} else {
					mapString = new String[] { "" + method, device,
							prop, inputVal };
					smValueFieldDigList.get(index).setBackground(new Color(0xffffff));
					smValueFieldDigList.get(index).setForeground(new Color(0x000000));
				}
				break;
			case Constants.PROPSTEP:
				try {
					Double.parseDouble(smValueFieldDigList.get(index)
							.getText());
					Double.parseDouble(medValueFieldDigList.get(index)
							.getText());
					Double.parseDouble(bigValueFieldDigList.get(index)
							.getText());
				} catch (Exception e) {
					valid = false;
					errorString +="\n-Your step values have to be numbers.";
				}
				if (valid) {
					mapString = new String[] { "" + method, device,
							prop,
							"" + smValueFieldDigList.get(index).getText(),
							"" + medValueFieldDigList.get(index).getText(),
							"" + bigValueFieldDigList.get(index).getText() };
				}

				break;
			}
			int inputNr;
			try{
				inputNr = Integer.parseInt(pinLblList.get(index).getText());
				pinLblList.get(index).setBackground(new Color(0xffffff));
				pinLblList.get(index).setForeground(new Color(0x000000));
			}
			catch(Exception e){
				valid = false;
				inputNr = -2;
				errorString +="\n" + "-Invalid InputNr" + "\n";
				pinLblList.get(index).setBackground(new Color(Constants.COLOR_ERROR));
				pinLblList.get(index).setForeground(new Color(Constants.COLOR_ERRORTEXT));
			}
			if (valid) {
				map.put(inputNr, mapString);
			}
		}
		if(errorString.length() >0){
			JOptionPane.showMessageDialog(null, 
					errorString,
					"Invalid Values",
					JOptionPane.ERROR_MESSAGE);
		}
		else{
			fh.saveFile(map,new File(Constants.CONFFILENAME));
			setApplied(true);
		}
	}
	
	/*Will be used in the future
	 * private class IPThread extends Thread{
		public void run(){
			portLabel.setText("loading...");
//			setLocalIP(IPgetter.getLocalIP());

			String ip = IPgetter.getPublicIP();
			portLabel.setVisible(false);
			panel.remove(portLabel);
			panel.revalidate();
			panel.repaint();
			QRCodeGenerator qrg = new QRCodeGenerator(""+Constants.QRENCODER +Constants.QRSEPERATOR+ ip);
			portLabel = new JLabel(new ImageIcon(qrg.getCodeAsImage()));
			panel.add(portLabel,"cell 8 0");
			portLabel.setText("");
			return;
		}
	}*/
	
	public void addNewLine(){
		//methodBox
		
		String[] methodBoxDigString = Constants.METHODBOXSTRINGS;
		JComboBox methodBoxDigi = new JComboBox(methodBoxDigString);
		int rownumber = methodBoxDigList.size() + FIRSTBLOCKSTART+1;
		methodBoxDigi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent item) {
				JComboBox box = (JComboBox) item.getSource();
				String method = (String) box.getSelectedItem();
				int index = 0;
				for (int j = 0; j < methodBoxDigList.size(); j++) {
					if (box.equals(methodBoxDigList.get(j))) {
						index = j;
					}
				}
				try {
					deviceGroupFunctionBoxDigList.get(index)
							.setModel(new DefaultComboBoxModel(
									ScriptInterfaceWrapper.getGroupNames()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				deviceGroupFunctionBoxDigList.get(index).setVisible(false);
				deviceGroupFunctionBoxDigList.get(index).setEnabled(false);
				propChanBoxDigList.get(index).setVisible(false);
				propChanBoxDigList.get(index).setEnabled(false);
				smValueFieldDigList.get(index).setVisible(false);
				smValueFieldDigList.get(index).setEnabled(false);
				medValueFieldDigList.get(index).setVisible(false);
				medValueFieldDigList.get(index).setEnabled(false);
				bigValueFieldDigList.get(index).setVisible(false);
				bigValueFieldDigList.get(index).setEnabled(false);

				if (method.equals(Constants.METHODFUNCTION)) {
					deviceGroupFunctionBoxDigList.get(index)
							.setModel(new DefaultComboBoxModel(functions));
					deviceGroupFunctionBoxDigList.get(index).setVisible(true);
					deviceGroupFunctionBoxDigList.get(index).setEnabled(true);
					if(deviceGroupFunctionBoxDigList.get(index).getSelectedItem().equals(Constants.FUNCTIONSTRINGSCRIPT)){
						smValueFieldDigList.get(index).setVisible(true);
						smValueFieldDigList.get(index).setEnabled(true);
					}
				}
				if (method.equals(Constants.METHODCONFIGCERTAIN)) {
					try {
						String[] groups = ScriptInterfaceWrapper
								.getGroupNames();
						deviceGroupFunctionBoxDigList.get(index)
								.setModel(new DefaultComboBoxModel(groups));
						propChanBoxDigList.get(index).setModel(new DefaultComboBoxModel(
								ScriptInterfaceWrapper
										.getGroupChannelNames(groups[0])));
					} catch (Exception e) {
						e.printStackTrace();
					}
					deviceGroupFunctionBoxDigList.get(index).setVisible(true);
					deviceGroupFunctionBoxDigList.get(index).setEnabled(true);
					propChanBoxDigList.get(index).setVisible(true);
					propChanBoxDigList.get(index).setEnabled(true);
				}
				if (method.equals(Constants.METHODCONFIGUP)) {
					deviceGroupFunctionBoxDigList.get(index).setVisible(true);
					deviceGroupFunctionBoxDigList.get(index).setEnabled(true);
				}
				if (method.equals(Constants.METHODCONFIGDOWN)) {
					deviceGroupFunctionBoxDigList.get(index).setVisible(true);
					deviceGroupFunctionBoxDigList.get(index).setEnabled(true);
				}
				if (method.equals(Constants.MEtHODPROPCERTAIN)) {
					try {
						String[] devices = ScriptInterfaceWrapper
								.getDeviceNames();
						deviceGroupFunctionBoxDigList.get(index)
								.setModel(new DefaultComboBoxModel(devices));
						propChanBoxDigList.get(index).setModel(new DefaultComboBoxModel(
								ScriptInterfaceWrapper
										.getDevicePropertyNames(devices[0])));
					} catch (Exception e) {
						e.printStackTrace();
					}
					deviceGroupFunctionBoxDigList.get(index).setVisible(true);
					deviceGroupFunctionBoxDigList.get(index).setEnabled(true);
					propChanBoxDigList.get(index).setVisible(true);
					propChanBoxDigList.get(index).setEnabled(true);
					smValueFieldDigList.get(index).setVisible(true);
					smValueFieldDigList.get(index).setEnabled(true);
				}
				if (method.equals(Constants.METHODPROPSTEP)) {
					try {
						String[] devices = ScriptInterfaceWrapper
								.getNumberDeviceNames();
						deviceGroupFunctionBoxDigList.get(index)
								.setModel(new DefaultComboBoxModel(devices));
						propChanBoxDigList.get(index).setModel(new DefaultComboBoxModel(
								ScriptInterfaceWrapper
										.getDeviceNumberPropertyNames(devices[0])));
					} catch (Exception e) {
						e.printStackTrace();
					}
					deviceGroupFunctionBoxDigList.get(index).setVisible(true);
					deviceGroupFunctionBoxDigList.get(index).setEnabled(true);
					propChanBoxDigList.get(index).setVisible(true);
					propChanBoxDigList.get(index).setEnabled(true);
					smValueFieldDigList.get(index).setVisible(true);
					smValueFieldDigList.get(index).setEnabled(true);
					medValueFieldDigList.get(index).setVisible(true);
					medValueFieldDigList.get(index).setEnabled(true);
					bigValueFieldDigList.get(index).setVisible(true);
					bigValueFieldDigList.get(index).setEnabled(true);
				}
			}
		});
		methodBoxDigList.add(methodBoxDigi);
		
		//deviceGroupFunctionBox
		JComboBox deviceGroupFunctionBoxDigi;
		try {
			deviceGroupFunctionBoxDigi = new JComboBox(
					functions);
		} catch (Exception e1) {
			deviceGroupFunctionBoxDigi = new JComboBox();
		}
		deviceGroupFunctionBoxDigi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent item) {
				JComboBox box = (JComboBox) item.getSource();
				String deviceORgroup = (String) box.getSelectedItem();
				String[] propORchannel;
				int index = 0;
				for (int j = 0; j < deviceGroupFunctionBoxDigList.size(); j++) {
					if (box.equals(deviceGroupFunctionBoxDigList.get(j))) {
						index = j;
					}
				}
				String selectedMethod = (String) methodBoxDigList.get(index)
						.getSelectedItem();
				if (selectedMethod.equals(Constants.MEtHODPROPCERTAIN)) {
					try {
							propORchannel = ScriptInterfaceWrapper
									.getDevicePropertyNames(deviceORgroup);
					} catch (Exception e) {
						propORchannel = new String[] {};
					}
				} else if(selectedMethod.equals(Constants.METHODFUNCTION)){

					propChanBoxDigList.get(index).setVisible(false);
					propChanBoxDigList.get(index).setEnabled(false);
					smValueFieldDigList.get(index).setVisible(false);
					smValueFieldDigList.get(index).setEnabled(false);
					medValueFieldDigList.get(index).setVisible(false);
					medValueFieldDigList.get(index).setEnabled(false);
					bigValueFieldDigList.get(index).setVisible(false);
					bigValueFieldDigList.get(index).setEnabled(false);
					propORchannel = ScriptInterfaceWrapper
							.getGroupChannelNames(deviceORgroup);
					if(((String)box.getSelectedItem()).equals(Constants.FUNCTIONSTRINGSCRIPT)&&!smValueFieldDigList.get(index).isVisible()){
						smValueFieldDigList.get(index).setVisible(true);
						smValueFieldDigList.get(index).setEnabled(true);
					}
				} else{
					try {
						propORchannel = ScriptInterfaceWrapper
								.getGroupChannelNames(deviceORgroup);
					} catch (Exception e) {
						propORchannel = new String[] {};
					}
				}
				propChanBoxDigList.get(index).setModel(new DefaultComboBoxModel(
						propORchannel));
				panel.repaint();
			}
		});
		deviceGroupFunctionBoxDigList.add(deviceGroupFunctionBoxDigi);
		
		
		//propChanBox
		JComboBox propChanBoxDigi = new JComboBox();
		propChanBoxDigList.add(propChanBoxDigi);
		
		
		//smValueFieldDig
		JTextField smValueFieldDigi = new JTextField();
		smValueFieldDigList.add(smValueFieldDigi);
		
		smValueFieldDigi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				if(arg0.getClickCount() >1){
					for(int i=0; i < smValueFieldDigList.size(); i++){
						if(arg0.getSource().equals(smValueFieldDigList.get(i))){
						}
					}
				}
			}
		});
		
		//medValueFieldDi
		JTextField medValueFieldDigi = new JTextField();
		medValueFieldDigList.add(medValueFieldDigi);
		
		
		//bigValueFieldDig
		JTextField bigValueFieldDigi = new JTextField();
		panel.add(bigValueFieldDigi, "cell "+(LINECOLINT+7)+" " + rownumber + ",growx");
		bigValueFieldDigi.setVisible(false);
		bigValueFieldDigi.setEnabled(false);
		bigValueFieldDigList.add(bigValueFieldDigi);
		

		//pinLbl
		JTextField pinLbli = new JTextField();
		pinLblList.add(pinLbli);
		
		
		//dltBtnList
		JButton dltBtni = new JButton(BTNSTRINGDEL);
		dltBtni.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int index = -1;
				JButton dltBtni = (JButton)arg0.getSource();
				for(int i = 0; i < dltBtnList.size(); i++){
					if(dltBtni.equals(dltBtnList.get(i))){
						index = i;
						break;
					}
				}
				
				int size = methodBoxDigList.size();
				for(int i = 0; i < size; i++){
					methodBoxDigList.get(i).setVisible(false);
					panel.remove(methodBoxDigList.get(i));
					deviceGroupFunctionBoxDigList.get(i).setVisible(false);
					panel.remove(deviceGroupFunctionBoxDigList.get(i));
					propChanBoxDigList.get(i).setVisible(false);
					panel.remove(propChanBoxDigList.get(i));
					smValueFieldDigList.get(i).setVisible(false);
					panel.remove(smValueFieldDigList.get(i));
					medValueFieldDigList.get(i).setVisible(false);
					panel.remove(medValueFieldDigList.get(i));
					bigValueFieldDigList.get(i).setVisible(false);
					panel.remove(bigValueFieldDigList.get(i));
					pinLblList.get(i).setVisible(false);
					panel.remove(pinLblList.get(i));
					dltBtnList.get(i).setVisible(false);
					panel.remove(dltBtnList.get(i));
				}
				dltBtnList.remove(index);
				methodBoxDigList.remove(index);
				deviceGroupFunctionBoxDigList.remove(index);
				propChanBoxDigList.remove(index);
				smValueFieldDigList.remove(index);
				medValueFieldDigList.remove(index);
				bigValueFieldDigList.remove(index);
				pinLblList.remove(index);
				
				size = methodBoxDigList.size();
				
				for(int i = 0; i < size; i++){
					int rownumber = i + FIRSTBLOCKSTART+1;
					panel.remove(dltBtnList.get(i));
					panel.remove(methodBoxDigList.get(i));
					panel.remove(deviceGroupFunctionBoxDigList.get(i));
					panel.remove(propChanBoxDigList.get(i));
					panel.remove(smValueFieldDigList.get(i));
					panel.remove(medValueFieldDigList.get(i));
					panel.remove(bigValueFieldDigList.get(i));
					panel.remove(pinLblList.get(i));
					
					panel.add(dltBtnList.get(i), "cell "+(LINECOLINT)+" " + rownumber + ",growx");
					dltBtnList.get(i).setVisible(true);
					panel.add(methodBoxDigList.get(i), "cell "+(LINECOLINT+2)+" " + rownumber + ",growx");
					methodBoxDigList.get(i).setVisible(true);
					
					panel.add(deviceGroupFunctionBoxDigList.get(i), "cell "+(LINECOLINT+3)+" " + rownumber
							+ ",growx");
					deviceGroupFunctionBoxDigList.get(i).setVisible(true);
					deviceGroupFunctionBoxDigList.get(i).setEnabled(true);
					
					panel.add(propChanBoxDigList.get(i), "cell "+(LINECOLINT+4)+" " + rownumber + ",growx");
					propChanBoxDigList.get(i).setVisible(false);
					propChanBoxDigList.get(i).setEnabled(false);
					
					panel.add(smValueFieldDigList.get(i), "cell "+(LINECOLINT+5)+" " + rownumber + ",growx");
					smValueFieldDigList.get(i).setVisible(false);
					smValueFieldDigList.get(i).setEnabled(false);
					
					panel.add(medValueFieldDigList.get(i), "cell "+(LINECOLINT+6)+" " + rownumber + ",growx");
					medValueFieldDigList.get(i).setVisible(false);
					medValueFieldDigList.get(i).setEnabled(false);
					
					panel.add(pinLblList.get(i), "cell "+(LINECOLINT+1)+" " + rownumber + ",growx");
					pinLblList.get(i).setVisible(true);
					
					int methInd = methodBoxDigList.get(i).getSelectedIndex();
					methodBoxDigList.get(i).setSelectedIndex(2);
					methodBoxDigList.get(i).setSelectedIndex(3);
					methodBoxDigList.get(i).setSelectedIndex(methInd);
				}
			}
		});	
		
		dltBtni.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent item) {
				setApplied(false);
			}
		});
		methodBoxDigi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent item) {
				setApplied(false);
			}
		});
		deviceGroupFunctionBoxDigi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent item) {
				setApplied(false);
			}
		});
		propChanBoxDigi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent item) {
				setApplied(false);
			}
		});
		smValueFieldDigi.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
		});
		medValueFieldDigi.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
		});
		bigValueFieldDigi.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
		});
		pinLbli.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				setApplied(false);
				
			}
		});
		
		panel.add(dltBtni, "cell "+(LINECOLINT)+" " + rownumber);
		dltBtnList.add(dltBtni);
		
		panel.add(methodBoxDigi, "cell "+(LINECOLINT+2)+" " + rownumber + ",growx");
		methodBoxDigi.setVisible(true);
		
		panel.add(deviceGroupFunctionBoxDigi, "cell "+(LINECOLINT+3)+" " + rownumber
				+ ",growx");
		deviceGroupFunctionBoxDigi.setVisible(true);
		deviceGroupFunctionBoxDigi.setEnabled(true);
		
		panel.add(propChanBoxDigi, "cell "+(LINECOLINT+4)+" " + rownumber + ",growx");
		propChanBoxDigi.setVisible(false);
		propChanBoxDigi.setEnabled(false);
		
		panel.add(smValueFieldDigi, "cell "+(LINECOLINT+5)+" " + rownumber + ",growx");
		smValueFieldDigi.setVisible(false);
		smValueFieldDigi.setEnabled(false);
		
		panel.add(medValueFieldDigi, "cell "+(LINECOLINT+6)+" " + rownumber + ",growx");
		medValueFieldDigi.setVisible(false);
		medValueFieldDigi.setEnabled(false);
		
		panel.add(pinLbli, "cell "+(LINECOLINT+1)+" " + rownumber + ",growx");
		

	}

	
}
