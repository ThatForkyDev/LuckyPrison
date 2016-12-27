package com.ulfric.lib.api.server;

import com.ulfric.lib.api.module.IModule;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class Commands {


	static ICommands impl = ICommands.EMPTY;

	private Commands()
	{
	}

	public static void register(IModule module, String command, CommandExecutor executor)
	{
		impl.register(module, command, executor);
	}

	public static void dispatch(String command)
	{
		impl.dispatch(command);
	}

	public static void dispatch(CommandSender sender, String command)
	{
		impl.dispatch(sender, command);
	}

	protected interface ICommands {
		ICommands EMPTY = new ICommands() {
		};

		default void register(IModule module, String command, CommandExecutor executor)
		{
		}

		default void dispatch(String command)
		{
		}

		default void dispatch(CommandSender sender, String command)
		{
		}
	}


}
