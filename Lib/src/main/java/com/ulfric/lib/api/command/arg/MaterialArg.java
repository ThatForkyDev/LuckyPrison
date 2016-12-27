package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.inventory.MaterialUtils;

final class MaterialArg implements ArgStrategy<ItemPair> {


	@Override
	public ItemPair match(String string)
	{
		return MaterialUtils.pair(string);
	}


}
