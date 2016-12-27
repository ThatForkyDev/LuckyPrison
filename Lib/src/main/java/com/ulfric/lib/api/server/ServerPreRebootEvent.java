package com.ulfric.lib.api.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ServerPreRebootEvent extends Event {

	private static final HandlerList HANDLERS = Events.newHandlerList();

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
