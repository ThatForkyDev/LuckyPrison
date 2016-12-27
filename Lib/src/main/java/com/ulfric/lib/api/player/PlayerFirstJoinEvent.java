package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class PlayerFirstJoinEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();

	public PlayerFirstJoinEvent(Player who)
	{
		super(who);
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
