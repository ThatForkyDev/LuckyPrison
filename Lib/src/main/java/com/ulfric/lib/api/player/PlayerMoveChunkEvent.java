package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import com.ulfric.uspigot.event.player.CancellablePlayerEvent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class PlayerMoveChunkEvent extends CancellablePlayerEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();
	private final Chunk from;
	private final Chunk to;
	private double bounce;

	public PlayerMoveChunkEvent(Player who, Chunk from, Chunk to)
	{
		super(who);
		this.from = from;
		this.to = to;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	public double getBounce()
	{
		return this.bounce;
	}

	public void setBounce(double bounce)
	{
		this.bounce = bounce;
	}

	public boolean hasBounce()
	{
		return this.bounce > 0;
	}

	public Chunk getFrom()
	{
		return this.from;
	}

	public Chunk getTo()
	{
		return this.to;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

}
