package com.ulfric.control.commands;

import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.entity.Punishment;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class CommandRecent extends SimpleCommand {

	public CommandRecent()
	{
		this.withArgument("int", ArgStrategy.POSITIVE_INTEGER, 10);
	}

	@Override
	public void run()
	{
		int search = (int) this.getObject("int");

		if (search < 1)
		{
			Locale.sendError(this.getSender(), "control.recent_positive");
			return;
		}

		int current = PunishmentCache.getPunishmentTotal();
		if (search > current) search = current;

		if (search == 0)
		{
			Locale.sendError(this.getSender(), "control.recent_none");
			return;
		}

		CommandSender sender = this.getSender();
		ComponentBuilder message = new ComponentBuilder(Locale.getMessage(this.getSender(), "control.recent"));

		String format = Locale.getMessage(this.getSender(), "control.recent_format");
		String click = Locale.getMessage(this.getSender(), "control.recent_click");

		for (int id = current-search; id < current; id++)
		{
			Punishment punishment = PunishmentCache.getPunishmentById(id);
			if (punishment == null) continue;

			String name = punishment.getHolder().getName();
			if (punishment.getHolder().isIp() && !sender.hasPermission("control.seeip"))
			{
				name = "<IP HIDDEN>";
			}

			message.append("\n");

			message.append(Strings.format(format, id, punishment.getType(), name, punishment.getSender().getName()));
			message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										 TextComponent.fromLegacyText(Strings.format(click, id))));
			message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect " + id));
		}

		sender.sendMessage(message.create());
	}

}
