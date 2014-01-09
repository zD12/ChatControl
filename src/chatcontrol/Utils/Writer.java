package chatcontrol.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.entity.Player;

public class Writer {
	
	  public static void writeAd(Player hrac, String sprava){
		    BufferedWriter w = null;
		    try{
		      w = new BufferedWriter(new FileWriter("plugins/ChatControl/advertisements.log", true));
		      w.write("[" + cas() + "] " + hrac.getName() + ": " + sprava);
		      w.newLine();
		    } catch (Exception ex) {
		      try{
		        if (w != null) {
		          w.flush();
		          w.close();
		        }
		      } catch (Exception ex2) {}
		    }
		    finally{
		      try{
		        if (w != null) {
		          w.flush();
		          w.close();
		        }
		      } catch (Exception ex) {
		      }
		    }
	  }
	  
	  public static void writeChat(Player pl, String msg) {
		BufferedWriter bw = null;		        
		   try {            
		       	bw = new BufferedWriter(new FileWriter("plugins/ChatControl/chat.log", true));            
		       	bw.write("[" + cas() + "] " + pl.getName() + ": " + msg);
		       	bw.newLine();            
		   } catch (Exception ex) {
		   } finally {
		       try {
		          if (bw != null) {
		              	bw.flush();
		               	bw.close();
		          }		         
		       } catch (Exception ex) {
		       }
		  }
	  }
	  
	  public static String cas() {
		    DateFormat date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		    Calendar cal = Calendar.getInstance();
		    return date.format(cal.getTime());
	  }
		  
}
