package com.ulfric.ess.tasks;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.ulfric.ess.Ess;
import com.ulfric.ess.modules.ModuleMotd;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.server.Listeners;
import com.ulfric.lib.api.server.ServerPreRebootEvent;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;

public class TaskReboot extends ATask {

	private static final TaskReboot INSTANCE = new TaskReboot();
	public static TaskReboot get() { return TaskReboot.INSTANCE; }

	private TaskReboot() { }

	private boolean rebooting;
	public boolean isRebooting() { return this.rebooting; }
	public void rebootNow()
	{
		this.rebooting = true;

		this.annihilate();

		this.run();
	}

	@Override
	public void start()
	{
		super.start();

		Instant now = Instant.now();

		ZoneId zone = TimeZone.getTimeZone("EST").toZoneId();

		ZonedDateTime time = ZonedDateTime.ofInstant(now, zone);

		boolean add = (time.getHour() >= 2);

		ZonedDateTime twoAm = ZonedDateTime.of(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), 2, 0, 0, 0, zone);

		if (add)
		{
			twoAm = twoAm.plusDays(1);
		}

		long ticks = time.until(twoAm, ChronoUnit.SECONDS) * 20;

		this.setTaskId(Tasks.runLater(this, ticks).getTaskId());
	}

	@Override
	public void run()
	{
		if (!this.rebooting)
		{
			Locale.sendMass("system.reboot_in", "60 seconds");

			Tasks.runLater(this, Ticks.MINUTE);

			this.rebooting = true;

			return;
		}

		Locale.sendMass("system.reboot_now");

		Events.call(new ServerPreRebootEvent());

		this.rebooting = true;

		ModuleMotd.get().clear();
		ModuleMotd.get().addMotd("&9- &bTHE SERVER &9-", "&9- &bIS REBOOTING &9-");

		this.runTask(() ->
		{
			Listeners.register(Ess.get(), new Listener()
			{
				private String message = ChatColor.AQUA + "THE SERVER IS REBOOTING...";

				@EventHandler(priority = EventPriority.LOWEST)
				public void onLogin(AsyncPlayerPreLoginEvent event)
				{
					event.disallow(Result.KICK_OTHER, this.message);
				}
			});

			for (Player player : Bukkit.getOnlinePlayers())
			{
				player.kickPlayer(Locale.getMessage(player, "system.reboot_kick"));
			}

			Bukkit.savePlayers();

			for (World world : Bukkit.getWorlds())
			{
				world.save();
			}
		});

		Bukkit.shutdown();
	}

	private void runTask(Runnable runnable)
	{
		if (Bukkit.isPrimaryThread())
		{
			runnable.run();

			return;
		}

		Tasks.run(runnable);
	}

}