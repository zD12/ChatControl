package chatcontrol.Utils;

public class OldUpdater {
	
	/*private URL filesFeed;
	private String version;
	private String link;
	public static boolean isOutdated = false;
	public static String newVersion = "";
	
	
	public Updater(String url){
		try{
			filesFeed = new URL(url);
		} catch (Exception ex){}
	}
	
	public boolean needsUpdate(){
		Bukkit.getScheduler().runTaskAsynchronously(ChatControl.plugin, new BukkitRunnable() {
			@Override
			public void run() {
				try {					
					InputStream input =  filesFeed.openConnection().getInputStream();
					Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
					
					Node latestFile = document.getElementsByTagName("item").item(0);
					NodeList children = latestFile.getChildNodes();
					
					version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
					link = children.item(3).getTextContent();					
				} catch (Exception ex){
				}
			}
		});
		
		String ver = ChatControl.plugin.getDescription().getVersion();
		if(Double.valueOf(ver.replace(".", "")) < Double.valueOf(version.replace(".", ""))){
			return true;
		}
		
		return false;
	}
	
	public String getVersion(){
		return version;
	}
	
	public String getLink(){
		return link;
	}*/
}
