package com.ulfric.lib.api.teleport;

import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.nms.CraftEntityVI;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.TaskAlreadyRunningException;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.uspigot.Locatable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class TeleportTask extends ATask implements Locatable {

	private final Entity entity;
	private final Location startingLocation;
	private final boolean noisy;
	private final boolean usenms;
	private Location location;
	private boolean enforce;
	private long delay;
	private double distance;

	TeleportTask(Entity entity, Location location, int delay, boolean noisy, boolean enforce, boolean nms)
	{
		this.entity = entity;
		this.startingLocation = entity.getLocation();
		this.location = location;
		this.usenms = nms;
		this.noisy = noisy;
		this.delay = entity.getLocation().getY() < 0 || entity.hasPermission("lib.teleport.instant") ? 0 : delay;
		this.enforce = enforce;

		if (Events.call(new EntityTeleportTaskBuildEvent(this)).isCancelled()) return;

		this.enforce = this.enforce && this.delay > 0;

		TeleportUtils.addTask(this);

		if (!this.noisy || this.delay <= 0) return;

		if (entity instanceof Player)
		{
			Locale.send((Player) entity, "lib.teleport_warmup", delay, delay > 1 ? 's' : Strings.BLANK);

			return;
		}

		Locale.send(entity, "lib.teleport_warmup", delay, delay > 1 ? 's' : Strings.BLANK);
	}

	public Entity getEntity()
	{
		return this.entity;
	}

	public Location getStartingLocation()
	{
		return this.startingLocation;
	}

	@Override
	public Location getLocation()
	{
		return this.location;
	}

	public void setLocation(Location location)
	{
		if (this.isRunning())
		{
			throw new TaskAlreadyRunningException();
		}

		this.location = location;
	}

	public boolean getEnforce()
	{
		return this.enforce;
	}

	public boolean getNoisy()
	{
		return this.noisy;
	}

	public boolean getUseNms()
	{
		return this.usenms;
	}

	public long getDelay()
	{
		return this.delay;
	}

	public void setDelay(long delayInSeconds)
	{
		this.delay = delayInSeconds;
	}

	public long getDelayInTicks()
	{
		return Ticks.fromSeconds(this.delay);
	}

	public double getDistance()
	{
		return this.distance;
	}

	@Override
	public void start()
	{
		super.start();

		Metadata.apply(this.entity, "_ulf_teleporting", this);

		this.distance = this.location.distance(this.startingLocation);

		enforce:
		if (this.enforce)
		{
			if (this.distance > 1) break enforce;

			if (this.kill(TeleportCancelReason.TOO_CLOSE)) return;
		}

		this.setTaskId(Tasks.runLater(this, this.getDelayInTicks()).getTaskId());
	}

	@Override
	public void annihilate()
	{
		super.annihilate();

		Metadata.remove(this.entity, "_ulf_teleporting");

		TeleportUtils.clearTask(this.entity);
	}

	@Override
	public void run()
	{
		Entity entity = this.entity;
		boolean success = true;
		TeleportCancelReason reason = TeleportCancelReason.OTHER;

		if (this.enforce)
		{
			Location current = entity.getLocation();

			if (this.startingLocation.getWorld().equals(current.getWorld()) && this.startingLocation.distanceSquared(current) > (1.25 * 1.25))
			{
				reason = TeleportCancelReason.TOO_FAR;

				success = false;
			}
		}

		if (!success)
		{
			if (this.kill(reason)) return;
		}

		if (entity instanceof Player)
		{
			Locale.send((Player) entity, "lib.teleport_success");
		}
		else
		{
			Locale.send(entity, "lib.teleport_success");
		}

		Location location = this.location;

		if (this.usenms)
		{ CraftEntityVI.of(entity).getHandle().setPosition(location.getX(), location.getY(), location.getZ()); }

		else { entity.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN); }

		this.annihilate();
	}

	public boolean kill()
	{
		return this.kill(TeleportCancelReason.OTHER);
	}

	public boolean kill(TeleportCancelReason reason)
	{
		if (Events.call(new EntityTeleportTaskCancelEvent(this, reason)).isCancelled()) return false;

		if (this.noisy)
		{
			if (this.entity instanceof Player)
			{
				Locale.sendError((Player) this.entity, "lib.teleport_err", reason.getMessage());
			}
			else
			{
				Locale.sendError(this.entity, "lib.teleport_err", reason.getMessage());
			}
		}

		Tasks.cancel(this.getTaskId());

		this.annihilate();

		return true;
	}

	public enum TeleportCancelReason {
		DAMAGE("you were damaged!"),
		TOO_FAR("you moved too far!"),
		TOO_CLOSE("you are too close to the teleport destination."),
		OTHER("unknown.");

		private final String message;

		TeleportCancelReason(String message)
		{
			this.message = message;
		}

		public String getMessage()
		{
			return this.message;
		}
	}

}
