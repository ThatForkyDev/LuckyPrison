package com.ulfric.tag;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.Plugin;
import com.ulfric.tag.command.CommandCombattag;
import com.ulfric.tag.command.CommandForcetag;
import com.ulfric.tag.command.CommandUntag;
import com.ulfric.tag.hook.CombatTagImpl;
import com.ulfric.tag.listener.ListenerCombat;
import com.ulfric.tag.modules.ModuleAllowedCombatCommands;

public class Tag extends Plugin {

	private static Tag i;
	public static Tag get() { return Tag.i; }

	@Override
	public void preLoad()
	{
		this.withSubModule(new ModuleAllowedCombatCommands());
	}

	@Override
	public void load()
	{
		Tag.i = this;

		this.addListener(new ListenerCombat());

		this.addCommand("combattag", new CommandCombattag());
		this.addCommand("forcetag", new CommandForcetag());
		this.addCommand("untag", new CommandUntag());

		this.registerHook(Hooks.COMBATTAG, CombatTagImpl.INSTANCE);
	}

	@Override
	public void disable()
	{
		Tag.i = null;
	}

}