package com.ulfric.luckyscript;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.Plugin;
import com.ulfric.luckyscript.hook.ScriptHookImpl;
import com.ulfric.luckyscript.lang.ScriptsModule;

public class LuckyScript extends Plugin {


	private static LuckyScript i;
	public static LuckyScript get() { return LuckyScript.i; }


	@Override
	public void load()
	{
		LuckyScript.i = this;

		this.withSubModule(new ScriptsModule());

		this.registerHook(Hooks.SCRIPT, ScriptHookImpl.INSTANCE);
	}

	@Override
	public void enable()
	{
		// DO NOTHING
	}

	@Override
	public void disable()
	{
		LuckyScript.i = null;
	}


}