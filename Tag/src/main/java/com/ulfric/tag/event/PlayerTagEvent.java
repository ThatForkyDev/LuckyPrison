package com.ulfric.tag.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.ulfric.lib.api.server.Events;
import com.ulfric.tag.event.enums.CombatTagStatus;

public class PlayerTagEvent extends PlayerEvent {

	public PlayerTagEvent(Player who, CombatTagStatus status)
	{
		super(who);

		this.status = status;
	}

	private final CombatTagStatus status;
	public CombatTagStatus getStatus() { return this.status; }

	private static final HandlerList HANDLERS = Events.newHandlerList();
	@Override
	public HandlerList getHandlers() { return PlayerTagEvent.HANDLERS; }
	public static HandlerList getHandlerList() { return PlayerTagEvent.HANDLERS; }

}