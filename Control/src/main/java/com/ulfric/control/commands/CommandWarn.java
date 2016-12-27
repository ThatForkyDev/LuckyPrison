package com.ulfric.control.commands;

import org.bukkit.command.CommandSender;

import com.ulfric.control.commands.arg.PunishmentHolderStrategy;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.PunishmentSender;
import com.ulfric.control.entity.Warning;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;

public class CommandWarn extends SimpleCommand {

	public CommandWarn()
	{
		this.withIndexUnusedArgs();

		this.withArgument("holder", PunishmentHolderStrategy.INSTANCE, "control.warn_holder_needed");

		this.withArgument(Argument.builder()
								  .withPath("reason")
								  .withStrategy(ArgStrategy.ENTERED_STRING)
								  .withUsage("control.warn_reason_needed")
								  .withRemovalExclusion());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		PunishmentHolder holder = (PunishmentHolder) this.getObject("holder");

		if (!holder.canWarn())
		{
			Locale.sendError(sender, "control.warn_too_soon", holder.getName());

			return;
		}

		PunishmentSender executor = this.isPlayer() ? PunishmentSender.fromPlayer(this.getPlayer()) : PunishmentSender.CONSOLE;

		holder.warn(new Warning(executor, this.getUnusedArgs()));
	}

}