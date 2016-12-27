package com.ulfric.lib.api.hook;

import com.ulfric.lib.api.hook.EconHook.IEconHook;
import com.ulfric.lib.api.java.Unique;
import com.ulfric.lib.api.server.Events;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public final class EconHook extends Hook<IEconHook> {

	EconHook()
	{
		super(IEconHook.EMPTY, "Cash", "Cash hook module", "Packet", "1.0.0-REL");
	}

	public boolean takeMoney(UUID uuid, double amount, String reason)
	{
		return this.impl.takeMoney(uuid, amount, reason);
	}

	public void giveMoney(UUID uuid, double amount, String reason)
	{
		this.impl.giveMoney(uuid, amount, reason);
	}

	public double getMoney(UUID uuid)
	{
		return this.impl.getMoney(uuid);
	}

	public boolean takeTokens(UUID uuid, int amount)
	{
		return this.impl.takeTokens(uuid, amount);
	}

	public void giveTokens(UUID uuid, int amount)
	{
		this.impl.giveTokens(uuid, amount);
	}

	public int getTokens(UUID uuid)
	{
		return this.impl.getTokens(uuid);
	}

	public Price price(String price)
	{
		return this.impl.price(price);
	}

	public Price priceMoney(double price)
	{
		return this.impl.priceMoney(price);
	}

	public Price priceTokens(int price)
	{
		return this.impl.priceTokens(price);
	}

	public interface IEconHook extends HookImpl {
		IEconHook EMPTY = new IEconHook() {
		};

		default boolean takeMoney(UUID uuid, double amount, String reason)
		{
			return false;
		}

		default void giveMoney(UUID uuid, double amount, String reason)
		{
		}

		default double getMoney(UUID uuid)
		{
			return 0;
		}

		default boolean takeTokens(UUID uuid, int amount)
		{
			return false;
		}

		default void giveTokens(UUID uuid, int amount)
		{
		}

		default int getTokens(UUID uuid)
		{
			return 0;
		}

		default Price price(String price)
		{
			return null;
		}

		default Price priceMoney(double price)
		{
			return null;
		}

		default Price priceTokens(int price)
		{
			return null;
		}
	}

	public interface Price {
		boolean isToken();

		Number getAmount();

		Number getRemaining(UUID uuid);

		void give(UUID uuid, String reason);

		boolean take(UUID uuid, String reason);

		void setBal(UUID uuid);

		Price add(Price price);

		Price subtract(Price price);

		@Override
		String toString();

		String toString(boolean flag);
	}

	public static final class BalanceChangeEvent extends Event implements Cancellable, Unique {
		private static final HandlerList HANDLERS = Events.newHandlerList();
		private final UUID uuid;
		private final double immutableDifference;
		private final String reason;
		private double newBalance;
		private boolean cancel;

		public BalanceChangeEvent(UUID uuid, double newBalance, double difference, String reason)
		{
			this.uuid = uuid;

			this.newBalance = newBalance;

			this.immutableDifference = difference;

			this.reason = reason;
		}

		public static HandlerList getHandlerList()
		{
			return HANDLERS;
		}

		@Override
		public UUID getUniqueId()
		{
			return this.uuid;
		}

		public double getNewBalance()
		{
			return this.newBalance;
		}

		public void setNewBalance(double newBalance)
		{
			this.newBalance = newBalance;
		}

		public double getImmutableDifference()
		{
			return this.immutableDifference;
		}

		public String getReason()
		{
			return this.reason;
		}

		@Override
		public boolean isCancelled()
		{
			return this.cancel;
		}

		@Override
		public void setCancelled(boolean cancel)
		{
			this.cancel = cancel;
		}

		@Override
		public HandlerList getHandlers()
		{
			return HANDLERS;
		}
	}

}
