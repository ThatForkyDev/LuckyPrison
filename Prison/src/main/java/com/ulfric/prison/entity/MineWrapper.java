package com.ulfric.prison.entity;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.hook.RegionHook.Region;
import com.ulfric.lib.api.inventory.WeightedItemPair;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.WeightedWrapper;

public class MineWrapper implements Named {

	public MineWrapper(String name)
	{
		this.name = WordUtils.capitalizeFully(name.toLowerCase().replace('_', ' '));

		this.mines = Maps.newHashMap();
	}

	private String name;
	@Override
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }

	private Map<Region, WeightedWrapper<List<WeightedItemPair>>> mines;
	public Map<Region, WeightedWrapper<List<WeightedItemPair>>> getMines() { return this.mines; }

	public void addMine(Region region, List<WeightedItemPair> blocks)
	{
		this.getMines().put(region, new WeightedWrapper<>(CollectUtils.getTotalWeight(blocks), blocks));
	}

}