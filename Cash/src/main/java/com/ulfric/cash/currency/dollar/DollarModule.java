package com.ulfric.cash.currency.dollar;

import java.util.UUID;

import com.ulfric.cash.currency.dollar.Money.IMoney;
import com.ulfric.lib.api.hook.EconHook.BalanceChangeEvent;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.time.TimeUtils;

public class DollarModule extends SimpleModule {

	public DollarModule()
	{
		super("dollar", "Dollar currency implementation", "Packet", "1.0.0-REL");

		this.withSubModule(new EconListModule());

		this.addCommand("balance", new CommandBalance());
	}

	@Override
	public void postEnable()
	{
		Money.impl = new IMoney()
		{
			private final Object lock = new Object();

			@Override
			public double getBalance(UUID uuid)
			{
				synchronized (this.lock)
				{
					return Hooks.DATA.getPlayerDataAsDouble(uuid, "currency.cash");
				}
			}

			@Override
			public boolean take(UUID uuid, double amount)
			{
				return Money.take(uuid, amount, Strings.BLANK);
			}

			@Override
			public boolean take(UUID uuid, double amount, String reason)
			{
				double value = Money.getBalance(uuid) - amount;

				if (value < 0) return false;

				this.setBalance(uuid, value, amount, reason);

				return true;
			}

			@Override
			public void give(UUID uuid, double amount)
			{
				this.setBalance(uuid, amount + Money.getBalance(uuid), amount, Strings.BLANK);
			}

			@Override
			public void give(UUID uuid, double amount, String reason)
			{
				this.setBalance(uuid, amount + Money.getBalance(uuid), amount, reason);
			}

			@Override
			public void set(UUID uuid, double amount)
			{
				Hooks.DATA.setPlayerData(uuid, "currency.cash", amount);
			}

			private void setBalance(UUID uuid, double balance, double change, String reason)
			{
				synchronized (this.lock)
				{
					BalanceChangeEvent event = new BalanceChangeEvent(uuid, balance, change, reason);

					if (Events.call(event).isCancelled()) return;

					balance = event.getNewBalance();

					Hooks.DATA.setPlayerData(uuid, "currency.cash", balance);

					if (reason.isEmpty()) return;

					Hooks.DATA.addToPlayerDataCollection(uuid, "currency.history", Strings.format("ti.{0} ch.{1} ne.{2} re.{3}", Strings.fakeSpace(TimeUtils.formatCurrentDateFully()), change, balance, Strings.fakeSpace(reason)));
				}
			}
		};
	}

	@Override
	public void postDisable()
	{
		Money.impl = IMoney.EMPTY;
	}

	private class EconListModule extends SimpleModule
	{
		private EconListModule()
		{
			super("econlist", "Economy listings and information", "Packet", "1.0.0-REL");

			this.addCommand("balancetop", new CommandBalancetop());
			this.addCommand("payments", new CommandPayments());
		}
	}

}