package com.ulfric.lib.api.collect;

import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Weighted;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.tuple.TupleModule;

import java.util.*;
import java.util.stream.Collectors;

public final class CollectModule extends SimpleModule {

	public CollectModule()
	{
		super("collect", "Collection utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new TupleModule());
		this.withSubModule(new SetsModule());
		this.withSubModule(new ArraysModule());
	}

	@Override
	public void postEnable()
	{
		CollectUtils.impl = new CollectUtils.ICollectUtils() {
			@Override
			public List<String> toStringCollect(Collection<? extends Named> collection)
			{
				return collection.stream().map(Named::getName).collect(Collectors.toList());
			}

			@Override
			public void trimToSize(Collection<?> collection, int size)
			{
				Iterator<?> iter = collection.iterator();

				while (collection.size() > size)
				{
					iter.next();
					iter.remove();
				}
			}

			@Override
			public boolean anyContains(Iterable<String> iter, String string)
			{
				for (String entry : iter)
				{
					if (!entry.contains(string)) continue;

					return true;
				}

				return false;
			}

			@Override
			public boolean containsIgnoreCase(Collection<String> collection, String string)
			{
				string = string.toLowerCase();

				for (String entry : collection)
				{
					if (!string.equals(entry.toLowerCase())) continue;

					return true;
				}

				return false;
			}

			@Override
			public boolean isEmpty(Collection<?> collection)
			{
				return collection == null || collection.isEmpty();
			}

			@Override
			public boolean isEmpty(Map<?, ?> map)
			{
				return map == null || map.isEmpty();
			}

			@Override
			public boolean isNotEmpty(Collection<?> collection)
			{
				return collection != null && !collection.isEmpty();
			}

			@Override
			public boolean isNotEmpty(Map<?, ?> map)
			{
				return map != null && !map.isEmpty();
			}

			@SafeVarargs
			@Override
			public final <T> void addAll(Collection<? super T> collection, T... objects)
			{
				Collections.addAll(collection, objects);
			}

			@Override
			public int getTotalWeight(Collection<? extends Weighted> collection)
			{
				if (this.isEmpty(collection)) return 0;

				int weight = 0;

				for (Weighted weighted : collection)
				{
					weight += weighted.getWeight();
				}

				return weight;
			}

			@Override
			public Weighted getHeaviest(Collection<? extends Weighted> collection)
			{
				if (this.isEmpty(collection)) return null;

				Weighted heaviest = null;

				for (Weighted weighted : collection)
				{
					if (heaviest == null)
					{
						heaviest = weighted;

						continue;
					}

					if (weighted.getWeight() <= heaviest.getWeight()) continue;

					heaviest = weighted;
				}

				return heaviest;
			}
		};
	}

	private static final class SetsModule extends SimpleModule {
		SetsModule()
		{
			super("sets", "Set creation module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Sets.impl = new Sets.ISets() {
				@Override
				public <E> HashSet<E> newHashSet()
				{
					return new HashSet<>();
				}

				@Override
				public <E> HashSet<E> newHashSetWithExpectedSize(int size)
				{
					return new HashSet<>(size);
				}

				@Override
				public <E> HashSet<E> newHashSetWithExpectedSize(int size, float loadFactor)
				{
					return new HashSet<>(size, loadFactor);
				}

				@SafeVarargs
				@Override
				public final <E> HashSet<E> newHashSet(E... elements)
				{
					HashSet<E> set = this.newHashSetWithExpectedSize(elements.length + 3);

					Collections.addAll(set, elements);

					return set;
				}

				@Override
				public <E> HashSet<E> newHashSet(Collection<? extends E> elements)
				{
					HashSet<E> set = this.newHashSetWithExpectedSize(elements.size() + 3);

					for (E element : elements)
					{
						set.add(element);
					}

					return set;
				}

				@Override
				public <E> Set<E> newWeakSet()
				{
					return Collections.newSetFromMap(new WeakHashMap<>());
				}

				@Override
				public <E> Set<E> newWeakSetWithExpectedSize(int size)
				{
					return Collections.newSetFromMap(new WeakHashMap<>(size));
				}

				@Override
				public <E> Set<E> newWeakSetWithExpectedSize(int size, float loadFactor)
				{
					return Collections.newSetFromMap(new WeakHashMap<>(size, loadFactor));
				}

				@SafeVarargs
				@Override
				public final <E> Set<E> newWeakSet(E... elements)
				{
					Set<E> set = this.newWeakSetWithExpectedSize(elements.length + 3);

					Collections.addAll(set, elements);

					return set;
				}

				@Override
				public <E> Set<E> newWeakSet(Collection<? extends E> elements)
				{
					Set<E> set = this.newWeakSetWithExpectedSize(elements.size() + 3);

					for (E element : elements)
					{
						set.add(element);
					}

					return set;
				}
			};
		}

		@Override
		public void postDisable()
		{
			Sets.impl = Sets.ISets.EMPTY;
		}
	}

	private static final class ArraysModule extends SimpleModule {
		ArraysModule()
		{
			super("arrays", "Array utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			ArrayUtils.impl = new ArrayUtils.IArrayUtils() {
				@Override
				public String mergeToString(String[] array, int start)
				{
					StringBuilder builder = new StringBuilder();

					for (int x = start; x < array.length; x++)
					{
						builder.append(array[x]);

						builder.append(' ');
					}

					String value = builder.toString();

					return (value.substring(0, value.length() - 1));
				}
			};
		}

		@Override
		public void postDisable()
		{
			ArrayUtils.impl = ArrayUtils.IArrayUtils.EMPTY;
		}
	}

	@Override
	public void postDisable()
	{
		CollectUtils.impl = CollectUtils.ICollectUtils.EMPTY;
	}


}
