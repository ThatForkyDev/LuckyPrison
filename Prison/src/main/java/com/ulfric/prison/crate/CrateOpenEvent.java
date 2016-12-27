package com.ulfric.prison.crate;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.ulfric.lib.api.server.Events;

public class CrateOpenEvent extends PlayerEvent {

	public CrateOpenEvent(Player who, Crate crate)
	{
		super(who);

		this.crate = crate;
	}

	private final Crate crate;

	public Crate getCrate()
	{
		return this.crate;
	}

	private static final HandlerList HANDLERS = Events.newHandlerList();
	@Override
	public HandlerList getHandlers() { return CrateOpenEvent.HANDLERS; }
	public static HandlerList getHandlerList() { return CrateOpenEvent.HANDLERS; }

}