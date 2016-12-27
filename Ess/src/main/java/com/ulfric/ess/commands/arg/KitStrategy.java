package com.ulfric.ess.commands.arg;

import com.ulfric.ess.entity.Kit;
import com.ulfric.ess.modules.ModuleKits;
import com.ulfric.lib.api.command.arg.ArgStrategy;

public enum KitStrategy implements ArgStrategy<Kit> {

	INSTANCE;

	@Override
	public Kit match(String string)
	{
		return ModuleKits.get().getKit(string);
	}

}