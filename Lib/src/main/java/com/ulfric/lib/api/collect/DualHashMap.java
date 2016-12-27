package com.ulfric.lib.api.collect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class DualHashMap<K1, K2, V> {

	private final int expectedSize;
	private Map<K1, Map<K2, V>> map;

	public DualHashMap()
	{
		this(5);
	}

	public DualHashMap(int expectedSize)
	{
		this.expectedSize = expectedSize;
	}

	public void put(K1 k1, K2 k2, V v)
	{
		Map<K2, V> secondary;

		if (this.map != null)
		{
			secondary = this.map.get(k1);

			if (secondary == null)
			{
				secondary = Maps.newHashMapWithExpectedSize(this.expectedSize);

				this.map.put(k1, secondary);
			}

			secondary.put(k2, v);

			return;
		}

		this.map = Maps.newHashMapWithExpectedSize(this.expectedSize);

		secondary = Maps.newHashMapWithExpectedSize(this.expectedSize);

		secondary.put(k2, v);

		this.map.put(k1, secondary);
	}

	public boolean putIfAbsent(K1 k1, K2 k2, V v)
	{
		V current = this.get(k1, k2);
		if (current == null)
		{
			this.put(k1, k2, v);
			return true;
		}

		return false;
	}

	public V get(K1 k1, K2 k2)
	{
		if (CollectUtils.isEmpty(this.map)) return null;

		Map<K2, V> secondary = this.map.get(k1);

		if (CollectUtils.isEmpty(secondary)) return null;

		return secondary.get(k2);
	}

	public boolean clear()
	{
		if (CollectUtils.isEmpty(this.map)) return false;

		this.map.clear();

		return true;
	}

	public Collection<V> values()
	{
		if (CollectUtils.isEmpty(this.map)) return ImmutableList.of();

		Stream<Collection<V>> stream = this.map.values().stream().map(Map::values).filter(CollectUtils::isNotEmpty);

		List<V> values = Lists.newArrayListWithExpectedSize((int) stream.count());

		stream.forEach(values::addAll);

		return values;
	}

}
