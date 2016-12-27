package com.ulfric.control.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.ulfric.lib.api.player.PlayerDamageEntityEvent;

public class ListenerInteract implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onRightClick(PlayerInteractEvent event)
	{
		Action action = event.getAction();

		if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_AIR)) return;

		if (!event.getPlayer().hasMetadata("aa.interact")) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDamage(PlayerDamageEntityEvent event)
	{
		if (!event.getPlayer().hasMetadata("aa.damage")) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPickup(PlayerPickupItemEvent event)
	{
		if (!event.getPlayer().hasMetadata("aa.pickup")) return;

		event.setCancelled(true);
	}

	/*@EventHandler(ignoreCancelled = true)
	public void onSign(SignChangeEvent event)
	{
		Player player = event.getPlayer();

		if (player.hasPermission("control.bypass.chat")) return;

		if (CollectUtils.isEmpty(PunishmentCache.getValidPunishments(player, PunishmentType.MUTE))) return;

		event.setCancelled(true);

		Locale.sendError(player, "control.muted");
	}*/

}