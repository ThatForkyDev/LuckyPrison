package com.ulfric.control.entity.enums;

public enum PunishmentType {

	BAN(true, true, "banned", "The ban hammer has spoken!", "control.banned_deny", "control.banned_initial"),
	MUTE(false, true, "muted", "The mute hammer has spoken!", "control.muted_deny"),
	CMUTE(false, true, "command-muted", "The command-mute hammer has spoken!", "control.cmuted_deny"),
	KICK(true, false, "kicked", "The kick hammer has spoken!", "control.kicked_deny");

	private final boolean disconnect;
	public boolean getDisconnect() { return this.disconnect; }

	private final boolean persist;
	public boolean getPersist() { return this.persist; }

	private final String past;
	public String getPast() { return this.past; }

	private final String defaultReason;
	public String getDefaultReason() { return this.defaultReason; }

	private final String message;
	public String getMessage() { return this.message; }

	private final String imessage;
	public String getIMessage() { return this.imessage; }

	PunishmentType(boolean disconnect, boolean persist, String past, String defaultReason)
	{
		this(disconnect, persist, past, defaultReason, null);
	}

	PunishmentType(boolean disconnect, boolean persist, String past, String defaultReason, String message)
	{
		this(disconnect, persist, past, defaultReason, message, message);
	}

	PunishmentType(boolean disconnect, boolean persist, String past, String defaultReason, String message, String imessage)
	{
		this.disconnect = disconnect;

		this.persist = persist;

		this.past = past;

		this.defaultReason = defaultReason;

		this.message = message;

		this.imessage = imessage == null ? message : imessage;
	}

}