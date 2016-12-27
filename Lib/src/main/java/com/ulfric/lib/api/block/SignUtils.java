package com.ulfric.lib.api.block;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.List;

public final class SignUtils {

	static ISignUtils impl = ISignUtils.EMPTY;

	private SignUtils()
	{
	}

	public static boolean isSign(Block block)
	{
		return impl.isSign(block);
	}

	public static Sign asSign(Block block)
	{
		return impl.asSign(block);
	}

	public static void setLines(Sign sign, List<String> lines)
	{
		impl.setLines(sign, lines);
	}

	public static void setLines(Sign sign, String... lines)
	{
		impl.setLines(sign, lines);
	}

	protected interface ISignUtils {
		ISignUtils EMPTY = new ISignUtils() {
		};

		default boolean isSign(Block block)
		{
			return false;
		}

		default Sign asSign(Block block)
		{
			return null;
		}

		default void setLines(Sign sign, List<String> lines)
		{
		}

		default void setLines(Sign sign, String... lines)
		{
		}
	}

}
