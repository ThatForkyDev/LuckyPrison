package com.ulfric.xmap;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.Plugin;
import com.ulfric.xmap.hook.XMapHookImpl;
import com.ulfric.xmap.map.MapModule;

public class XMap extends Plugin {

	private static XMap i;
	public static XMap get() { return XMap.i; }

	@Override
	public void load()
	{
		XMap.i = this;

		this.withSubModule(new MapModule());

		this.registerHook(Hooks.XMAP, XMapHookImpl.INSTANCE);
	}

	@Override
	public void enable()
	{
		// NOTHING
	}

	@Override
	public void disable()
	{
		XMap.i = null;
	}

}