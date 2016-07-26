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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ArduinoScriptGeneratorWindow extends JDialog {
	public static String menuName = "Arduino Script Generator";
	public static String tooltipDescription = "Generate the arduino-script for the ArduinoRemote plugin.";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	JCheckBox[] chckbxDigital = new JCheckBox[Constants.PINNUMBERDIG];
	JCheckBox[] chckbxAnalog = new JCheckBox[Constants.PINNUMBERANAL];
	JLabel informationLbl;
	ArduinoScriptGeneratorWindow dialog;

	
	public ArduinoScriptGeneratorWindow() {
		setBounds(100, 100, 450, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][][][][][]", "[][]"));
		informationLbl = new JLabel("All other parts of ArduinoRemote have to be closed, \n while uploading a Arduino Script");
		contentPanel.add(informationLbl,"span");
		for(int j = 0; j < 3; j++){
			for(int i = 0; i < Constants.PINNUMBERDIG/3; i++)
			{
				chckbxDigital[i] = new JCheckBox(Constants.IDPREFIX+" "+((j*Constants.PINNUMBERDIG/3)+i));
				contentPanel.add(chckbxDigital[i], "cell "+j+" "+(i+2));
			}
		}
		for(int i = 3*(int)Math.floor(Constants.PINNUMBERDIG/3); i < Constants.PINNUMBERDIG; i++)
		{
			chckbxDigital[i] = new JCheckBox(Constants.IDPREFIX+" "+(i+1));
			contentPanel.add(chckbxDigital[i], "cell "+3+" "+(i-3*(int)Math.floor(Constants.PINNUMBERDIG/3)+2));
		}
		for(int i = 0; i< Constants.PINNUMBERANAL; i++)
		{
			chckbxAnalog[i] = new JCheckBox("Analog "+i);
			contentPanel.add(chckbxAnalog[i], "cell 5 "+(i+2));
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						JButton b = (JButton)arg0.getSource();
						ArduinoScriptGeneratorWindow jD = (ArduinoScriptGeneratorWindow)SwingUtilities.getRoot(b);
						generateScript(getDigBoxes(), getAnalBoxes());
						jD.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						JButton b = (JButton)arg0.getSource();
						JDialog jD = (JDialog)SwingUtilities.getRoot(b);
						jD.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
	}
	
	private int[] getDigBoxes(){
		ArrayList<Integer> helperList = new ArrayList<Integer>();
		for( int i = 0; i < chckbxDigital.length; i++){
			if(chckbxDigital[i].isSelected()){
				helperList.add(i);
			}
		}
		int[] returnArray = new int[helperList.size()];
		for(int i = 0; i < helperList.size(); i++){
			returnArray[i] = helperList.get(i);
		}
		return returnArray;
	}
	
	private int[] getAnalBoxes(){
		ArrayList<Integer> helperList = new ArrayList<Integer>();
		for( int i = 0; i < chckbxAnalog.length; i++){
			if(chckbxAnalog[i].isSelected()){
				helperList.add(i);
			}
		}
		int[] returnArray = new int[helperList.size()];
		for(int i = 0; i < helperList.size(); i++){
			returnArray[i] = helperList.get(i);
		}
		return returnArray;
		
	}
	
	public void generateScript(int[] digInUse, int[] analInUse){
		File dir = new File("ArduinoGen");
		if(!dir.exists()){
			dir.mkdir();
		}
		File file = new File("ArduinoGen/ArduinoGen.ino");
		FileWriter fileWr;
		BufferedWriter writer;
		String inputDigInUse= "";
		for(int i: digInUse){
			inputDigInUse += "digInUse["+i+"] = true; \n  ";
		}
		String inputAnalInUse = "";
		for(int i: analInUse){
			inputAnalInUse += "analInUse["+i+"] = true; \n  ";
		}
		try {
			fileWr = new FileWriter(file);
			writer = new BufferedWriter(fileWr);
			writer.write("boolean digInUse["+(Constants.PINNUMBERDIG)+"];" +"\n"
					+"boolean analInUse[10];" +"\n"
					+"int aVals[6];" +"\n"
					+"unsigned long buttonTimes["+(Constants.PINNUMBERDIG)+"];" +"\n"
					+"unsigned long lastSendTime = millis();" +"\n"
					+"int cap = 9000;" +"\n"
					+"unsigned long lastSaveTime = 0;" +"\n"
					+"" +"\n"
					+"void setup() {" +"\n"
					+"  //NonLEDS" +"\n"
					+"  for(int i = 0; i<="+(Constants.PINNUMBERDIG-1)+"; i++){" +"\n"
					+"    pinMode(i,INPUT);" +"\n"
					+"    buttonTimes[i] = millis();" +"\n"
					+"    digInUse[i] = false;" +"\n"
					+"  }" +"\n"
					+"  //AnalogValues" +"\n"
					+"  for(int i = 0; i < 6; i++){" +"\n"
					+"    aVals[i] = 0;" +"\n"
					+"    analInUse[i] = false;" +"\n"
					+"  }" +"\n"
					+"  //insert anal and dig generic stuff" +"\n"
					+"  " +inputDigInUse + inputAnalInUse
					+"  Serial.begin(9600);" +"\n"
					+"}" +"\n"
					+"" +"\n"
					+"void loop() {" +"\n"
					+"  if( millis() - lastSaveTime > 1000){" +"\n"
					+"    Serial.println(\"-1,0\");"+"\n"
					+"    lastSaveTime = millis();"+"\n"
					+"  }" +"\n"
					+"  " +"\n"
					+"  //ports 0 and 1 are spamming" +"\n"
					+"  //If a Place isn't taken it goes off if the one next to it goes off" +"\n"
					+"    /*for(int i = 0; i<="+(Constants.PINNUMBERDIG-1)+"; i++){" +"\n"
					+"      digitalWrite(i,LOW);" +"\n"
					+"    }*/" +"\n"
					+"  for(int i = 0; i<="+(Constants.PINNUMBERDIG-1)+"; i++){" +"\n"
					+"    if(digInUse[i]){" +"\n"
					+"      if(digitalRead(i) == HIGH && millis() - buttonTimes[i] > 400){" +"\n"
					+"        " +"\n"
					+"        String value = String(i)+\",0\";" +"\n"
					+"        buttonTimes[i] = millis();" +"\n"
					+"        serialPrintln(String(value));" +"\n"
					+"      }" +"\n"
					+"      //To prevent a bug where after a while the buttons start spamming" +"\n"
					+"      if(millis()-buttonTimes[i] > 1500){" +"\n"
					+"        buttonTimes[i] = millis()-500;" +"\n"
					+"        " +"\n"
					+"      }" +"\n"
					+"    }" +"\n"
					+"  }" +"\n"
					+"  " +"\n"
					+"  //If the place isnt taken it spams repeatedly" +"\n"
					+"  //The signal goes from 0 to a cap of 999 to not have to deal with overload" +"\n"
					+"  if(analInUse[0] &&!(analogRead(A0) - aVals[0] <2 && analogRead(A0) - aVals[0] > -2)){" +"\n"
					+"    aVals[0] = analogRead(A0);" +"\n"
					+"    if(aVals[0] > cap){" +"\n"
					+"      aVals[0] = cap;" +"\n"
					+"    }" +"\n"
					+"    serialPrintln(\"10,\" + String(aVals[0]));" +"\n"
					+"  }" +"\n"
					+"    if(analInUse[1] &&!(analogRead(A1) - aVals[1] <2 && analogRead(A1) - aVals[1] > -2)){" +"\n"
					+"    aVals[1] = analogRead(A1);" +"\n"
					+"    if(aVals[1] > cap){" +"\n"
					+"      aVals[1] = cap;" +"\n" +"\n"
					+"    }" +"\n"
					+"    Serial.println(\"11,\" + String(aVals[1]));" +"\n"
					+"  }" +"\n"
					+"    if(analInUse[2] &&!(analogRead(A2) - aVals[2] <2 && analogRead(A2) - aVals[2] > -2)){" +"\n"
					+"    aVals[2] = analogRead(A2);" +"\n"
					+"    if(aVals[2] > cap){" +"\n"
					+"      aVals[2] = cap;" +"\n"
					+"    }" +"\n"
					+"    Serial.println(\"12,\" + String(aVals[2]));" +"\n"
					+"  }" +"\n"
					+"    if(analInUse[3] &&!(analogRead(A3) - aVals[3] <2 && analogRead(A3) - aVals[3] > -2)){" +"\n"
					+"    aVals[3] = analogRead(A3);" +"\n"
					+"    if(aVals[3] > cap){" +"\n"
					+"      aVals[3] = cap;" +"\n"
					+"    }" +"\n"
					+"    Serial.println(\"13,\" + String(aVals[3]));" +"\n"
					+"  }" +"\n"
					+"    if(analInUse[4] &&!(analogRead(A4) - aVals[4] <2 && analogRead(A4) - aVals[4] > -2)){" +"\n"
					+"    aVals[4] = analogRead(A4);" +"\n"
					+"    if(aVals[4] > cap){" +"\n"
					+"      aVals[4] = cap;" +"\n"
					+"    }" +"\n"
					+"    Serial.println(\"14,\" + String(aVals[4]));" +"\n"
					+"  }" +"\n"
					+"    if(analInUse[5] &&!(analogRead(A5) - aVals[5] <2 && analogRead(A5) - aVals[5] > -2)){" +"\n"
					+"    aVals[5] = analogRead(A5);" +"\n"
					+"    if(aVals[5] > cap){" +"\n"
					+"      aVals[5] = cap;" +"\n"
					+"    }" +"\n"
					+"    Serial.println(\"15,\" + String(aVals[5]));" +"\n"
					+"  }" +"\n"
					+"  }" +"\n"
					+" " +"\n"
					+"  void serialPrintln(String msg){" +"\n"
					+"    int msgPerSecond = 15;" +"\n"
					+"    if(millis()-lastSendTime > 1000/msgPerSecond || millis() < 2000){" +"\n"
					+"       Serial.println(msg); " +"\n"
					+"       lastSendTime = millis();" +"\n"
					+"    }" +"\n"
					+"  }" +"\n"
					+"  " +"\n"
					+"");
			writer.close();
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
