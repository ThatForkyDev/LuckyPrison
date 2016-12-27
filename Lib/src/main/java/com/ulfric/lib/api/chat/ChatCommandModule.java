package com.ulfric.lib.api.chat;

import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatCommandModule extends SimpleModule {

	public ChatCommandModule()
	{
		super("chatcommand", "Command handling on the chat thread", "Packet", "1.0.0-REL");

		this.addListener(new Listener() {
			@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
			public void onChat(AsyncPlayerChatEvent event)
			{
				String raw = event.getMessage();

				if (!raw.startsWith("!")) return;

				Player player = event.getPlayer();

				if (!player.hasPermission("lib.commandchat")) return;

				event.setCancelled(true);

				String message = raw.substring(1);

				String[] split = message.split("\\s+");

				PluginCommand command = Bukkit.getPluginCommand(split[0]);

				if (command == null || !command.isRegistered())
				{
					Locale.sendError(player, "lib.command_disabled");

					return;
				}

				if (!command.testPermission(player)) return;

				Bukkit.getLogger().info("[AsyncCmd] " + player.getName() + ": " + raw);

				String[] args = split.length <= 1 ? new String[0] : ArrayUtils.subarray(split, 1, split.length);

				Locale.sendSuccess(player, "lib.command_executed", command.execute(player, command.getLabel(), args));
			}
		});
	}

}
