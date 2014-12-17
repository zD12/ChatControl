package me.kangarko.chc.model;

import org.apache.commons.lang3.Validate;

// rewrite
@Deprecated
public enum ChatMessageType {
	DEFAULT,
	HIDDEN,
	CUSTOM {
		@Override
		public String toString() {
			return getMsg();
		}
	};

	private String value;

	public String getMsg() {
		Validate.isTrue(this == ChatMessageType.CUSTOM, "Only custom message type support message itself");
		return value;
	}

	public ChatMessageType setValue(String val) {
		Validate.isTrue(this == ChatMessageType.CUSTOM, "Only custom message type support setting the message itself");
		value = val;
		return this;
	}

	public static ChatMessageType fromValue(String raw) {
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
				return CUSTOM.setValue(raw);
		}
	}
}