package com.ulfric.lib.api.teleport;

import com.ulfric.lib.api.server.Events;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

public final class EntityTeleportTaskBuildEvent extends EntityTeleportTaskEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();

	public EntityTeleportTaskBuildEvent(TeleportTask task)
	{
		super(task);
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	public void setDelay(long delayInSeconds)
	{
		this.getTask().setDelay(delayInSeconds);
	}

	public void setLocation(Location location)
	{
		this.getTask().setLocation(location);
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

}
