package com.ulfric.ess.commands;

import java.util.Optional;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.world.Clock;
import com.ulfric.lib.api.world.TimeType;

public class CommandPlayertime extends SimpleCommand {

	public CommandPlayertime()
	{
		this.withEnforcePlayer();
		this.withArgument("time", ArgStrategy.MC_TIME);
		this.withArgument("relative", ArgStrategy.BOOLEAN);
	}

	@Override
	public void run()
	{
		TimeType type = (TimeType) this.getObject("time");
		boolean relative = Optional.ofNullable((Boolean) this.getObject("relative")).orElse(false);
		if (type == null || !this.hasPermission(Permissions.PLAYER_TIME_SET))
		{
			long time = this.getPlayer().getPlayerTime();

			Locale.send(this.getPlayer(), "ess.time", time, Clock.parse(time).get());

			return;
		}

		this.getPlayer().setPlayerTime(type.getTime(), relative);

		Locale.sendSuccess(this.getPlayer(), "ess.time_set", type.get());
	}


	public static class CommandNight extends SimpleCommand
	{

		@Override
		public void run()
		{
			Commands.dispatch(this.getSender(), "ptime " + TimeType.NIGHT.getTime() + " false");
		}

	}

	public static class CommandDay extends SimpleCommand
	{

		@Override
		public void run()
		{
			Commands.dispatch(this.getSender(), "ptime " + TimeType.DAY.getTime() + " false");
		}

	}

}