package com.ulfric.cash;

import com.ulfric.cash.currency.CurrencyModule;
import com.ulfric.cash.hook.EconImpl;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.Plugin;

public class Cash extends Plugin {

	private static Cash i;
	public static Cash get() { return Cash.i; }

	@Override
	public void load()
	{
		Cash.i = this;

		this.withSubModule(new CurrencyModule());

		this.registerHook(Hooks.ECON, EconImpl.INSTANCE);
	}

	@Override
	public void disable()
	{
		Cash.i = null;
	}

}