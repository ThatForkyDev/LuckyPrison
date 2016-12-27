package com.intellectualcrafters.plot.object;

import com.intellectualcrafters.plot.util.StringComparison;
import com.intellectualcrafters.plot.util.WorldUtil;

public class PlotItemStack {
	public final int id;
	public final short data;
	public final int amount;
	public final String name;
	public final String[] lore;

	@Deprecated
	public PlotItemStack(int id, short data, int amount, String name, String... lore)
	{
		this.id = id;
		this.data = data;
		this.amount = amount;
		this.name = name;
		this.lore = lore;
	}

	public PlotItemStack(String id, int amount, String name, String... lore)
	{
		StringComparison<PlotBlock>.ComparisonResult match = WorldUtil.IMP.getClosestBlock(id);
		PlotBlock block = match.best;
		this.id = block.id;
		this.data = block.data;
		this.amount = amount;
		this.name = name;
		this.lore = lore;
	}
}
