package com.ulfric.lib.api.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtils {

	static IInventoryUtils impl = IInventoryUtils.EMPTY;

	private InventoryUtils()
	{
	}

	public static void clear(Inventory inventory)
	{
		impl.clear(inventory);
	}

	public static boolean hasRoomFor(Inventory inventory, ItemStack item, boolean allowOverflow)
	{
		return impl.hasRoomFor(inventory, item, allowOverflow);
	}

	public static boolean isFull(Inventory inventory)
	{
		return impl.isFull(inventory);
	}

	public static boolean isEmpty(Inventory inventory)
	{
		return impl.isEmpty(inventory);
	}

	public static boolean giveOrDrop(Player player, ItemStack item)
	{
		return impl.giveOrDrop(player, item);
	}

	public static boolean giveOrDrop(Player player, ItemStack... items)
	{
		return impl.giveOrDrop(player, items);
	}

	public static int count(Inventory inventory, ItemStack item)
	{
		return impl.count(inventory, item);
	}

	protected interface IInventoryUtils {
		IInventoryUtils EMPTY = new IInventoryUtils() {
		};

		default void clear(Inventory inventory)
		{
		}

		default int count(Inventory inventory, ItemStack item)
		{
			return 0;
		}

		default boolean hasRoomFor(Inventory inventory, ItemStack item, boolean allowOverflow)
		{
			return false;
		}

		default boolean isFull(Inventory inventory)
		{
			return false;
		}

		default boolean isEmpty(Inventory inventory)
		{
			return false;
		}

		default boolean giveOrDrop(Player player, ItemStack... items)
		{
			return false;
		}
	}

}
