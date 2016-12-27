package com.ulfric.ess.commands;

import com.ulfric.ess.configuration.ConfigurationStore;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;

public class CommandDelwarp extends SimpleCommand {

	public CommandDelwarp()
	{
		this.withArgument("name", ArgStrategy.STRING, "ess.delwarp_name_empty");
	}

	@Override
	public void run()
	{
		String warpName = (String) getObject("name");

		if (ConfigurationStore.get().removeWarp(warpName))
		{
			Locale.sendSuccess(getSender(), "ess.delwarp", warpName);

			return;
		}

		Locale.sendError(getSender(), "ess.delwarp_failure", warpName);
	}

}