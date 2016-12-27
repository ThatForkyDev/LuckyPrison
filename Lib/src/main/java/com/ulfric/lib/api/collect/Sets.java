package com.ulfric.lib.api.collect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Sets {

	static ISets impl = ISets.EMPTY;

	private Sets()
	{
	}

	public static <E> HashSet<E> newHashSet()
	{
		return impl.newHashSet();
	}

	public static <E> HashSet<E> newHashSetWithExpectedSize(int size)
	{
		return impl.newHashSetWithExpectedSize(size);
	}

	public static <E> HashSet<E> newHashSetWithExpectedSize(int size, float loadFactor)
	{
		return impl.newHashSetWithExpectedSize(size, loadFactor);
	}

	@SafeVarargs
	public static <E> HashSet<E> newHashSet(E... elements)
	{
		return impl.newHashSet(elements);
	}

	public static <E> HashSet<E> newHashSet(Collection<? extends E> elements)
	{
		return impl.newHashSet(elements);
	}

	public static <E> Set<E> newWeakSet()
	{
		return impl.newWeakSet();
	}

	public static <E> Set<E> newWeakSetWithExpectedSize(int size)
	{
		return impl.newWeakSetWithExpectedSize(size);
	}

	public static <E> Set<E> newWeakSetWithExpectedSize(int size, float loadFactor)
	{
		return impl.newWeakSetWithExpectedSize(size, loadFactor);
	}

	@SafeVarargs
	public static <E> Set<E> newWeakSet(E... elements)
	{
		return impl.newWeakSet(elements);
	}

	public static <E> Set<E> newWeakSet(Collection<? extends E> elements)
	{
		return impl.newWeakSet(elements);
	}

	protected interface ISets {
		ISets EMPTY = new ISets() {
		};

		default <E> HashSet<E> newHashSet()
		{
			return null;
		}

		default <E> HashSet<E> newHashSetWithExpectedSize(int size)
		{
			return null;
		}

		default <E> HashSet<E> newHashSetWithExpectedSize(int size, float loadFactor)
		{
			return null;
		}

		default <E> HashSet<E> newHashSet(E... elements)
		{
			return null;
		}

		default <E> HashSet<E> newHashSet(Collection<? extends E> elements)
		{
			return null;
		}

		default <E> Set<E> newWeakSet()
		{
			return null;
		}

		default <E> Set<E> newWeakSetWithExpectedSize(int size)
		{
			return null;
		}

		default <E> Set<E> newWeakSetWithExpectedSize(int size, float loadFactor)
		{
			return null;
		}

		default <E> Set<E> newWeakSet(E... elements)
		{
			return null;
		}

		default <E> Set<E> newWeakSet(Collection<? extends E> elements)
		{
			return null;
		}
	}

}
