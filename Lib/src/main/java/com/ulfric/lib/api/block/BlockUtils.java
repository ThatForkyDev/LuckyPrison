package com.ulfric.lib.api.block;

import com.ulfric.lib.api.inventory.ItemPair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

public final class BlockUtils {

	static IBlockUtils impl = IBlockUtils.EMPTY;

	private BlockUtils()
	{
	}

	public static MultiBlockChange newMultiChange()
	{
		return impl.newMultiChange();
	}

	public static MultiBlockChange newMultiChange(int capacity)
	{
		return impl.newMultiChange(capacity);
	}

	public static boolean isEmpty(Block block)
	{
		return impl.isEmpty(block);
	}

	public static boolean isSmashable(Block block)
	{
		return impl.isSmashable(block);
	}

	public static Set<Material> getTransparent()
	{
		return impl.getTransparent();
	}

	public static boolean isTransparent(Material material)
	{
		return impl.isTransparent(material);
	}

	public static void playTemporaryBlock(Player player, Location location, ItemPair pair, long delay)
	{
		impl.playTemporaryBlock(player, location, pair, delay);
	}

	public static void playTemporaryBlocks(Player player, MultiBlockChange change, long delay)
	{
		impl.playTemporaryBlocks(player, change, delay);
	}

	public static void playBlock(Player player, Location location, ItemPair pair)
	{
		impl.playBlock(player, location, pair);
	}

	public static void playBlocks(Player player, MultiBlockChange change)
	{
		impl.playBlocks(player, change);
	}

	public static void playBlockEffect(Player player, Location location, Material type)
	{
		impl.playBlockEffect(player, location, type);
	}

	protected interface IBlockUtils {
		IBlockUtils EMPTY = new IBlockUtils() {
		};

		default MultiBlockChange newMultiChange()
		{
			return null;
		}

		default MultiBlockChange newMultiChange(int capacity)
		{
			return null;
		}

		default boolean isEmpty(Block block)
		{
			return false;
		}

		default boolean isSmashable(Block block)
		{
			return false;
		}

		default boolean isTransparent(Material material)
		{
			return false;
		}

		default Set<Material> getTransparent()
		{
			return null;
		}

		default void playTemporaryBlock(Player player, Location location, ItemPair pair, long delay)
		{
		}

		default void playTemporaryBlocks(Player player, MultiBlockChange change, long delay)
		{
		}

		default void playBlock(Player player, Location location, ItemPair pair)
		{
		}

		default void playBlocks(Player player, MultiBlockChange change)
		{
		}

		default void playBlockEffect(Player player, Location location, Material type)
		{
		}
	}

}
