package com.ulfric.lib.api.location;

import org.bukkit.Chunk;

public final class ChunkUtils {

	static IChunkUtils impl = IChunkUtils.EMPTY;

	private ChunkUtils()
	{
	}

	public static String toString(Chunk chunk)
	{
		return impl.toString(chunk);
	}

	public static Chunk fromString(String string)
	{
		return impl.fromString(string);
	}

	protected interface IChunkUtils {
		IChunkUtils EMPTY = null;

		default String toString(Chunk chunk)
		{
			return null;
		}

		default Chunk fromString(String string)
		{
			return null;
		}
	}

}
