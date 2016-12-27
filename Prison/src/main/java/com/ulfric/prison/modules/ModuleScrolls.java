package com.ulfric.prison.modules;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.ulfric.lib.api.inventory.Enchant;
import com.ulfric.lib.api.inventory.EnchantUtils;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerDamagePlayerEvent;

public final class ModuleScrolls extends SimpleModule {

	public ModuleScrolls()
	{
		super("scrolls", "Enchantment scrolls module", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		for (Material material : Material.values())
		{
			if (material.getMaxDurability() <= 1) continue;

			if (!EnchantmentTarget.ALL.includes(material)) continue;

			Bukkit.addRecipe(new ShapelessRecipe(new ItemStack(material)).addIngredient(material).addIngredient(Material.PAPER));
		}

		this.addListener(new Listener()
		{
			@EventHandler
			public void onSword(PlayerDamagePlayerEvent event)
			{
				ItemStack item = event.getPlayer().getItemInHand();

				if (!ItemUtils.is(item, Material.DIAMOND_SWORD)) return;

				if (!item.hasItemMeta()) return;

				ItemMeta meta = item.getItemMeta();

				if (!meta.hasEnchants()) return;

				if (meta.getEnchantLevel(Enchantment.DAMAGE_ALL) <= 6) return;

				if (event.getPlayer().hasPermission("sharpness.bypass")) return;

				event.getPlayer().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
			}

			@EventHandler
			public void onCraft(PrepareItemCraftEvent event)
			{
				if (event.isRepair()) return;

				CraftingInventory inventory = event.getInventory();

				ItemStack result = event.getRecipe().getResult();

				if (!EnchantmentTarget.ALL.includes(result)) return;

				ItemStack scroll = null;
				ItemStack item = null;

				for (ItemStack i : inventory.getMatrix())
				{
					if (i == null) continue;

					Material material = i.getType();

					if (material == Material.AIR) continue;

					if (material == Material.PAPER)
					{
						scroll = i;

						if (item != null) break;

						continue;
					}

					item = i;

					if (scroll != null) break;
				}

				if (scroll == null || item == null) return;

				if (result.hasItemMeta()) return;

				ItemMeta meta = item.getItemMeta();

				scroll.getEnchantments().forEach((ench, lvl) ->
				{
					if (!ench.canEnchantItem(result)) return;

					int current = meta.getEnchantLevel(ench);

					int max = EnchantUtils.getMaxLevel(ench);

					int p = lvl + current;

					if (p > max)
					{
						p = Math.min(current, max);
					}

					Enchant.of(ench, p).apply(meta, false);
				});

				result.setItemMeta(meta);

				inventory.setResult(result);
			}
		});
	}

}