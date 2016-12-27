package com.ulfric.ess.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerFirstJoinEvent;

public class ListenerConnection implements Listener {

	@EventHandler
	public void onFirstJoin(PlayerFirstJoinEvent event)
	{
		Locale.sendMass("ess.first_join", event.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		if (!GameMode.CREATIVE.equals(player.getGameMode())) return;

		if (player.hasPermission("ess.gamemode")) return;

		player.setGameMode(GameMode.SURVIVAL);
	}

}