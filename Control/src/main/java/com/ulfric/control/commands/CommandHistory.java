package com.ulfric.control.commands;

import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.commands.arg.PunishmentHolderStrategy;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Map.Entry;

public class CommandHistory extends SimpleCommand {

	public CommandHistory()
	{
		this.withArgument("holder", PunishmentHolderStrategy.INSTANCE, "control.specify_valid_holder");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		PunishmentHolder holder = (PunishmentHolder) this.getObject("holder");

		Map<PunishmentType, Map<Integer, Punishment>> found = PunishmentCache.getPunishments(holder);

		if (CollectUtils.isEmpty(found))
		{
			Locale.sendError(sender, "control.recent_none");

			return;
		}

		String name = holder.getName();

		ComponentBuilder message = new ComponentBuilder(Strings.format(Locale.getMessage(sender, "control.history"), name));

		String format = Locale.getMessage(sender, "control.recent_format");

		String click = Locale.getMessage(sender, "control.recent_click");

		String type = Locale.getMessage(sender, "control.punishment_type");

		int total = 0;

		for (Entry<PunishmentType, Map<Integer, Punishment>> entry : found.entrySet())
		{
			Map<Integer, Punishment> punishments = entry.getValue();

			if (CollectUtils.isEmpty(punishments)) continue;

			total += punishments.size();

			message.append(Strings.format(type, entry.getKey()));

			for (Punishment punishment : punishments.values())
			{
				message.append("\n");

				int id = punishment.getId();

				message.append(Strings.format(format, id, punishment.getType(), name, punishment.getSender().getName()));
				message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											 TextComponent.fromLegacyText(Strings.format(click, id))));
				message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect " + id));
			}
		}

		message.append("\n");
		message.append(Strings.format(Locale.getMessage(sender, "control.history_count"), total));

		sender.sendMessage(message.create());
	}

}
