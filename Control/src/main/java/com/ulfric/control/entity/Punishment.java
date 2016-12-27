package com.ulfric.control.entity;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.time.Timestamp;

public class Punishment {

	public Punishment(PunishmentHolder holder, PunishmentSender sender, PunishmentType type, String reason, String date, Timestamp expiry, long duration)
	{
		this(PunishmentCache.getIncrementedPunishmentTotal(), holder, sender, type, reason, date, expiry, duration);
	}
	public Punishment(int id, PunishmentHolder holder, PunishmentSender sender, PunishmentType type, String reason, String date, Timestamp expiry, long duration)
	{
		this.id = id;

		this.holder = holder;

		this.sender = sender;

		this.type = type;

		this.reason = StringUtils.isEmpty(reason) ? reason : Chat.color(reason).replace(Strings.PLAYER, holder.getName());

		this.date = date;

		this.duration = duration;

		this.expiry = Optional.ofNullable(expiry).orElseGet(Timestamp::infinite);
	}

	private final int id;
	public int getId() { return this.id; }

	private final PunishmentType type;
	public PunishmentType getType() { return this.type; }

	private final String reason;
	public String getReason() { return this.reason; }

	private final String date;
	public String getDate() { return this.date; }

	private Timestamp expiry;
	public boolean isExpired()
	{
		return this.expiry.hasPassed();
	}
	public long timeTillExpiry()
	{
		if (this.isExpired()) return 0;

		return this.expiry.timeTill();
	}
	public long timeSinceExpiry()
	{
		return this.expiry.timeSince();
	}
	public long longTimestamp()
	{
		return this.expiry.getTime();
	}
	public void expire()
	{
		this.expiry = Timestamp.now();
	}

	private final long duration;
	public long getDuration() { return this.duration; }

	private final PunishmentHolder holder;
	public PunishmentHolder getHolder() { return this.holder; }

	private final PunishmentSender sender;
	public PunishmentSender getSender() { return this.sender; }

}