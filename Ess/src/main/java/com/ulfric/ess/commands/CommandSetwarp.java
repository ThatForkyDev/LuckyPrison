package com.ulfric.ess.commands;

import com.ulfric.ess.configuration.ConfigurationStore;
import com.ulfric.ess.entity.Warp;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.location.LocationUtils;

public class CommandSetwarp extends SimpleCommand {

	public CommandSetwarp()
	{
		this.withEnforcePlayer();

		this.withArgument("int", ArgStrategy.POSITIVE_INTEGER, 5);

		this.withArgument("name", ArgStrategy.STRING, "ess.setwarp_name_empty");
	}

	@Override
	public void run()
	{
		String name = (String) this.getObject("name");

		int delay = (int) this.getObject("int");

		ConfigurationStore.get().addWarp(new Warp(name, LocationUtils.proxy(this.getLocation()), delay));

		Locale.sendSuccess(this.getPlayer(), "ess.setwarp", name, delay);
	}

}