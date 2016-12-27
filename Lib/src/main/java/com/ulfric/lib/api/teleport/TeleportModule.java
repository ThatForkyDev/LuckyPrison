package com.ulfric.lib.api.teleport;

import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerDamageEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.WeakHashMap;

public final class TeleportModule extends SimpleModule {

	public TeleportModule()
	{
		super("teleport", "Teleport utilities module", "Packet", "1.0.0-REL");

		this.addListener(new Listener() {
			@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
			public void onPlayerDamage(PlayerDamageEvent event)
			{
				Player player = event.getPlayer();

				if (!player.hasMetadata("_ulf_teleporting")) return;

				TeleportTask task = Metadata.getValue(player, "_ulf_teleporting");

				task.kill(TeleportTask.TeleportCancelReason.TOO_FAR);
			}
		});
	}

	@Override
	public void postEnable()
	{
		TeleportUtils.impl = new TeleportUtils.ITeleportUtils() {
			private final Map<Entity, TeleportTask> tasks = new WeakHashMap<>();

			@Override
			public void addTask(TeleportTask task)
			{
				this.tasks.put(task.getEntity(), task);
			}

			@Override
			public void clearTask(Entity entity)
			{
				this.tasks.remove(entity);
			}

			@Override
			public TeleportTask teleport(Entity entity, Location location)
			{
				return this.teleport(entity, location, 0);
			}

			@Override
			public TeleportTask teleport(Entity entity, Location location, int warmup)
			{
				return this.teleport(entity, location, warmup, true);
			}

			@Override
			public TeleportTask teleport(Entity entity, Location location, int warmup, boolean noisy)
			{
				return this.teleport(entity, location, warmup, noisy, true);
			}

			@Override
			public TeleportTask teleport(Entity entity, Location location, int warmup, boolean noisy, boolean enforce)
			{
				return this.teleport(entity, location, warmup, noisy, enforce, false);
			}

			@Override
			public TeleportTask teleport(Entity entity, Location location, int warmup, boolean noisy, boolean enforce, boolean nms)
			{
				TeleportTask task = new TeleportTask(entity, location, warmup, noisy, enforce, nms);

				task.start();

				return task;
			}
		};
	}

	@Override
	public void postDisable()
	{
		TeleportUtils.impl = TeleportUtils.ITeleportUtils.EMPTY;
	}
}
