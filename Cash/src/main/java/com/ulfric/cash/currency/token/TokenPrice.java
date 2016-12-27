package com.ulfric.cash.currency.token;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.ulfric.cash.currency.Price;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;

public class TokenPrice extends Price {

	public TokenPrice(int amount)
	{
		this.amount = amount;
	}

	private final int amount;
	@Override
	public Integer getAmount() { return this.amount; }

	@Override
	public Integer getRemaining(UUID uuid)
	{
		int balance = Tokens.getBalance(uuid);

		if (balance > this.amount) return 0;

		return this.amount - balance;
	}

	@Override
	public void give(UUID uuid)
	{
		Bukkit.getLogger().info(Strings.format("[TOKEN] User:{0} Amt:{1} Reason:{2}", uuid, this.amount, "UNKNOWN"));

		Tokens.give(uuid, this.amount);
	}

	@Override
	public void give(UUID uuid, String reason)
	{
		Bukkit.getLogger().info(Strings.format("[TOKEN] User:{0} Amt:{1} Reason:{2}", uuid, this.amount, reason));

		Tokens.give(uuid, this.amount);
	}

	@Override
	public boolean take(UUID uuid, String reason)
	{
		Bukkit.getLogger().info(Strings.format("[TOKEN] User:{0} Amt:-{1} Reason:{2}", uuid, this.amount, reason));

		return Tokens.take(uuid, this.amount);
	}

	@Override
	public void setBal(UUID uuid)
	{
		Tokens.set(uuid, this.getAmount());
	}

	@Override
	public String toString()
	{
		return StringUtils.formatNumber(this.amount) + " Tokens";
	}

	@Override
	public String toString(boolean small)
	{
		return StringUtils.formatNumber(this.amount) + "T";
	}

	@Override
	public TokenPrice add(Price price)
	{
		if (!(price instanceof TokenPrice)) return this;

		return new TokenPrice(this.amount + ((TokenPrice) price).getAmount());
	}

	@Override
	public TokenPrice subtract(Price price)
	{
		if (!(price instanceof TokenPrice)) return this;

		return new TokenPrice(this.amount - ((TokenPrice) price).getAmount());
	}

}