package com.ulfric.ess.hook;

import org.bukkit.Location;

import com.ulfric.ess.entity.Warp;
import com.ulfric.lib.api.hook.EssHook.IWarp;

public class WarpImpl implements IWarp {

	protected WarpImpl(Warp warp)
	{
		this.warp = warp;
	}

	private final Warp warp;

	@Override
	public String getName()
	{
		return this.warp.getName();
	}

	@Override
	public Location getLocation()
	{
		return this.warp.getLocation();
	}

	@Override
	public int getWarmup()
	{
		return this.warp.getWarmup();
	}

}