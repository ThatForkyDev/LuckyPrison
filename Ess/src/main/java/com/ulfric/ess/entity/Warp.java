package com.ulfric.ess.entity;

import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.location.LocationProxy;
import com.ulfric.uspigot.Locatable;
import org.bukkit.Location;

public class Warp implements Named, Locatable {

	public Warp(String name, LocationProxy location, int warmup)
	{
		this.name = name;

		this.location = location;

		this.warmup = warmup;
	}

	private final String name;
	@Override
	public String getName() { return this.name; }

	private final LocationProxy location;
	@Override
	public Location getLocation() { return this.location.getLocation(); }

	private final int warmup;
	public int getWarmup() { return this.warmup; }

	@Override
	public int hashCode()
	{
		return this.getName().hashCode();
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof Named)) return false;

		Named other = (Named) object;

		return other.getName().equalsIgnoreCase(this.name);
	}

}
