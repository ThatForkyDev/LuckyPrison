package com.ulfric.luckyscript.hook;

import com.ulfric.lib.api.hook.ScriptHook.IScriptHook;
import com.ulfric.lib.api.hook.ScriptHook.Script;
import com.ulfric.lib.api.hook.ScriptHook.ScriptEngine;

public enum ScriptHookImpl implements IScriptHook {

	INSTANCE;

	private ScriptEngine engine;

	@Override
	public void buildEngine(boolean cache)
	{
		this.engine = new ScriptEngineImpl(cache);
	}

	@Override
	public void clearEngine()
	{
		this.engine = null;
	}

	@Override
	public Script getScript(String name)
	{
		return this.engine.getScript(name);
	}

}