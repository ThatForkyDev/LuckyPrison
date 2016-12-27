package com.ulfric.prison.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.PlayerInventory;

import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleCarrotblock extends SimpleModule {

	public ModuleCarrotblock()
	{
		super("carrotblock", "Disables brewing with carrots", "StaticShadow", "1.0.0-REL");

		this.addListener(new Listener()
		{
			@EventHandler
			public void onInventoryPlace(InventoryMoveItemEvent event)
			{
				if (event.getDestination().getType() != InventoryType.BREWING) return;

				if (!ItemUtils.is(event.getItem(), Material.GOLDEN_CARROT)) return;

				event.setCancelled(true);

				if (event.getSource().getType() != InventoryType.PLAYER) return;

				Player player = (Player)(((PlayerInventory) event.getSource()).getHolder());
				player.closeInventory();
				Locale.sendError(player, "prison.carrot_blocked");
			}
		});
	}

}