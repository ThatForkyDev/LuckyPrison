package com.ulfric.prison.entity;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Annihilate;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.Timestamp;
import com.ulfric.prison.lang.Meta;

public class Minebuddy implements Annihilate {

	public Minebuddy(Player left, Player right)
	{
		this.asker = left;
		this.accepter = right;

		this.started = Timestamp.now();
	}

	private final Timestamp started;
	public Timestamp getStarted() { return this.started; }

	private final Player asker;
	private final Player accepter;
	public Player getAsker()
	{
		return this.asker;
	}

	public Player getPartner(Player player)
	{
		return this.asker.equals(player) ? this.accepter : this.asker;
	}

	private int itemTotal;
	public int itemTotal() { return this.itemTotal; }
	public void addItemTotal(int amount)
	{
		this.itemTotal += amount;
	}

	private double cashTotal;
	public double cashTotal() { return this.cashTotal; }
	public void addCashTotal(double amount)
	{
		this.cashTotal += amount;
	}

	private int tokenTotal;
	public int tokenTotal() { return this.tokenTotal; }
	public void addTokenTotal(int amount)
	{
		this.tokenTotal += amount;
	}

	private Timestamp lastSell;
	public boolean canSell()
	{
		if (this.lastSell == null)
		{
			this.lastSell = Timestamp.future(Milliseconds.fromSeconds(1.6));

			return true;
		}

		if (!this.lastSell.hasPassed()) return false;

		this.lastSell.setFuture(Milliseconds.fromSeconds(1.6));

		return true;
	}

	@Override
	public void annihilate()
	{
		Locale.send(this.asker, "prison.minebuddy_del", this.accepter.getName());
		Locale.send(this.accepter, "prison.minebuddy_del", this.asker.getName());

		Metadata.remove(this.asker, Meta.MINEBUDDY);
		Metadata.remove(this.accepter, Meta.MINEBUDDY);
	}

}