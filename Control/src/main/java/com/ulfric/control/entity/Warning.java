package com.ulfric.control.entity;

import com.ulfric.lib.api.time.Timestamp;

public class Warning {

	public Warning(PunishmentSender sender, String reason)
	{
		this.sender = sender;

		this.reason = reason;

		this.time = Timestamp.now();
	}

	private final PunishmentSender sender;
	public PunishmentSender getSender() { return this.sender; }

	private final String reason;
	public String getReason() { return this.reason; }

	private final Timestamp time;
	public Timestamp getTimestamp() { return this.time; }

}