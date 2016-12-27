package com.ulfric.lib.api.inventory;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public final class MaterialUtils {

	static IMaterialUtils impl = IMaterialUtils.EMPTY;

	private MaterialUtils()
	{
	}

	public static String getName(Material material)
	{
		return impl.getName(material);
	}

	public static String getName(ItemPair pair)
	{
		return impl.getName(pair);
	}

	public static Material smelt(Material material)
	{
		return impl.smelt(material);
	}

	public static Material compress(ItemStack item)
	{
		return impl.compress(item);
	}

	public static void setMaxStackSizeMutable(boolean lock)
	{
		impl.setMaxStackSizeMutable(lock);
	}

	public static void setMaxStackSize(Material material, int size)
	{
		impl.setMaxStackSize(material, size);
	}

	public static boolean isBlock(Material material)
	{
		return impl.isBlock(material);
	}

	public static ItemPair pair(Block block)
	{
		return impl.pair(block);
	}

	public static ItemPair pair(ItemStack item)
	{
		return impl.pair(item);
	}

	public static ItemPair pair(Material type)
	{
		return impl.pair(type);
	}

	public static ItemPair pair(Material type, int data)
	{
		return impl.pair(type, data);
	}

	public static ItemPair pair(String name)
	{
		return impl.pair(name);
	}

	public static WeightedItemPair weight(int weight, ItemPair pair)
	{
		return impl.weight(weight, pair);
	}

	public static WeaponType getWeaponType(Material material)
	{
		return impl.getWeaponType(material);
	}

	protected interface IMaterialUtils {
		IMaterialUtils EMPTY = new IMaterialUtils() {
		};

		default String getName(Material material)
		{
			return null;
		}

		default WeaponType getWeaponType(Material material)
		{
			return WeaponType.UNKNOWN;
		}

		default Material compress(ItemStack item)
		{
			return null;
		}

		default Material smelt(Material material)
		{
			return material;
		}

		default String getName(ItemPair pair)
		{
			return null;
		}

		default void setMaxStackSizeMutable(boolean lock)
		{
		}

		default void setMaxStackSize(Material material, int size)
		{
		}

		default boolean isBlock(Material material)
		{
			return false;
		}

		default ItemPair pair(Block block)
		{
			return null;
		}

		default ItemPair pair(ItemStack item)
		{
			return null;
		}

		default ItemPair pair(Material type)
		{
			return null;
		}

		default ItemPair pair(Material type, int data)
		{
			return null;
		}

		default ItemPair pair(String name)
		{
			return null;
		}

		default WeightedItemPair weight(int weight, ItemPair pair)
		{
			return null;
		}
	}

}
