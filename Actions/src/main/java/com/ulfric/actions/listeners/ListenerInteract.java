package com.ulfric.actions.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemStack;

import com.ulfric.actions.persist.LogFile;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.player.PlayerDamagePlayerEvent;

final class ListenerInteract implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShock(LightningStrikeEvent event)
	{
		Player player = Metadata.getTied(event.getLightning());

		if (player == null) return;

		if (!LogFile.shouldLog(player)) return;

		LogFile.log(player, "Lightning Strike ({0})", LocationUtils.toString(event.getLightning().getLocation(), true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTame(EntityTameEvent event)
	{
		Player player = (Player) event.getOwner();

		if (!LogFile.shouldLog(player)) return;

		LogFile.log(player, "Tame ({0}) ({1})", event.getEntityType(), LocationUtils.toString(event.getEntity().getLocation(), true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onIgnite(BlockIgniteEvent event)
	{
		Player player = event.getPlayer();

		if (player == null) return;

		if (!LogFile.shouldLog(player)) return;

		LogFile.log(player, "Ignite ({0})", LocationUtils.toString(event.getBlock().getLocation(), true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGamemodeChange(PlayerGameModeChangeEvent event)
	{
		Player player = event.getPlayer();

		if (!LogFile.shouldLog(player)) return;

		LogFile.log(player, "Gamemode change ({0} -> {1})", player.getGameMode(), event.getNewGameMode());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), () ->
		{
			Location from = event.getFrom(), to = event.getTo();

			if (from.equals(to))
			{
				return Strings.format("Teleport ({0}) ({1})", event.getCause(), LocationUtils.toString(to, true));
			}

			return Strings.format("Teleport ({0}) ({1} -> {2})", event.getCause(), LocationUtils.toString(from, true),  LocationUtils.toString(to, true));
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSheer(PlayerShearEntityEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Sheer ({0}) ({1})", event.getEntity().getType(), LocationUtils.toString(event.getEntity().getLocation(), true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldChange(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();

		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(player, "World Change ({0} -> {1})", event.getFrom().getName(), player.getWorld().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDie(PlayerDeathEvent event)
	{
		Player player = event.getEntity();

		if (!LogFile.shouldLog(player)) return;

		LogFile.log(player, "Death ({0}) (Keep Inv: {1}) (Keep Level: {2})", LocationUtils.toString(player.getLocation(), true), event.getKeepInventory(), event.getKeepLevel());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPvP(PlayerDamagePlayerEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Attack ({0})", event.getDamaged().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRightClickEntity(PlayerInteractAtEntityEvent event)
	{
		Player player = event.getPlayer();

		if (!LogFile.shouldLog(player)) return;

		LogFile.log(player, () ->
		{
			ItemStack item = player.getItemInHand();

			if (ItemUtils.isEmpty(item))
			{
				return Strings.format("Interact (ENTITY) ({0})", event.getRightClicked().getType());
			}

			return Strings.format("Interact (ENTITY) ({0}) ({1})", event.getRightClicked().getType(), ItemUtils.toString(item));
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBedEnter(PlayerBedEnterEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Bed (ENTER) ({0})", LocationUtils.toString(event.getBed().getLocation(), true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBedExit(PlayerBedLeaveEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Bed (EXIT) ({0})", LocationUtils.toString(event.getBed().getLocation(), true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBucket(PlayerBucketFillEvent event)
	{
		this.onBucketEvent(event);
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBucket(PlayerBucketEmptyEvent event)
	{
		this.onBucketEvent(event);
	}
	private void onBucketEvent(PlayerBucketEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Bucket ({0} -> {1})", event.getBucket(), event.getItemStack().getType());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Block (BREAK) ({0}) ({1})", event.getBlock().getType(), LocationUtils.toString(event.getBlock().getLocation(), true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockPlaceEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Block (PLACE) ({0}) ({1})", event.getBlock().getType(), LocationUtils.toString(event.getBlock().getLocation(), true));
	}

}