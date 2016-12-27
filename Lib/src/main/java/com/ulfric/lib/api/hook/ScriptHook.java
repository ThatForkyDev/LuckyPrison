package com.ulfric.lib.api.hook;

import com.ulfric.lib.api.hook.ScriptHook.IScriptHook;
import com.ulfric.uspigot.metadata.LocatableMetadatable;
import org.bukkit.entity.Player;

public final class ScriptHook extends Hook<IScriptHook> {

	ScriptHook()
	{
		super(IScriptHook.EMPTY, "LuckyScript", "LuckyScript hook module", "Packet", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		this.impl.buildEngine(true);
	}

	@Override
	public void postDisable()
	{
		this.impl.clearEngine();
	}

	public Script getScript(String name)
	{
		return this.impl.getScript(name);
	}

	public interface IScriptHook extends HookEngineImpl {
		IScriptHook EMPTY = new IScriptHook() {
		};

		default Script getScript(String name)
		{
			return null;
		}
	}

	@FunctionalInterface
	public interface Script {
		void run(Player player, LocatableMetadatable object);
	}

	@FunctionalInterface
	public interface ScriptEngine {
		Script getScript(String name);
	}

}
