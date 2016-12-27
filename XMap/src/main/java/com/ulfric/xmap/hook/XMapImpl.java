package com.ulfric.xmap.hook;

import org.bukkit.map.MapView.Scale;

import com.ulfric.lib.api.hook.XMapHook.XMap;

public class XMapImpl implements XMap {

	XMapImpl(com.ulfric.xmap.map.XMapCanvas map)
	{
		this.map = map;
	}

	private com.ulfric.xmap.map.XMapCanvas map;

	@Override
	public String getName()
	{
		return this.map.getName();
	}

	@Override
	public byte getBackground()
	{
		return this.map.getBackground();
	}

	@Override
	public Scale getScale()
	{
		return this.map.getScale();	
	}

	@Override
	public int getRendererCount()
	{
		return this.map.getTotalRenderers();
	}

}