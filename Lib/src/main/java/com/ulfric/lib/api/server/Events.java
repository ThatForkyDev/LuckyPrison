package com.ulfric.lib.api.server;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class Events {


	static IEvents impl = IEvents.EMPTY;

	private Events()
	{
	}

	public static <T extends Event> T call(T event)
	{
		if (event == null) return null;

		Bukkit.getPluginManager().callEvent(event);

		return event;
	}

	public static HandlerList newHandlerList()
	{
		return new HandlerList();
	}

	protected interface IEvents {
		IEvents EMPTY = new IEvents() {
		};

		default <T extends Event> T call(T event)
		{
			return event;
		}

		default HandlerList newHandlerList()
		{
			return null;
		}
	}


}
