package com.ulfric.ess.commands;

import org.bukkit.Location;

import com.ulfric.ess.modules.ModuleSpawnpoint;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ExactArg;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.location.LocationUtils;

public class CommandSetspawn extends SimpleCommand {

	public CommandSetspawn()
	{
		this.withEnforcePlayer();

		this.withArgument("rnd", new ExactArg("--round"));
	}

	@Override
	public void run()
	{
		Location location = this.getLocation();

		if (this.hasObject("rnd"))
		{
			location = LocationUtils.center(location.clone());
		}

		ModuleSpawnpoint.get().setSpawn(location);

		Locale.sendSuccess(this.getPlayer(), "ess.setspawn", LocationUtils.toString(location));
	}

}