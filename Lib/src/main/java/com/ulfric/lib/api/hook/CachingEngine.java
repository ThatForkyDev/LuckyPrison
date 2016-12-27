package com.ulfric.lib.api.hook;

import com.google.common.collect.Maps;

import java.util.Map;

public class CachingEngine<K, V> {

	private final boolean caching;
	private Map<K, V> cache;

	public CachingEngine(boolean caching)
	{
		this.caching = caching;

		if (!caching) return;

		this.cache = Maps.newHashMap();
	}

	public final boolean isCaching()
	{
		return this.caching;
	}

	public V getCached(K path)
	{
		return this.cache.get(path);
	}

	public void cache(K path, V value)
	{
		this.cache.put(path, value);
	}

	public void clear()
	{
		this.cache.clear();
	}

}
