package com.ulfric.luckyscript.hook;

import com.ulfric.lib.api.hook.CachingEngine;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.ScriptHook.Script;
import com.ulfric.lib.api.hook.ScriptHook.ScriptEngine;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.luckyscript.lang.Scripts;

public class ScriptEngineImpl extends CachingEngine<String, Script> implements ScriptEngine {

	public ScriptEngineImpl(boolean caching)
	{
		super(caching);

		Assert.isTrue(Hooks.SCRIPT.isModuleEnabled());
	}

	@Override
	public Script getScript(String name)
	{
		name = name.toLowerCase();

		if (!this.isCaching())
		{
			com.ulfric.luckyscript.lang.Script script = Scripts.getScript(name);

			if (script == null) return null;

			return new ScriptImpl(name, script);
		}

		Script script = this.getCached(name);

		if (script != null) return script;

		com.ulfric.luckyscript.lang.Script scriptObj = Scripts.getScript(name);

		if (scriptObj == null) return null;

		script = new ScriptImpl(name, scriptObj);

		this.cache(name, script);

		return script;
	}

}