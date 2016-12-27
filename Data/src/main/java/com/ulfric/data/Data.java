package com.ulfric.data;

import com.ulfric.data.coll.DataCollModule;
import com.ulfric.data.commands.CommandDelete;
import com.ulfric.data.hook.DataHookImpl;
import com.ulfric.data.modules.ModuleSavetask;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.Plugin;

public class Data extends Plugin {

	private static Data i;
	public static Data get() { return Data.i; }

	@Override
	public void load()
	{
		Data.i = this;

		this.addCommand("delete", new CommandDelete());

		this.withSubModule(new DataCollModule());
		this.withSubModule(new ModuleSavetask());

		this.registerHook(Hooks.DATA, DataHookImpl.INSTANCE);
	}

	@Override
	public void disable()
	{
		Data.i = null;
	}

}