package com.ulfric.lib.api.server;

import com.ulfric.lib.api.module.IModule;
import org.bukkit.event.Listener;

public final class Listeners {

	static IListeners impl = IListeners.EMPTY;

	private Listeners()
	{
	}

	public static void register(IModule module, Listener listener)
	{
		impl.register(module.getOwningPlugin(), listener);
	}

	public static void register(IModule module, Listener... listeners)
	{
		impl.register(module.getOwningPlugin(), listeners);
	}

	protected interface IListeners {
		IListeners EMPTY = new IListeners() {
		};

		default void register(IModule module, Listener listener)
		{
		}

		default void register(IModule module, Listener... listeners)
		{
		}
	}

}
