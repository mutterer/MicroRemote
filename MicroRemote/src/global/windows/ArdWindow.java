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

package global.windows;

import global.util.TextAreaPrintStream;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.*;

import java.awt.Color;

public class ArdWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textField;
	private JScrollPane scroller;
	private static TextAreaPrintStream ps;


	public ArdWindow() {
		setTitle("LOG");
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBackground(UIManager.getColor("CheckBoxMenuItem.acceleratorForeground"));
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{167, 89, 0};
		gbl_panel.rowHeights = new int[]{23, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		textField = new JTextArea();
		textField.setForeground(Color.GREEN);
		textField.setBackground(UIManager.getColor("CheckBoxMenuItem.acceleratorForeground"));
		textField.setRows(10);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridheight = 4;
		gbc_textField.gridwidth = 2;
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 2;
		scroller = new JScrollPane(textField);
		
		panel.add(scroller, gbc_textField);
		textField.setColumns(10);
		
		ps = new TextAreaPrintStream(textField,System.out);
	}
	
	public static void print(String input){
		ps.print(input);
	}
	public static void println(String input){
		ps.print(input + "\n");
	}

}
