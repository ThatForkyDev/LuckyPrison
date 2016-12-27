package com.ulfric.xmap.hook;

import com.ulfric.lib.api.hook.CachingEngine;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.XMapHook.MapEngine;
import com.ulfric.lib.api.hook.XMapHook.XMap;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.xmap.map.MapLoader;

public class MapEngineImpl extends CachingEngine<String, XMap> implements MapEngine {

	public MapEngineImpl(boolean caching)
	{
		super(caching);

		Assert.isTrue(Hooks.XMAP.isModuleEnabled());
	}

	@Override
	public XMap getMap(String name)
	{
		name = name.toLowerCase();

		if (!this.isCaching())
		{
			com.ulfric.xmap.map.XMapCanvas map = MapLoader.getMap(name);

			if (map == null) return null;

			return new XMapImpl(map);
		}

		XMap map = this.getCached(name);

		if (map != null) return map;

		com.ulfric.xmap.map.XMapCanvas mapObj = MapLoader.getMap(name);

		if (mapObj == null) return null;

		map = new XMapImpl(mapObj);

		this.cache(name, map);

		return map;
	}

}