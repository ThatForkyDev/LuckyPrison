package com.ulfric.lib.api.inventory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class ItemUtils {

	static IItemUtils impl = IItemUtils.EMPTY;

	private ItemUtils()
	{
	}

	public static boolean isPrettyMuchTheSame(ItemStack item1, ItemStack item2)
	{
		if (item1 == item2) return true;

		if (item1 == null || item2 == null) return false;

		if (item1.getType() != item2.getType()) return false;

		if (item1.getDurability() != item2.getDurability()) return false;

		String item1Name = "";
		List<String> item1Lore = ImmutableList.of();
		Set<ItemFlag> item1Flags = ImmutableSet.of();
		Map<Enchantment, Integer> item1Enchants = ImmutableMap.of();

		String item2Name = "";
		List<String> item2Lore = ImmutableList.of();
		Set<ItemFlag> item2Flags = ImmutableSet.of();
		Map<Enchantment, Integer> item2Enchants = ImmutableMap.of();

		if (item1.hasItemMeta())
		{
			ItemMeta meta = item1.getItemMeta();

			if (meta.hasDisplayName())
			{
				item1Name = meta.getDisplayName();
			}

			if (meta.hasLore())
			{
				item1Lore = meta.getLore();
			}

			if (meta.hasEnchants())
			{
				item1Enchants = meta.getEnchants();
			}

			item1Flags = meta.getItemFlags();
		}

		if (item2.hasItemMeta())
		{
			ItemMeta meta = item2.getItemMeta();

			if (meta.hasDisplayName())
			{
				item2Name = meta.getDisplayName();
			}

			if (meta.hasLore())
			{
				item2Lore = meta.getLore();
			}

			if (meta.hasEnchants())
			{
				item2Enchants = meta.getEnchants();
			}

			item2Flags = meta.getItemFlags();
		}

		return item1Name.equals(item2Name) && item1Lore.equals(item2Lore) && item1Flags.equals(item2Flags) && item1Enchants.equals(item2Enchants);
	}

	public static boolean is(ItemStack item, Material material)
	{
		return impl.is(item, material);
	}

	public static String toString(ItemStack item)
	{
		return impl.toString(item);
	}

	public static ItemStack fromString(String item)
	{
		return impl.fromString(item);
	}

	public static String colorsToString(List<Color> colors)
	{
		return impl.colorsToString(colors);
	}

	public static boolean isEmpty(ItemStack item)
	{
		return impl.isEmpty(item);
	}

	public static boolean hasNameAndLore(ItemStack item)
	{
		return impl.hasNameAndLore(item);
	}

	public static void decrementHand(Player player)
	{
		impl.decrementHand(player);
	}

	public static ItemStack blank()
	{
		return impl.blank();
	}

	public static ItemStack worthless()
	{
		return impl.worthless();
	}

	public static void replacePagePlaceholders(ItemStack item, Player player)
	{
		impl.replacePagePlaceholders(item, player);
	}

	public static DyeColor dyeFromChat(ChatColor color)
	{
		return impl.dyeFromChat(color);
	}

	protected interface IItemUtils {
		IItemUtils EMPTY = new IItemUtils() {
		};

		default boolean is(ItemStack item, Material material)
		{
			return false;
		}

		default DyeColor dyeFromChat(ChatColor color)
		{
			return DyeColor.WHITE;
		}

		default void replacePagePlaceholders(ItemStack item, Player player)
		{
		}

		default boolean isEmpty(ItemStack item)
		{
			return false;
		}

		default boolean hasNameAndLore(ItemStack item)
		{
			return false;
		}

		default String toString(ItemStack item)
		{
			return null;
		}

		default ItemStack fromString(String item)
		{
			return null;
		}

		default String colorsToString(List<Color> colors)
		{
			return null;
		}

		default void decrementHand(Player player)
		{
		}

		default ItemStack blank()
		{
			return null;
		}

		default ItemStack worthless()
		{
			return null;
		}
	}

}
