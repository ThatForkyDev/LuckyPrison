package com.ulfric.lib;

import com.ulfric.lib.api.ApiModule;
import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Wrapper;
import com.ulfric.lib.api.module.Module;
import com.ulfric.lib.api.module.Modules;
import com.ulfric.lib.api.module.Plugin;
import com.ulfric.lib.api.server.ServerPreRebootEvent;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.lib.command.LoadpluginCommand;
import com.ulfric.lib.command.ModuleCommand;
import com.ulfric.uspigot.event.server.ServerShutdownEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Plugin instance for Lib
 *
 * @author Adam
 * @version 1.0.44
 * @see Plugin
 */
public final class Lib extends Plugin {

	private static Lib i;
	private volatile boolean running = true;

	/**
	 * Gets the current Lib instance
	 *
	 * @return The Lib instance
	 * @see Lib#preLoad()
	 */
	public static Lib get()
	{
		return i;
	}

	@Override
	public void disable()
	{
		i = null;

		this.running = false;
	}

	@Override
	public void load()
	{
		this.withSubModule(new ApiModule());

		this.addCommand("module", new ModuleCommand());
		this.addCommand("loadplugin", new LoadpluginCommand());
		// this.addCommand("forcegc", sender -> System.gc()); Throwing error, System.gc() should be a bool, returns void

		this.addListener(new Listener() {
			@EventHandler
			public void onShutdown(ServerShutdownEvent event)
			{
				EntityUtils.removeNonpermanentEntities();
			}

			@EventHandler
			public void onUnload(WorldUnloadEvent event)
			{
				event.getWorld().getEntities().stream().filter(ent -> ent.hasMetadata("_ulf_temporary")).forEach(Entity::remove);
			}
		});
	}

	@Override
	public void preLoad()
	{
		i = this;

		Module module = new RestrictorModule();

		Modules.register(module);

		module.tryEnable();
	}

	@Override
	public void postEnable()
	{
		Modules.getModule("restrictor").disable();

		this.addListener(new Listener() {
			@EventHandler
			public void onShutdown(ServerShutdownEvent event)
			{
				Lib.this.running = false;
			}

			@EventHandler
			public void onReboot(ServerPreRebootEvent event)
			{
				Lib.this.running = false;
			}
		});

		Thread thread = new Thread("crash-watchdog") {
			@Override
			public void run()
			{
				Wrapper<Integer> tick = new Wrapper<>(Bukkit.getTick());
				AtomicInteger integer = new AtomicInteger();
				long minute = Milliseconds.MINUTE;
				while (Lib.this.running)
				{
					try
					{
						Thread.sleep(minute);
					}
					catch (InterruptedException e)
					{
						continue;
					}

					int newTick = Bukkit.getTick();

					if (tick.getValue() != newTick)
					{
						System.out.println("[Crash Watchdog] Passed #" + integer.incrementAndGet());

						tick.setValue(newTick);

						continue;
					}

					System.out.println("[Crash Watchdog] Failed #" + integer.incrementAndGet());

					System.out.println("[Crash Watchdog] Saving Data");
					Hooks.DATA.annihilate();
					System.out.println("[Crash Watchdog] Saving players");
					Bukkit.savePlayers();
					System.out.println("[Crash Watchdog] Saving worlds");
					Bukkit.getWorlds().forEach(World::save);
					System.out.println("[Crash Watchdog] Shutting down");
					Bukkit.shutdown();

					break;
				}
			}
		};

		Tasks.runLater(thread::start, Ticks.fromMinutes(1));
	}

}
