package com.ulfric.cash.currency;

import com.ulfric.lib.api.command.arg.ArgStrategy;

public enum PriceArg implements ArgStrategy<Price> {


	INSTANCE;

	@Override
	public Price match(String string)
	{
		return Price.of(string);
	}


}