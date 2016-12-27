package com.ulfric.lib.api.entity;

import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.time.Timestamp;
import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;

import java.util.List;
import java.util.Set;

public final class Metadata {

	static IMetadata impl = IMetadata.EMPTY;

	private Metadata()
	{
	}

	public static <T> T getValue(Metadatable metadatable, String path)
	{
		return impl.getValue(metadatable, path);
	}

	public static boolean getValueAsBoolean(Metadatable metadatable, String path)
	{
		return impl.getValueAsBoolean(metadatable, path);
	}

	public static byte getValueAsByte(Metadatable metadatable, String path)
	{
		return impl.getValueAsByte(metadatable, path);
	}

	public static short getValueAsShort(Metadatable metadatable, String path)
	{
		return impl.getValueAsShort(metadatable, path);
	}

	public static int getValueAsInt(Metadatable metadatable, String path)
	{
		return impl.getValueAsInt(metadatable, path);
	}

	public static long getValueAsLong(Metadatable metadatable, String path)
	{
		return impl.getValueAsLong(metadatable, path);
	}

	public static double getValueAsDouble(Metadatable metadatable, String path)
	{
		return impl.getValueAsDouble(metadatable, path);
	}

	public static float getValueAsFloat(Metadatable metadatable, String path)
	{
		return impl.getValueAsFloat(metadatable, path);
	}

	public static String getValueAsString(Metadatable metadatable, String path)
	{
		return impl.getValueAsString(metadatable, path);
	}

	public static <T> List<T> getValueAsList(Metadatable metadatable, String path)
	{
		return impl.getValueAsList(metadatable, path);
	}

	public static List<String> getValueAsStringList(Metadatable metadatable, String path)
	{
		return impl.getValueAsStringList(metadatable, path);
	}

	public static <T> Set<T> getValueAsSet(Metadatable metadatable, String path)
	{
		return impl.getValueAsSet(metadatable, path);
	}

	public static Timestamp getValueAsTimestamp(Metadatable metadatable, String path)
	{
		return impl.getValueAsTimestamp(metadatable, path);
	}

	public static ATask getValueAsTask(Metadatable metadatable, String path)
	{
		return impl.getValueAsTask(metadatable, path);
	}

	public static void applyNull(Metadatable metadatable, String path)
	{
		impl.applyNull(metadatable, path);
	}

	public static void applyTrue(Metadatable metadatable, String path)
	{
		impl.applyTrue(metadatable, path);
	}

	public static void applyFalse(Metadatable metadatable, String path)
	{
		impl.applyFalse(metadatable, path);
	}

	public static boolean applyTemp(Metadatable metadatable, String path, Object object, long ticks)
	{
		return impl.applyTemp(metadatable, path, object, ticks);
	}

	public static void apply(Metadatable metadatable, String path, Object object)
	{
		impl.apply(metadatable, path, object);
	}

	public static <T> T getAndRemove(Metadatable metadatable, String path)
	{
		return impl.getAndRemove(metadatable, path);
	}

	public static void remove(Metadatable metadatable, String path)
	{
		impl.remove(metadatable, path);
	}

	public static boolean removeIfPresent(Metadatable metadatable, String path)
	{
		return impl.removeIfPresent(metadatable, path);
	}

	public static void tieToPlayer(Metadatable metadatable, Player player)
	{
		impl.tieToPlayer(metadatable, player);
	}

	public static Player getTied(Metadatable metadatable)
	{
		return impl.getTied(metadatable);
	}

	protected interface IMetadata {
		IMetadata EMPTY = new IMetadata() {
		};

		default <T> T getValue(Metadatable metadatable, String path)
		{
			return null;
		}

		default boolean getValueAsBoolean(Metadatable metadatable, String path)
		{
			return false;
		}

		default byte getValueAsByte(Metadatable metadatable, String path)
		{
			return 0;
		}

		default short getValueAsShort(Metadatable metadatable, String path)
		{
			return 0;
		}

		default int getValueAsInt(Metadatable metadatable, String path)
		{
			return 0;
		}

		default long getValueAsLong(Metadatable metadatable, String path)
		{
			return 0L;
		}

		default double getValueAsDouble(Metadatable metadatable, String path)
		{
			return 0.0D;
		}

		default float getValueAsFloat(Metadatable metadatable, String path)
		{
			return 0.0F;
		}

		default String getValueAsString(Metadatable metadatable, String path)
		{
			return null;
		}

		default <T> List<T> getValueAsList(Metadatable metadatable, String path)
		{
			return null;
		}

		default List<String> getValueAsStringList(Metadatable metadatable, String path)
		{
			return null;
		}

		default <T> Set<T> getValueAsSet(Metadatable metadatable, String path)
		{
			return null;
		}

		default Timestamp getValueAsTimestamp(Metadatable metadatable, String path)
		{
			return null;
		}

		default ATask getValueAsTask(Metadatable metadatable, String path)
		{
			return null;
		}

		default void applyNull(Metadatable metadatable, String path)
		{
		}

		default void applyTrue(Metadatable metadatable, String path)
		{
		}

		default void applyFalse(Metadatable metadatable, String path)
		{
		}

		default boolean applyTemp(Metadatable metadatable, String path, Object object, long ticks)
		{
			return false;
		}

		default void apply(Metadatable metadatable, String path, Object object)
		{
		}

		default void remove(Metadatable metadatable, String path)
		{
		}

		default <T> T getAndRemove(Metadatable metadatable, String path)
		{
			return null;
		}

		default boolean removeIfPresent(Metadatable metadatable, String path)
		{
			return false;
		}

		default void tieToPlayer(Metadatable metadatable, Player player)
		{
		}

		default Player getTied(Metadatable metadatable)
		{
			return null;
		}
	}

}
