package com.ulfric.ess.commands.arg;

import com.ulfric.ess.configuration.ConfigurationStore;
import com.ulfric.ess.entity.Warp;
import com.ulfric.lib.api.command.arg.ArgStrategy;

public enum WarpStrategy implements ArgStrategy<Warp> {

	INSTANCE;

	@Override
	public Warp match(String string)
	{
		return ConfigurationStore.get().getWarp(string);
	}

}