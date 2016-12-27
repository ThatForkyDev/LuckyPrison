package com.ulfric.actions.listeners;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import com.ulfric.actions.persist.LogFile;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.location.LocationUtils;

final class ListenerInventory implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();

		if (!LogFile.shouldLog(player)) return;

		ItemStack item = event.getCurrentItem();

		if (ItemUtils.isEmpty(item)) return;

		LogFile.log(player, "Inventory ({0}) ({1})", event.getAction(), ItemUtils.toString(item));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Item drop ({0})", ItemUtils.toString(event.getItemDrop().getItemStack()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEnchant(EnchantItemEvent event)
	{
		if (!LogFile.shouldLog(event.getEnchanter())) return;

		LogFile.log(event.getEnchanter(), "Enchant ({0}) ({1})", event.getEnchantsToAdd(), ItemUtils.toString(event.getItem()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBookEdit(PlayerEditBookEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Author ({0} -> {1})", event.getPreviousBookMeta().getPageCount(), event.getNewBookMeta().getPageCount());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Consume ({0})", ItemUtils.toString(event.getItem()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFish(PlayerFishEvent event)
	{
		if (!LogFile.shouldLog(event.getPlayer())) return;

		LogFile.log(event.getPlayer(), "Fish ({0})", event.getState());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onOpen(InventoryOpenEvent event)
	{
		this.logInventoryEvent(event, (Player) event.getPlayer(), "OPEN");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClose(InventoryCloseEvent event)
	{
		this.logInventoryEvent(event, (Player) event.getPlayer(), "CLOSE");
	}

	private void logInventoryEvent(InventoryEvent event, Player player, String action)
	{
		if (player == null || !LogFile.shouldLog(player)) return;

		String actionClone = action;

		LogFile.log(player, () ->
		{
			Inventory inventory = event.getInventory();
			InventoryHolder holder = inventory.getHolder();

			if (holder instanceof BlockState)
			{
				BlockState block = (BlockState) holder;
				return Strings.format("Inventory {0} {1} {2} {3}", actionClone, inventory.getType(), block.getType(), LocationUtils.toString(block.getLocation(), true));
			}
			else if (holder instanceof Entity)
			{
				Entity entity = (Entity) holder;
				return Strings.format("Inventory {0} {1} {2} {3}", actionClone, inventory.getType(), entity.getType(), LocationUtils.toString(entity.getLocation(), true));
			}

			return Strings.format("Inventory {0} {1}", actionClone, inventory.getType());
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLaunch(ProjectileLaunchEvent event)
	{
		ProjectileSource sauce = event.getEntity().getShooter();

		if (!(sauce instanceof Player)) return;

		Player player = (Player) sauce;

		if (!LogFile.shouldLog(player)) return;

		LogFile.log(player, "Shoot ({0})", event.getEntityType());
	}

}