package com.ulfric.lib.api.server;

import com.ulfric.lib.api.module.IModule;
import com.ulfric.lib.api.module.Plugin;
import com.ulfric.lib.api.module.SimpleModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public final class PluginsModule extends SimpleModule {

	public PluginsModule()
	{
		super("plugins", "Plugin utilities parent module", "Packet", "1.0.0-REL");

		this.withSubModule(new EventsModule());
		this.withSubModule(new CommandsModule());
		this.withSubModule(new ListenersModule());
	}

	private static final class EventsModule extends SimpleModule {
		EventsModule()
		{
			super("events", "Event utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Events.impl = new Events.IEvents() {
				@Override
				public <T extends Event> T call(T event)
				{
					Bukkit.getPluginManager().callEvent(event);

					return event;
				}

				@Override
				public HandlerList newHandlerList()
				{
					return new HandlerList();
				}
			};
		}

		@Override
		public void postDisable()
		{
			Events.impl = Events.IEvents.EMPTY;
		}
	}

	private static final class CommandsModule extends SimpleModule {
		CommandsModule()
		{
			super("commands", "Command utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Commands.impl = new Commands.ICommands() {
				@Override
				public void register(IModule module, String command, CommandExecutor executor)
				{
					if (executor == null)
					{
						module.log("Module {0} tried to load a command {1} with a null executor", module.getName(), command);

						return;
					}

					if (command == null || command.isEmpty())
					{
						module.log("Module {0} tried to load a null or empty command", module.getName());

						return;
					}

					Plugin plugin = module.getOwningPlugin();
					if (plugin == null)
					{
						module.log("Module {0} tried to load command '{1}' with a null owning plugin", module.getName(), command);

						return;
					}

					PluginCommand pcommand = plugin.getCommand(command);
					if (pcommand == null)
					{
						module.log("Module {0} tried to load an invalid command: {1}", module.getName(), command);

						return;
					}

					pcommand.setExecutor(executor);
				}

				@Override
				public void dispatch(String command)
				{
					Commands.dispatch(Bukkit.getConsoleSender(), command);
				}

				@Override
				public void dispatch(CommandSender sender, String command)
				{
					Bukkit.getServer().dispatchCommand(sender, command);
				}
			};
		}

		@Override
		public void postDisable()
		{
			Commands.impl = Commands.ICommands.EMPTY;
		}
	}

	private static final class ListenersModule extends SimpleModule {
		ListenersModule()
		{
			super("listeners", "Listener utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Listeners.impl = new Listeners.IListeners() {
				@Override
				public void register(IModule module, Listener listener)
				{
					Bukkit.getPluginManager().registerEvents(listener, module.getOwningPlugin());
				}

				@Override
				public void register(IModule module, Listener... listeners)
				{
					for (Listener listener : listeners)
					{
						this.register(module, listener);
					}
				}
			};
		}

		@Override
		public void postDisable()
		{
			Listeners.impl = Listeners.IListeners.EMPTY;
		}
	}

}
