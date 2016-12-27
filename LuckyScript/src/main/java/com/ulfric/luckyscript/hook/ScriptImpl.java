package com.ulfric.luckyscript.hook;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.hook.ScriptHook.Script;
import com.ulfric.luckyscript.lang.Scripts;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class ScriptImpl implements Script {

	protected ScriptImpl(String name, com.ulfric.luckyscript.lang.Script script)
	{
		this.name = name;

		this.script = script;
	}

	private final String name;

	private com.ulfric.luckyscript.lang.Script script;

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		if (this.script != null)
		{
			this.script.run(player, object);

			return;
		}

		this.script = Scripts.getScript(this.name);

		if (this.script == null) return;

		this.script.run(player, object);
	}

}