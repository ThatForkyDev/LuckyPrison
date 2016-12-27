package com.intellectualcrafters.plot.flag;

import java.util.Collection;

import com.intellectualcrafters.plot.object.Plot;

public abstract class ListFlag<V extends Collection<?>> extends Flag<V> {

	protected ListFlag(String name)
	{
		super(name);
	}

	public boolean contains(Plot plot, Object value)
	{
		V existing = plot.getFlag(this, null);
		return existing != null && existing.contains(value);
	}
}
