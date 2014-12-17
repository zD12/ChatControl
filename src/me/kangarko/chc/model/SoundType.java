package me.kangarko.chc.model;

import org.bukkit.Sound;

public class SoundType {

	public final Sound sound;
	public final float volume, pitch;
	
	public SoundType(String raw) {
		
		String[] values = raw.split(", ");
		
		if (values.length == 1) {
			this.sound = Sound.valueOf(values[0].toUpperCase());
			this.volume = 1F;
			this.pitch = 1.5F;
			return;
		}
		
		if (values.length != 3)
			throw new RuntimeException("Malformed sound type, use format: bukkit_sound_name, float_volume, float_pitch");
		
		this.sound = Sound.valueOf(values[0].toUpperCase());
		this.volume = Float.parseFloat(values[1]);
		this.pitch = Float.parseFloat(values[2]);
	}
}