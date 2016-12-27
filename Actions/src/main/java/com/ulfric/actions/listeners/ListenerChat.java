package com.ulfric.actions.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.ulfric.actions.persist.LogFile;

final class ListenerChat implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Chat ({0})", event.getMessage());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Command ({0})", event.getMessage());
	}

}