package com.ulfric.chat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.task.Tasks;

public class ListenerConnection implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		event.setJoinMessage(null);

		Player player = event.getPlayer();

		Tasks.runAsync(() ->
		{
			String name = player.getName();

			for (Player send : Bukkit.getOnlinePlayers())
			{
				if (!Hooks.DATA.getPlayerDataAsBoolean(send.getUniqueId(), Meta.JOINQUIT)) continue;

				Locale.send(send, "chat.join", name);
			}
		});

		if (!player.hasPermission("chat.nickname")) return;

		String name = Hooks.DATA.getPlayerDataAsString(player.getUniqueId(), Meta.NICKNAME_DATA);

		if (name == null || name.isEmpty()) return;

		player.setDisplayName(name);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		event.setQuitMessage(null);

		Tasks.runAsync(() ->
		{
			String name = event.getPlayer().getName();

			for (Player send : Bukkit.getOnlinePlayers())
			{
				if (!Hooks.DATA.getPlayerDataAsBoolean(send.getUniqueId(), Meta.JOINQUIT)) continue;

				Locale.send(send, "chat.quit", name);
			}
		});
	}

}