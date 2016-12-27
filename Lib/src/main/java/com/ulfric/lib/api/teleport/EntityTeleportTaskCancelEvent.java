package com.ulfric.lib.api.teleport;

import com.ulfric.lib.api.server.Events;
import org.bukkit.event.HandlerList;

public final class EntityTeleportTaskCancelEvent extends EntityTeleportTaskEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();
	private TeleportTask.TeleportCancelReason reason;

	public EntityTeleportTaskCancelEvent(TeleportTask task, TeleportTask.TeleportCancelReason reason)
	{
		super(task);

		this.reason = reason;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	public TeleportTask.TeleportCancelReason getReason()
	{
		return this.reason;
	}

	public void setReason(TeleportTask.TeleportCancelReason reason)
	{
		this.reason = reason;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

}
