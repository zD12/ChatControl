package me.kangarko.chc.model;


public class CasusHelper {

	private final String nominativPl; // 2 to 4 seconds (slovak case - sekundy)
	private final String akuzativSg; // 1 second (slovak case - sekundu) - not in english (LOL noobs)
	private final String genitivePl; // 5 and more seconds (slovak case - sekund)
	
	public CasusHelper(String raw) {
		
		String[] values = raw.split(", ");
		
		if (values.length == 2) {
			this.akuzativSg = values[0];
			this.nominativPl = values[1];
			this.genitivePl = nominativPl;
			return;
		}
		
		if (values.length != 3)
			throw new RuntimeException("Malformed type, use format: second, seconds, seconds (if your language has it)");
		
		this.akuzativSg = values[0];
		this.nominativPl = values[1];
		this.genitivePl = values[2];
	}
	
	public String formatNumbers(long count) {
		if(count == 1) 
			return akuzativSg;
		if(count > 1 && count < 5) 
			return nominativPl;
		
		return genitivePl;
	}
}