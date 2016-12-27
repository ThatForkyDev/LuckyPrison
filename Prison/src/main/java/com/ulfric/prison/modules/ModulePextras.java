package com.ulfric.prison.modules;

import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.prison.commands.CommandPexfix;
import com.ulfric.prison.commands.CommandPexr;
import com.ulfric.prison.commands.CommandPexrel;

public class ModulePextras extends SimpleModule {

	public ModulePextras()
	{
		super("pextras", "Extra PermissionsEx commands", "Packet", "1.0.0-REL");

		this.addCommand("pexr", new CommandPexr());
		this.addCommand("pexfix", new CommandPexfix());
		this.addCommand("pexrel", new CommandPexrel());
	}

}