package com.ulfric.lib.api.teleport;

import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityEvent;

abstract class EntityTeleportTaskEvent extends EntityEvent implements Cancellable {

	private final TeleportTask task;
	private boolean cancelled;

	protected EntityTeleportTaskEvent(TeleportTask task)
	{
		super(task.getEntity());

		this.task = task;
	}

	@Override
	public boolean isCancelled()
	{
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel)
	{
		this.cancelled = cancel;
	}

	public TeleportTask getTask()
	{
		return this.task;
	}

}
