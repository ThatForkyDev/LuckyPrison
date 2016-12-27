package com.ulfric.control.commands;

import com.ulfric.control.commands.arg.PunishmentStrategy;
import com.ulfric.control.entity.Punishment;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

public class CommandExpire extends SimpleCommand {

	public CommandExpire()
	{
		this.withArgument("punishment", PunishmentStrategy.INSTANCE, "control.punishment_not_found");
	}

	@Override
	public void run()
	{
		Punishment punishment = (Punishment) this.getObject("punishment");

		if (punishment.isExpired())
		{
			Locale.sendError(this.getSender(), "control.punishments_already_expired", punishment.getId());

			return;
		}

		punishment.expire();

		Locale.sendSuccess(this.getSender(), "control.punishments_expire_single", punishment.getId(), punishment.getType(), punishment.getHolder().getName());
	}

}