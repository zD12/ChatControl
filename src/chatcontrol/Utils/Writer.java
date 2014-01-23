package chatcontrol.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import chatcontrol.ChatControl;

public class Writer {

	/*public static void writeAd(Player hrac, String sprava){
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
	  }*/
	public static enum TypSuboru {
		REKLAMY("advertisements.log"),
		ZAZNAM_CHATU("chat.log");
		
		public String typ;
		TypSuboru (String typ_suboru) {
			typ = typ_suboru;
		}
	}

	public static void writeToFile(TypSuboru typSuboru, String prefix, String msg) {
		BufferedWriter bw = null;		        
		try {            
			bw = new BufferedWriter(new FileWriter(new File(ChatControl.plugin.getDataFolder(), typSuboru.typ), true));            
			bw.write("[" + cas() + "] " + (prefix != null ? prefix + ": " : "") + msg);
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
