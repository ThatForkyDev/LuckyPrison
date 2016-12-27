package com.ulfric.actions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.ulfric.actions.persist.LogFile;
import com.ulfric.uspigot.event.server.ServerShutdownEvent;

final class ListenerShutdown implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onShutdown(ServerShutdownEvent event)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (!LogFile.shouldLog(player)) continue;

			LogFile.log(player, "Logged out (SHUTDOWN)");
		}
	}

}