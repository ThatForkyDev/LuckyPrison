package com.ulfric.control.commands;

import com.google.common.collect.Sets;
import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.task.Tasks;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class CommandSeenip extends PunishmentCommand {

	public CommandSeenip()
	{
		super(Argument.builder().withPath("inet").withStrategy(ArgStrategy.INET).withUsage("control.specify_valid_ip").build());
	}

	@Override
	public void execute()
	{
		CommandSender sender = this.getSender();
		String ip = (String) this.getObject("inet");
		Locale.send(sender, "system.data_lookup");

		Tasks.runAsync(() ->
		{
			Set<UUID> set = Sets.newHashSet();

			for (Entry<UUID, Object> entry : Hooks.DATA.getAllData("control.ips").entrySet())
			{
				@SuppressWarnings("unchecked")
				Collection<String> ips = (Collection<String>) entry.getValue();

				if (!CollectUtils.anyContains(ips, ip)) continue;

				set.add(entry.getKey());
			}

			int length = set.size();

			if (length > 0)
			{
				ComponentBuilder message = new ComponentBuilder(Locale.getMessage(sender, "control.ips_found"));

				String click = Locale.getMessage(sender, "control.ips_click");
				String banned =  Locale.getMessage(sender, "control.ips_banned");

				boolean first = true;
				for (UUID uuid : set)
				{
					if (!first)
					{
						message.append(", ").color(ChatColor.GRAY);
					}
					else
					{
						first = false;
					}

					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					String name = player.getName();
					if (name == null || name.isEmpty()) continue;

					message.append(name);
					message.color(player.isOnline() ? ChatColor.GREEN : ChatColor.RED);
					message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												 TextComponent.fromLegacyText(Strings.formatF(click, name))));
					message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/knownips " + name));

					if (!CollectUtils.isEmpty(PunishmentCache.getValidPunishments(PunishmentHolder.fromUUID(player.getUniqueId()), PunishmentType.BAN)))
					{
						message.append(" ✖").color(ChatColor.DARK_RED);
						message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													 TextComponent.fromLegacyText(Strings.format(banned, name))));
					}
				}

				this.getSender().sendMessage(message.create());
			}

			Locale.sendSuccess(sender, "control.ip_results", length);
		});
	}

}
