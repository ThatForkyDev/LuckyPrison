package com.ulfric.actions;

import com.ulfric.actions.hook.ActionsImpl;
import com.ulfric.actions.listeners.LoggerModule;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.Plugin;

public final class Actions extends Plugin {

	private static Actions i;
	public static Actions get() { return Actions.i; }

	@Override
	public void load()
	{
		Actions.i = this;

		this.withSubModule(new LoggerModule());

		this.registerHook(Hooks.ACTIONS, ActionsImpl.INSTANCE);
	}

	@Override
	public void disable()
	{
		Actions.i = null;
	}

}