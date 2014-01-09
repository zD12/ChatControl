package chatcontrol.Utils;


public class Updater_Old {

	/*
	private URL filesFeed;
	private String version;
	private String link;
	
	public Updater_Old(String url){
		try{
			filesFeed = new URL(url);
		} catch (Exception ex){}
	}
	
	public boolean needsUpdate(){
		Thread t = new Thread();
		t.start();
		try {
			String ver = ChatControl.plugin.getDescription().getVersion();
			InputStream input =  filesFeed.openConnection().getInputStream();
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			
			Node latestFile = document.getElementsByTagName("item").item(0);
			NodeList children = latestFile.getChildNodes();
			
			version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
			link = children.item(3).getTextContent();
			
			if(Double.valueOf(ver.replace(".", "")) < Double.valueOf(version.replace(".", ""))){
				return true;
			}
		} catch (Exception ex){
		}
		return false;
	}
	
	public String getVersion(){
		return version;
	}
	
	public String getLink(){
		return link;
	}
	*/
}
