package com.ulfric.prison.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.task.Tasks;

public class ListenerInventory implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent event)
	{
		ItemStack cursor = event.getCursor();

		if (ItemUtils.isEmpty(cursor)) return;

		ItemStack current = event.getCurrentItem();

		if (ItemUtils.isEmpty(current)) return;

		if (cursor.getAmount() + current.getAmount() <= 64) return;

		Player player = ((Player) event.getWhoClicked());

		Tasks.run(player::updateInventory);
	}

}