package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import com.ulfric.uspigot.event.player.CancellablePlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class PlayerJumpEvent extends CancellablePlayerEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();

	public PlayerJumpEvent(Player who)
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
