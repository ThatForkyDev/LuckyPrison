package com.ulfric.lib.api.module;

import org.bukkit.event.Event;

public abstract class ModuleEvent extends Event {


	private final IModule module;

	protected ModuleEvent(IModule module)
	{
		this.module = module;
	}

	public final IModule getModule()
	{
		return this.module;
	}


}
