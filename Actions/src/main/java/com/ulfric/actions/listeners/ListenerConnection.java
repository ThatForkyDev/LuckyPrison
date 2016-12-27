package com.ulfric.actions.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ulfric.actions.persist.LogFile;
import com.ulfric.lib.api.java.Strings;

final class ListenerConnection implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();

		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(player, Strings.format("Logged in ({0}) ({1})", event.getAddress().toString(), event.getResult().name()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.exit(event.getPlayer(), "Logged out ({0})", event.getQuitMessage());
	}

}