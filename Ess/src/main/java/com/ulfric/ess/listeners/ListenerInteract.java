package com.ulfric.ess.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.ulfric.ess.lang.Meta;

public class ListenerInteract implements Listener {

	@EventHandler
	public void onDeath(PlayerDeathEvent event)
	{
		event.setDeathMessage(null);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageEvent event)
	{
		if (!event.getEntityType().equals(EntityType.PLAYER)) return;

		if (!event.getEntity().hasMetadata(Meta.GODMODE)) return;

		event.setCancelled(true);
	}

}