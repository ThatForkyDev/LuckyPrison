package com.ulfric.control.commands;

import org.bukkit.command.CommandSender;

import com.ulfric.control.commands.arg.PunishmentHolderStrategy;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.Warning;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.TimeUtils;

public class CommandWarnings extends SimpleCommand {

	public CommandWarnings()
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

		StringBuilder builder = new StringBuilder(Strings.format(Locale.getMessage(sender, "control.warn_header"), holder.getName()));
		builder.append('\n');
		builder.append(Strings.format(Locale.getMessage(sender, "control.warn_able"), holder.canWarn()));
		String format = Locale.getMessage(sender, "control.warn_format");
		String reasonFormat = Locale.getMessage(sender,"control.warn_reason_format");

		for (Warning warning : holder.getWarnings())
		{
			builder.append('\n');
			builder.append(Strings.format(format, warning.getSender().resolveName(), TimeUtils.millisecondsToString(warning.getTimestamp().timeSince())));
			builder.append('\n');
			String reason = warning.getReason();
			builder.append(Strings.format(reasonFormat, reason.length() > 50 ? reason.substring(0,50)+"..." : reason));
		}

		sender.sendMessage(builder.toString());
	}

}