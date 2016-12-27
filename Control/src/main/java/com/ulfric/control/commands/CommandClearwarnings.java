package com.ulfric.control.commands;

import org.bukkit.command.CommandSender;

import com.ulfric.control.commands.arg.PunishmentHolderStrategy;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

public class CommandClearwarnings extends SimpleCommand {

	public CommandClearwarnings()
	{
		this.withArgument("holder", PunishmentHolderStrategy.INSTANCE, "control.warn_holder_needed");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		PunishmentHolder holder = (PunishmentHolder) this.getObject("holder");

		if (!holder.isWarned())
		{
			Locale.send(sender, "control.warn_none", holder.getName());

			return;
		}

		int count = holder.getWarnings().size();

		holder.clearWarnings();

		Locale.send(sender, "control.warn_cleared", holder.getName(), count);
	}

}