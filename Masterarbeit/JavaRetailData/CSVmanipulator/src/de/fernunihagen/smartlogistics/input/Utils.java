package de.fernunihagen.smartlogistics.input;
   
import java.util.Date;   

public class Utils {
	public static String getCurrentTime(long currentTime) {

        Date date=new Date(currentTime);  
        return date.toGMTString()+ ": ";   
		
	}
}
