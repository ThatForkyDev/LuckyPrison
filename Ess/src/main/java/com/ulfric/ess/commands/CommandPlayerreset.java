package com.ulfric.ess.commands;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

public class CommandPlayerreset extends SimpleCommand {

	public CommandPlayerreset()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		this.getPlayer().resetPlayerTime();
		this.getPlayer().resetPlayerWeather();

		Locale.sendSuccess(this.getPlayer(), "ess.player_time_reset");
	}

}