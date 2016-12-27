package com.ulfric.prison.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.hook.EconHook.Price;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.Enchant;
import com.ulfric.lib.api.inventory.EnchantUtils;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUseSignEvent;
import com.ulfric.lib.api.server.Events;
import com.ulfric.prison.events.PlayerPurchaseEnchantmentEvent;

public class ListenerSign implements Listener {


	@EventHandler(ignoreCancelled = true)
	public void onSignUse(PlayerUseSignEvent event)
	{
		Player player = event.getPlayer();

		ItemStack item = player.getItemInHand();

		String[] lines = event.getSign().getLines();

		switch (ChatColor.stripColor(lines[0]).toLowerCase())
		{
			case "[disposal]":
				player.openInventory(Bukkit.createInventory(player, 36, Locale.getMessage(player, "prison.sign_disposal")));
				break;

			case "[enchant]":
			{
				if (ItemUtils.isEmpty(item)) return;

				int levels = lines[1].startsWith("+") ? Integer.parseInt(lines[1].split("\\+")[1].trim()) : Integer.parseInt(lines[1].trim());

				Enchantment enchant = EnchantUtils.getEnchantByName(lines[2]);

				if (enchant == null) break;

				if (!enchant.canEnchantItem(item))
				{
					Locale.sendError(player, "prison.enchant_incompatible");

					break;
				}

				if (item.getEnchantmentLevel(enchant)+levels > EnchantUtils.getMaxLevel(enchant))
				{
					Locale.sendError(player, "prison.enchant_max", lines[2].toLowerCase());

					break;
				}

				String finalLine = lines[3];
				if (org.apache.commons.lang3.StringUtils.isNotBlank(finalLine))
				{
					UUID uuid = player.getUniqueId();

					Price price = Hooks.ECON.price(finalLine);

					String enchantmentText = Strings.format("+{0} {1}", levels, EnchantUtils.getHiddenName(enchant));

					if (!price.take(uuid, "Purchase of " + enchantmentText))
					{
						Locale.sendError(player, "prison.enchant_cannot_afford", price.toString());

						break;
					}

					Locale.sendSuccess(player, "prison.enchant_success", price.toString(), enchantmentText);
				}

				Enchant finalEnchant = Events.call(new PlayerPurchaseEnchantmentEvent(player, EnchantUtils.ench(enchant, levels))).getEnchant();

				item.setItemMeta(EnchantUtils.addFakeEnchant(item.getItemMeta(), finalEnchant, false));
				break;
			}

			case "[free]":
			{
				ItemPair pair = MaterialUtils.pair(lines[1]);

				Inventory inventory = Bukkit.createInventory(player, 27, Locale.getMessage(player, "prison.free_title"));

				ItemStack pitem = pair.toItem(64);
				ItemStack[] items = new ItemStack[inventory.getSize()];
				for (int x = 0; x < items.length; x++)
				{
					items[x] = pitem;
				}

				inventory.setContents(items);

				player.openInventory(inventory);

				break;
			}

			default:
				break;
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSignPlace(SignChangeEvent event)
	{
		if (event.getPlayer().hasPermission("lib.sign.placement")) return;

		String line = ChatColor.stripColor(event.getLine(0)).toLowerCase();

		switch (line)
		{
			case "[status]":
			case "[enchant]":
			case "[sellall]":
			case "[free]":
			case "[disposal]":
				event.setLine(0, "Mmm that's good!");
				break;

			default:
				break;
		}
	}


}