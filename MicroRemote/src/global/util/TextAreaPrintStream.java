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



package global.util;

import java.io.*;
import javax.swing.*;

public class TextAreaPrintStream extends PrintStream {

    private JTextArea textArea;

    public TextAreaPrintStream(JTextArea area, OutputStream out) {
	super(out);
	textArea = area;
    }

    public void println(String string) {
	textArea.append(string+"\n");
    }

    public void print(String string) {
	textArea.append(string);
    }
}