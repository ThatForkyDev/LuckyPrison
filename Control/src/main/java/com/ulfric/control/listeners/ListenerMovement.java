package com.ulfric.control.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ulfric.control.lang.Meta;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.player.PlayerMoveBlockEvent;

public class ListenerMovement implements Listener {


	@EventHandler(ignoreCancelled = true)
	public void onMove(PlayerMoveBlockEvent event)
	{
		Metadata.remove(event.getPlayer(), Meta.HAS_NOT_MOVED);
	}


}