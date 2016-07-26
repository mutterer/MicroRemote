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

import java.util.ArrayList;
import java.util.HashMap;

public class UndoRedoHandler {

	ArrayList<HashMap<Integer,String[]>> maps = new ArrayList<HashMap<Integer,String[]>>();
	int currentIndex = 0;
	
	public void actionPerformed(HashMap<Integer,String[]> map){
		maps.add(map);
		int index = maps.indexOf(map);
		for(int i = index+1; i < maps.size(); i++){
			maps.remove(maps.get(i));
		}
		currentIndex = maps.size();
	}
	
	public HashMap<Integer,String[]> actionUndo(){
		currentIndex --;
		if(currentIndex <0){
			currentIndex =0;
		}
		if(maps.isEmpty())
			return null;
		return maps.get(currentIndex);
	}
	
	public HashMap<Integer,String[]> actionRedo(){
		currentIndex ++;
		if(currentIndex >= maps.size()){
			currentIndex = maps.size()-1;
		}
		if(currentIndex <0){
			currentIndex =0;
		}
		if(maps.isEmpty())
			return null;
		return maps.get(currentIndex);
	}
	
	public HashMap<Integer,String[]> getCurrentMap(){
		try{
			return maps.get(currentIndex);
		}
		catch(NullPointerException nE){
			return null;
		}
	}
}
