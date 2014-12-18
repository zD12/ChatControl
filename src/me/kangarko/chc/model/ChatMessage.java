package me.kangarko.chc.model;

import java.util.Objects;

import org.apache.commons.lang.Validate;


public class ChatMessage {

	private final Type type;
	private final String message;
	
	public ChatMessage(String message) {
		this.type = Type.fromValue(message);
		this.message = message;
	}
	
	public ChatMessage(Type type) {
		Validate.isTrue(type != Type.CUSTOM, "Type cannot be custom.");
		
		this.type = type;
		this.message = type == Type.DEFAULT ? "default" : type == Type.HIDDEN ? "hidden" : null;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getMessage() {
		Objects.requireNonNull(message, "Message cannot be null!");		
		return message;
	}

	public enum Type {
		DEFAULT,
		HIDDEN,
		CUSTOM;

		public static Type fromValue(String raw) {
			switch (raw.toLowerCase()) {
				case "default":
				case "def":
				case "vanilla":
					return DEFAULT;
				case "none":
				case "hide":
				case "hidden":
					return HIDDEN;

				default:
					return CUSTOM;
			}
		}
	}


}