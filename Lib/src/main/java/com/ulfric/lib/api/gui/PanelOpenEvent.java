package com.ulfric.lib.api.gui;

import com.ulfric.lib.api.server.Events;
import com.ulfric.uspigot.event.player.CancellablePlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

final class PanelOpenEvent extends CancellablePlayerEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();

	PanelOpenEvent(Player who)
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
