package com.ulfric.lib.api.inventory;

import com.ulfric.lib.api.module.Module;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Set;

public final class EnchantUtils {

	static IEnchantUtils impl = IEnchantUtils.EMPTY;

	private EnchantUtils()
	{
	}

	public static Enchant ench(Enchantment enchant, Integer level)
	{
		return impl.ench(enchant, level);
	}

	public static void setAcceptingNewEnchantsMutable(boolean lock)
	{
		impl.setAcceptingNewEnchantsMutable(lock);
	}

	public static void registerCustomEnchantments(Enchantment... enchants)
	{
		impl.registerCustomEnchantments(enchants);
	}

	public static String getHiddenName(Enchantment enchant)
	{
		return impl.getHiddenName(enchant);
	}

	public static Enchantment getEnchantById(int id)
	{
		return impl.getEnchantById(id);
	}

	public static Enchantment getEnchantByName(String string)
	{
		return impl.getEnchantByName(string);
	}

	public static Enchant parse(String string)
	{
		return impl.parse(string);
	}

	public static int getMaxLevel(Enchantment enchant)
	{
		return impl.getMaxLevel(enchant);
	}

	public static int getLevel(ItemStack item, Enchantment enchant)
	{
		return impl.getLevel(item, enchant);
	}

	public static ItemMeta addFakeEnchant(ItemMeta meta, Enchant enchant, boolean romanNumeral)
	{
		return impl.addFakeEnchant(meta, enchant, romanNumeral);
	}

	public static ItemMeta addFakeEnchantMax(ItemMeta meta, Enchant enchant, boolean romanNumeral, int max)
	{
		return impl.addFakeEnchantMax(meta, enchant, romanNumeral, max);
	}

	public static Set<String> getFakeEnchantsAsLore(ItemStack item)
	{
		return impl.getFakeEnchantsAsLore(item);
	}

	public static Map<Enchantment, Integer> getFakeEnchants(ItemStack item)
	{
		return impl.getFakeEnchants(item);
	}

	public static int getWeight(ItemStack item)
	{
		return impl.getWeight(item);
	}

	protected interface IEnchantUtils {
		IEnchantUtils EMPTY = new IEnchantUtils() {
		};

		default Module getModule()
		{
			return null;
		}

		default ItemMeta addFakeEnchantMax(ItemMeta meta, Enchant enchant, boolean romanNumeral, int max)
		{
			return meta;
		}

		default Enchant ench(Enchantment enchant, Integer level)
		{
			return null;
		}

		default void setAcceptingNewEnchantsMutable(boolean lock)
		{
		}

		default void registerCustomEnchantments(Enchantment... enchants)
		{
		}

		default String getHiddenName(Enchantment enchant)
		{
			return null;
		}

		default Enchantment getEnchantById(int id)
		{
			return null;
		}

		default Enchantment getEnchantByName(String string)
		{
			return null;
		}

		default Enchant parse(String string)
		{
			return null;
		}

		default int getMaxLevel(Enchantment enchant)
		{
			return 0;
		}

		default int getLevel(ItemStack item, Enchantment enchant)
		{
			return 0;
		}

		default ItemMeta addFakeEnchant(ItemMeta meta, Enchant enchant, boolean romanNumeral)
		{
			return meta;
		}

		default Set<String> getFakeEnchantsAsLore(ItemStack item)
		{
			return null;
		}

		default Map<Enchantment, Integer> getFakeEnchants(ItemStack item)
		{
			return null;
		}

		default int getWeight(ItemStack item)
		{
			return 0;
		}
	}

}
