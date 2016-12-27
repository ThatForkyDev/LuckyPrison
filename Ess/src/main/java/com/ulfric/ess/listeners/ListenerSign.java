package com.ulfric.ess.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.ulfric.ess.configuration.ConfigurationStore;
import com.ulfric.ess.entity.StatusSign;
import com.ulfric.ess.lang.Meta;
import com.ulfric.ess.lang.Permissions;
import com.ulfric.ess.tasks.TaskUpdatestatus;
import com.ulfric.lib.api.block.SignUtils;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUseSignEvent;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.time.Timestamp;
import com.ulfric.lib.api.tuple.Pair;

public class ListenerSign implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onSignBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();

		if (!SignUtils.isSign(block)) return;

		ConfigurationStore.get().removeSign((Sign) block.getState());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onSignPlace(SignChangeEvent event)
	{
		if (event.getPlayer().hasPermission(Permissions.SIGN_COLOR))
		{
			for (int x = 0; x < event.getLines().length; x++)
			{
				event.setLine(x, Chat.color(event.getLine(x)));
			}
		}

		if (!event.getPlayer().hasPermission("lib.sign.placement")) return;

		String line = ChatColor.stripColor(event.getLine(0)).toLowerCase();

		if (!line.equals("[status]")) return;

		OfflinePlayer target = PlayerUtils.getOffline(event.getLine(1));

		if (target == null) return;

		StatusSign sign = StatusSign.create(event.getBlock().getLocation(), target.getUniqueId(), Hooks.PERMISSIONS.user(target.getUniqueId()).getGroupName(), Timestamp.of(target.getLastPlayed()));

		ConfigurationStore.get().getSigns().add(sign);

		TaskUpdatestatus.get().updateSign(sign);
	}

	@EventHandler(ignoreCancelled = true)
	public void onClick(PlayerUseSignEvent event)
	{
		Player player = event.getPlayer();

		if (player.hasMetadata(Meta.SIGN_CMD))
		{
			ConfigurationStore.get().addCommand(event.getSign(), Metadata.getValueAsString(player, Meta.SIGN_CMD));

			Metadata.remove(player, Meta.SIGN_CMD);

			Locale.sendSuccess(player, "ess.sign_command");

			return;
		} else if (player.hasMetadata(Meta.SIGN_CHANGE))
		{
			Pair<Integer, String> tuple = Metadata.getValue(player, Meta.SIGN_CHANGE);

			Metadata.remove(player, Meta.SIGN_CHANGE);

			event.getSign().setLine(tuple.getA(), tuple.getB());

			Locale.sendSuccess(player, "ess.sign_change");

			return;
		}

		List<String> commands = ConfigurationStore.get().getCommands(event.getSign());

		if (CollectUtils.isEmpty(commands)) return;

		for (String string : commands)
		{
			string = string.replace(Strings.PLAYER, player.getName()).replace("<uuid>", player.getUniqueId().toString());

			if (string.startsWith("[P]"))
			{
				Commands.dispatch(player, string.replace("[P]", Strings.BLANK).trim());

				continue;
			}

			Commands.dispatch(string);
		}
	}


}