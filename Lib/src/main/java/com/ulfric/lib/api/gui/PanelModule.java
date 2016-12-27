package com.ulfric.lib.api.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Milliseconds;

public final class PanelModule extends SimpleModule {

	public PanelModule()
	{
		super("panel", "Panel API module", "Packet", "1.0.0-REL");

		this.addListener(new Listener() {
			@EventHandler
			public void onClose(InventoryCloseEvent event)
			{
				Player player = (Player) event.getPlayer();

				if (event.getInventory().getType() != InventoryType.CHEST) return;

				Panel panel = Metadata.getAndRemove(player, "_ulf_panel");

				if (panel == null) return;

				panel.close(player);
			}

			@EventHandler(ignoreCancelled = true)
			public void onOpen(PanelOpenEvent event)
			{
				Player player = event.getPlayer();

				Object lastObj = Metadata.getValue(player, "_ulf_panel_last");
				long last = 0;

				if (lastObj != null)
				{
					if (lastObj instanceof Long)
					{
						last = (Long) lastObj;
					}
				}

				long current = System.currentTimeMillis();

				if (last > 0 && current - last <= Milliseconds.SECOND)
				{
					event.setCancelled(true);

					return;
				}

				Metadata.apply(player, "_ulf_panel_last", current);

				if (Metadata.getValue(player, "_ulf_panel") == null) return;

				event.setCancelled(true);

				Metadata.remove(player, "_ulf_panel");

				player.kickPlayer(ChatColor.RED + "Something went wrong with" + '\n' + ChatColor.RED + "your inventory. Please relog.");
			}

			@EventHandler(ignoreCancelled = true)
			public void onClick(InventoryClickEvent event)
			{
				if (event.getClickedInventory() == null) return; // Not inside the inventory

				Player player = (Player) event.getWhoClicked();
				InventoryType type = event.getClickedInventory().getType();
				InventoryAction action = event.getAction();

				boolean shiftClick = action == InventoryAction.MOVE_TO_OTHER_INVENTORY || event.getClick() == ClickType.DOUBLE_CLICK;
				if (type != InventoryType.CHEST && !shiftClick) return;

				Panel panel = Metadata.getValue(player, "_ulf_panel");
				if (panel == null) return;

				if (action == InventoryAction.COLLECT_TO_CURSOR) {
					event.setCancelled(true);
					event.setResult(Event.Result.DENY);
					Tasks.runLater(player::updateInventory, 1L);
					return;
				}

				int click = event.getSlot();
				ItemStack item = event.getCurrentItem();
				boolean clickAllowed = panel.isAllowed(click);

				boolean allowed = !shiftClick && clickAllowed;
				if (allowed)
				{
					panel.onSwap(player, event.getCursor(), item, click);
					return;
				}

				if (ItemUtils.isEmpty(item)) return;

				if (clickAllowed)
				{
					panel.onAllowedClick(player, item, click);
					return;
				}
				event.setCancelled(true);
				event.setResult(Event.Result.DENY);

				Tasks.runLater(player::updateInventory, 1L);
				panel.onClick(player, item, click);

			}
		});
	}
}
