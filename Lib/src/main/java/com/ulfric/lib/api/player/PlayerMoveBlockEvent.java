package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

public final class PlayerMoveBlockEvent extends PlayerMoveEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();

	public PlayerMoveBlockEvent(Player who, Location from, Location to)
	{
		super(who, from, to);
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

}
