package com.ulfric.lib.api.math;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Color;

import com.ulfric.lib.api.java.Weighted;

public final class RandomUtils {

	static IRandomUtils impl = IRandomUtils.EMPTY;

	private RandomUtils()
	{
	}

	public static Random getRandom()
	{
		return impl.getRandom();
	}

	public static Random reseedRandom()
	{
		return impl.reseedRandom();
	}

	public static boolean nextBoolean()
	{
		return impl.nextBoolean();
	}

	public static double nextDouble()
	{
		return impl.nextDouble();
	}

	public static int nextInt(int control)
	{
		return impl.nextInt(control);
	}

	public static int nextIntP1(int control)
	{
		return impl.nextIntP1(control);
	}

	public static double nextDouble(double control)
	{
		return impl.nextDouble(control);
	}

	public static int randomRange(int min, int max)
	{
		return impl.randomRange(min, max);
	}

	public static double randomRange(double min, double max)
	{
		return impl.randomRange(min, max);
	}

	public static boolean randomPercentage(double chance)
	{
		return impl.randomPercentage(chance);
	}

	public static <T> T randomValueFromList(List<T> list)
	{
		return impl.randomValueFromList(list);
	}

	public static <K, V> Map.Entry<K, V> randomEntryFromMap(Map<K, V> map)
	{
		return impl.randomEntryFromMap(map);
	}

	public static <T> T randomValueFromCollection(Collection<T> collection)
	{
		return impl.randomValueFromCollection(collection);
	}

	public static Color randomColor()
	{
		return impl.randomColor();
	}

	public static <E extends Weighted> E getWeighted(Collection<E> collection)
	{
		return impl.getWeighted(collection);
	}

	public static <E extends Weighted> E getWeighted(Collection<E> collection, int weight)
	{
		return impl.getWeighted(collection, weight);
	}

	public static <E extends Weighted> E getWeightedFromWeight(Collection<E> collection, int weight, boolean shuffle)
	{
		return impl.getWeightedFromWeight(collection, weight, shuffle);
	}

	public static <E extends Weighted> E getWeightedFromWeight(Collection<E> collection, int weight)
	{
		return impl.getWeightedFromWeight(collection, weight);
	}

	protected interface IRandomUtils {
		IRandomUtils EMPTY = new IRandomUtils() {
		};

		default Random getRandom()
		{
			return null;
		}

		default Random reseedRandom()
		{
			return null;
		}

		default boolean nextBoolean()
		{
			return false;
		}

		default double nextDouble()
		{
			return 0.0D;
		}

		default int nextInt(int control)
		{
			return 0;
		}

		default int nextIntP1(int control)
		{
			return 1;
		}

		default double nextDouble(double control)
		{
			return 0.0D;
		}

		default int randomRange(int min, int max)
		{
			return min;
		}

		default double randomRange(double min, double max)
		{
			return min;
		}

		default boolean randomPercentage(double chance)
		{
			return false;
		}

		default <T> T randomValueFromList(List<T> list)
		{
			return list.get(0);
		}

		default <K, V> Map.Entry<K, V> randomEntryFromMap(Map<K, V> map)
		{
			return map.entrySet().iterator().next();
		}

		default <T> T randomValueFromCollection(Collection<T> collection)
		{
			return collection.iterator().next();
		}

		default Color randomColor()
		{
			return null;
		}

		default <E extends Weighted> E getWeighted(Collection<E> collection)
		{
			return collection.iterator().next();
		}

		default <E extends Weighted> E getWeighted(Collection<E> collection, int weight)
		{
			return collection.iterator().next();
		}

		default <E extends Weighted> E getWeightedFromWeight(Collection<E> collection, int weight, boolean shuffle)
		{
			return collection.iterator().next();
		}

		default <E extends Weighted> E getWeightedFromWeight(Collection<E> collection, int weight)
		{
			return collection.iterator().next();
		}
	}

}
