package com.ulfric.ess.entity;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import com.ulfric.lib.api.java.Unique;
import com.ulfric.lib.api.time.Timestamp;

public class StatusSign implements Unique {

	public static StatusSign create(Location location, UUID uuid, String rank, Timestamp timestamp)
	{
		try
		{
			StatusSign sign = new StatusSign((Sign) location.getBlock().getState(), uuid, rank, timestamp);

			return sign;
		}
		catch (Throwable t) { }

		return null;
	}

	private StatusSign(Sign sign, UUID uuid, String rank, Timestamp timestamp)
	{
		this.sign = sign;

		this.uuid = uuid;

		this.rank = rank;

		this.time = timestamp;
	}

	private final UUID uuid;
	@Override
	public UUID getUniqueId() { return this.uuid; }

	private final String rank;
	public String getRank() { return this.rank; }

	private final Sign sign;
	public Sign getSign() { return this.sign; }

	private final Timestamp time;
	public Timestamp getTime() { return this.time; }

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof StatusSign)) return false;

		StatusSign other = (StatusSign) object;

		return other.getUniqueId().equals(this.getUniqueId()) && other.getSign().getLocation().equals(this.getSign().getLocation());
	}

}