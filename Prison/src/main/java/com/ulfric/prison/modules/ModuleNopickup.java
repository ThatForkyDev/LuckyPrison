package com.ulfric.prison.modules;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.inventory.EnchantUtils;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.prison.lang.Meta;

public class ModuleNopickup extends SimpleModule {

	public ModuleNopickup()
	{
		super("nopickup", "Allows players to disable picking up items", "Packet", "1.0.0-REL");

		this.addCommand("pickup", new SimpleCommand()
		{
			{
				this.withEnforcePlayer();
			}

			@Override
			public void run()
			{
				Player player = this.getPlayer();

				boolean enable = player.getCanPickupItems();

				player.setCanPickupItems(enable);

				if (enable)
				{
					Locale.sendSuccess(player, "prison.nopickup_on");

					return;
				}

				Locale.sendSuccess(player, "prison.nopickup_off");
			}
		});

		this.addListener(new Listener()
		{
			private final String defaultPick = Chat.color("&5&lStarter Pickaxe");
			@EventHandler(ignoreCancelled = true)
			public void onDrop(PlayerDropItemEvent event)
			{
				ItemStack item = event.getItemDrop().getItemStack();

				if (!item.hasItemMeta()) return;

				ItemMeta meta = item.getItemMeta();

				if (!meta.hasEnchants()) return;

				Player player = event.getPlayer();

				if (EnchantUtils.getWeight(item) >= 5)
				{
					if (!player.getOpenInventory().getType().equals(InventoryType.PLAYER)) return;

					if (!player.hasMetadata("_ulf_dropconfirm") ||
						!((ItemStack) Metadata.getAndRemove(player, "_ulf_dropconfirm")).equals(item))
					{
						event.setCancelled(true);

						Locale.sendError(player, "prison.drop_confirm");

						Metadata.apply(player, "_ulf_dropconfirm", item);

						return;
					}
				}

				if (!ItemUtils.is(item, Material.DIAMOND_PICKAXE)) return;

				if (!meta.hasDisplayName()) return;

				if (!meta.getDisplayName().equals(this.defaultPick)) return;

				Metadata.tieToPlayer(event.getItemDrop(), player);
			}

			@EventHandler(ignoreCancelled = true)
			public void onItemPickup(PlayerPickupItemEvent event)
			{
				Player player = event.getPlayer();

				Item item = event.getItem();

				Player tied = Metadata.getTied(item);
				if (tied != null)
				{
					if (tied.equals(player)) return;

					event.setCancelled(true);

					return;
				}

				if (!item.hasMetadata(Meta.LUCKYBLOCK_ITEM)) return;

				tied = Metadata.getValue(item, Meta.LUCKYBLOCK_ITEM);

				if (player.equals(tied)) return;

				event.setCancelled(true);
			}
		});
	}

}