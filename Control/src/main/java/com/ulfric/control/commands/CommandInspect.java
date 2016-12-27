package com.ulfric.control.commands;

import org.apache.commons.lang.StringUtils;

import com.ulfric.control.commands.arg.PunishmentStrategy;
import com.ulfric.control.entity.Punishment;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.TimeUtils;

public class CommandInspect extends SimpleCommand {

	public CommandInspect()
	{
		this.withArgument("punishment", PunishmentStrategy.INSTANCE, "control.punishment_not_found");
	}

	@Override
	public void run()
	{
		Punishment punishment = (Punishment) this.getObject("punishment");
		String expiry;
		if (punishment.isExpired())
		{
			expiry = Strings.format(Locale.getMessage(this.getSender(), "control.punishment_expired"), TimeUtils.millisecondsToString(punishment.timeSinceExpiry()));
		}
		else
		{
			expiry = TimeUtils.millisecondsToString(punishment.timeTillExpiry());
		}

		Locale.send(this.getSender(), "control.punishment_view", punishment.getId(),
																 punishment.getHolder().getName(),
																 punishment.getSender().getName(),
																 StringUtils.capitalize(punishment.getType().name().toLowerCase()),
																 punishment.getReason(),
																 expiry,
																 punishment.getDate(),
																 TimeUtils.millisecondsToString(punishment.getDuration()));
	}

}