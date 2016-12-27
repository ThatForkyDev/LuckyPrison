package com.ulfric.lib.api.module;

import com.ulfric.lib.api.server.Events;
import org.bukkit.event.HandlerList;

public final class ModuleDisableEvent extends ModuleEvent {


	private static final HandlerList HANDLERS = Events.newHandlerList();

	public ModuleDisableEvent(IModule module)
	{
		super(module);
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}


}
