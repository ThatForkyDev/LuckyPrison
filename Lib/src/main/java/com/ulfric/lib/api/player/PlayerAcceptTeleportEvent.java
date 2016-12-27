package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import com.ulfric.uspigot.event.player.CancellablePlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class PlayerAcceptTeleportEvent extends CancellablePlayerEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();
	private final Player sender;

	public PlayerAcceptTeleportEvent(Player accepter, Player sender)
	{
		super(accepter);

		this.sender = sender;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	public Player getSender()
	{
		return this.sender;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

}
