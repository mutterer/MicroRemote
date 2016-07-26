package global.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;


public class PasswGen {
	
	public static String genPasswd(int length){
		Random rand = new Random();
		byte[] utf8RetBytes = new byte[length];
		for(int i = 0; i <length; i++){
			int random= rand.nextInt(126);
			if((random < 32 && random > 0)||(random == 127)||(random == 58)){
				i-=1;
			}
			else{
				utf8RetBytes[i] = (byte)(random);
			}
		}
		try {
			return new String(utf8RetBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "UTF-8 does not exist";
		}
	}
}
