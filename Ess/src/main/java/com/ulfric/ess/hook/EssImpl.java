package com.ulfric.ess.hook;

import com.ulfric.ess.configuration.ConfigurationStore;
import com.ulfric.ess.modules.ModuleKits;
import com.ulfric.ess.modules.ModuleMotd;
import com.ulfric.ess.modules.ModuleSpawnpoint;
import com.ulfric.lib.api.hook.EssHook.IEss;
import com.ulfric.lib.api.hook.EssHook.IKit;
import com.ulfric.lib.api.hook.EssHook.IWarp;
import com.ulfric.lib.api.location.LocationProxy;

public enum EssImpl implements IEss {

	INSTANCE;

	@Override
	public LocationProxy getSpawnpoint()
	{
		return ModuleSpawnpoint.get().getSpawn();
	}

	@Override
	public IWarp warp(String name)
	{
		return new WarpImpl(ConfigurationStore.get().getWarp(name));
	}

	@Override
	public IKit kit(String name)
	{
		return new KitImpl(ModuleKits.get().getKit(name));
	}

	@Override
	public void clearMotd()
	{
		ModuleMotd.get().clear();
	}

}