package com.ulfric.chat.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.task.Tasks;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class CommandIgnore extends SimpleCommand {

	public CommandIgnore()
	{
		this.withEnforcePlayer();
		this.withArgument("player", ArgStrategy.OFFLINE_PLAYER);
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();
		// The returned list is mutable and is not a copy of the backing data.
		// It can be directly modified to change the data stored for that player.
		List<String> ignoring = Hooks.DATA.getPlayerDataAsStringList(player.getUniqueId(), Meta.IGNORE);

		if (this.hasObjects())
		{
			OfflinePlayer target = (OfflinePlayer) this.getObject("player");

			if (ignoring.contains(target.getUniqueId().toString()))
			{
				ignoring.remove(target.getUniqueId().toString());
				Locale.sendSuccess(player, "chat.ignore_off", target.getName());
				Hooks.DATA.setPlayerData(player.getUniqueId(), Meta.IGNORE, ignoring);
				return;
			}

			ignoring.add(target.getUniqueId().toString());
			Locale.sendSuccess(player, "chat.ignore", target.getName());
			Hooks.DATA.setPlayerData(player.getUniqueId(), Meta.IGNORE, ignoring);
			return;
		}

		Locale.send(player, "chat.ignore_fetching");

		// Prevent CME when using this list from another thread as it is the live copy
		List<String> uuidStrings = ImmutableList.copyOf(ignoring);

		Tasks.runAsync(() ->
		{
			Map<String, Boolean> names = Maps.newHashMap();

			for (String string : uuidStrings)
			{
				OfflinePlayer oplayer = PlayerUtils.getOffline(string);
				if (oplayer == null) continue;

				names.put(oplayer.getName(), oplayer.isOnline());
			}

			if (names.isEmpty())
			{
				Locale.sendError(player, "chat.ignore_empty");
				return;
			}

			ComponentBuilder message = new ComponentBuilder(Locale.getMessage(player, "chat.ignore_list"));

			boolean first = true;
			for (Map.Entry<String, Boolean> name : names.entrySet())
			{
				if (!first)
				{
					message.append(", ").color(ChatColor.YELLOW);
				}
				else
				{
					first = false;
				}

				message.append(name.getKey()).color(name.getValue() ? ChatColor.GREEN : ChatColor.GRAY);
			}

			player.sendMessage(message.create());
		});
	}
}
