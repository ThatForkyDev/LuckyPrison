package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class PlayerVanishEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();
	private final boolean vanish;

	public PlayerVanishEvent(Player who, boolean vanish)
	{
		super(who);

		this.vanish = vanish;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	public boolean isVanished()
	{
		return this.vanish;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

}
