package com.ulfric.prison.modules;

import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.prison.commands.CommandBackpack;

public class ModuleBackpacks extends SimpleModule {

	public ModuleBackpacks()
	{
		super("backpack", "Backpack feature module", "Packet", "1.0.0-SNAPSHOT");

		this.addCommand("backpack", new CommandBackpack());
	}

}