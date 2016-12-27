package com.ulfric.lib.api.collect;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Weighted;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class CollectUtils {

	static ICollectUtils impl = ICollectUtils.EMPTY;

	private CollectUtils()
	{
	}

	public static List<String> toStringCollect(Collection<? extends Named> collection)
	{
		return impl.toStringCollect(collection);
	}

	public static boolean containsIgnoreCase(Collection<String> collection, String string)
	{
		return impl.containsIgnoreCase(collection, string);
	}

	public static void trimToSize(Collection<?> collection, int size)
	{
		impl.trimToSize(collection, size);
	}

	public static boolean isEmpty(Collection<?> collection)
	{
		return impl.isEmpty(collection);
	}

	public static boolean isEmpty(Map<?, ?> map)
	{
		return impl.isEmpty(map);
	}

	public static boolean isNotEmpty(Collection<?> collection)
	{
		return impl.isNotEmpty(collection);
	}

	public static boolean isNotEmpty(Map<?, ?> map)
	{
		return impl.isNotEmpty(map);
	}

	public static boolean anyContains(Iterable<String> iter, String string)
	{
		return impl.anyContains(iter, string);
	}

	public static <T> void addAll(Collection<T> collection, T... objects)
	{
		impl.addAll(collection, objects);
	}

	public static int getTotalWeight(Collection<? extends Weighted> collection)
	{
		return impl.getTotalWeight(collection);
	}

	public static Weighted getHeaviest(Collection<? extends Weighted> collection)
	{
		return impl.getHeaviest(collection);
	}

	protected interface ICollectUtils {
		ICollectUtils EMPTY = new ICollectUtils() {
		};

		default List<String> toStringCollect(Collection<? extends Named> collection)
		{
			return ImmutableList.of();
		}

		default void trimToSize(Collection<?> collection, int size)
		{
		}

		default boolean anyContains(Iterable<String> iter, String string)
		{
			return false;
		}

		default boolean containsIgnoreCase(Collection<String> collection, String string)
		{
			return false;
		}

		default boolean isEmpty(Collection<?> collection)
		{
			return true;
		}

		default boolean isEmpty(Map<?, ?> map)
		{
			return true;
		}

		default boolean isNotEmpty(Collection<?> collection)
		{
			return true;
		}

		default boolean isNotEmpty(Map<?, ?> map)
		{
			return true;
		}

		default <T> void addAll(Collection<? super T> collection, T... objects)
		{
		}

		default int getTotalWeight(Collection<? extends Weighted> collection)
		{
			return 0;
		}

		default Weighted getHeaviest(Collection<? extends Weighted> collection)
		{
			return null;
		}
	}

}
