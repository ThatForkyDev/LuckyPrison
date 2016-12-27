package com.ulfric.lib.api.inventory;

import com.ulfric.lib.api.java.Weighted;

public final class WeightedItemPair extends ItemPair implements Weighted {

	private final int weight;

	WeightedItemPair(int weight, ItemPair pair)
	{
		super(pair.getType(), pair.getData());

		this.weight = weight;
	}

	@Override
	public int getWeight()
	{
		return this.weight;
	}

}
