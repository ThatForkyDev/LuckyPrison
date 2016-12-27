package com.ulfric.xmap.hook;

import com.ulfric.lib.api.hook.XMapHook.IXMapHook;
import com.ulfric.lib.api.hook.XMapHook.MapEngine;
import com.ulfric.lib.api.hook.XMapHook.XMap;

public enum XMapHookImpl implements IXMapHook {

	INSTANCE;

	private MapEngine engine;

	@Override
	public void buildEngine(boolean cache)
	{
		this.engine = new MapEngineImpl(cache);
	}

	public void clearCache()
	{
		this.engine = null;
	}

	@Override
	public XMap map(String name)
	{
		return this.engine.getMap(name);
	}

}