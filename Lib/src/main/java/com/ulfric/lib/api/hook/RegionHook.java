package com.ulfric.lib.api.hook;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.api.hook.RegionHook.IRegionHook;
import com.ulfric.lib.api.java.Container;
import com.ulfric.lib.api.java.Named;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public final class RegionHook extends Hook<IRegionHook> {

	RegionHook()
	{
		super(IRegionHook.EMPTY, "Guard", "Guard hook module", "Packet", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		this.impl.buildEngine(true);
	}

	@Override
	public void postDisable()
	{
		this.impl.clearEngine();
	}

	public Region region(String name)
	{
		return this.impl.region(name);
	}

	public List<Region> at(Location location)
	{
		return this.impl.at(location);
	}

	public interface IRegionHook extends HookEngineImpl {
		IRegionHook EMPTY = new IRegionHook() {
		};

		default Region region(String name)
		{
			return null;
		}

		default List<Region> at(Location location)
		{
			return ImmutableList.of();
		}
	}

	public interface Region extends Named, Iterable<Vector>, Container<Vector> {

		Vector getMin();

		Vector getMax();

		boolean isGlobal();
	}

	@FunctionalInterface
	public interface RegionEngine {
		Region getRegion(String name);
	}

}
