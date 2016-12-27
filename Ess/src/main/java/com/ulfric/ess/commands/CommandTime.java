package com.ulfric.ess.commands;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.world.Clock;
import com.ulfric.lib.api.world.TimeType;

public class CommandTime extends SimpleCommand {


	public CommandTime()
	{
		this.withEnforcePlayer();

		this.withArgument("time", ArgStrategy.MC_TIME);
	}


	@Override
	public void run()
	{
		TimeType type = (TimeType) this.getObject("time");
		if (type == null || !this.hasPermission(Permissions.TIME_SET))
		{
			long time = this.getPlayer().getWorld().getTime();

			Locale.send(this.getPlayer(), "ess.time", time, Clock.parse(time).get());

			return;
		}

		this.getPlayer().getWorld().setTime(type.getTime());

		Locale.sendSuccess(this.getPlayer(), "ess.time_set", type.get());
	}


	public static class CommandNight extends SimpleCommand
	{

		@Override
		public void run()
		{
			Commands.dispatch(this.getSender(), "time " + TimeType.NIGHT.getTime());
		}

	}

	public static class CommandDay extends SimpleCommand
	{

		@Override
		public void run()
		{
			Commands.dispatch(this.getSender(), "time " + TimeType.DAY.getTime());
		}

	}


}