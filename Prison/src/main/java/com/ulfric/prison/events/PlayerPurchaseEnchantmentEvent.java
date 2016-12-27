package com.ulfric.prison.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.ulfric.lib.api.inventory.Enchant;
import com.ulfric.lib.api.server.Events;

public class PlayerPurchaseEnchantmentEvent extends PlayerEvent {

	public PlayerPurchaseEnchantmentEvent(Player who, Enchant enchant)
	{
		super(who);

		this.enchant = enchant;
	}

	private Enchant enchant;
	public Enchant getEnchant()
	{
		return this.enchant;
	}
	public void setEnchant(Enchant enchant)
	{
		this.enchant = enchant;
	}

	private static final HandlerList HANDLERS = Events.newHandlerList();
	@Override
	public HandlerList getHandlers() { return PlayerPurchaseEnchantmentEvent.HANDLERS; }
	public static HandlerList getHandlerList() { return PlayerPurchaseEnchantmentEvent.HANDLERS; }

}