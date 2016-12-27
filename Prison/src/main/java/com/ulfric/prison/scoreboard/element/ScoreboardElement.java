package com.ulfric.prison.scoreboard.element;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.ulfric.lib.Lib;
import com.ulfric.lib.api.java.Named;
import com.ulfric.prison.scoreboard.ModuleScoreboard;

public abstract class ScoreboardElement implements Named, Function<Player, String> {

	protected ScoreboardElement(String name, int index, long timeoutInMillis)
	{
		this.name = name;
		this.index = index;
		this.timeout = timeoutInMillis;
		this.hasTimeout = timeoutInMillis > 0;

		if (!(this instanceof Listener)) return;

		ModuleScoreboard.INSTANCE.addListener((Listener) this);
	}

	private final String name;
	private final int index;
	private final long timeout;
	private final boolean hasTimeout;

	@Override
	public final String getName()
	{
		return this.name;
	}

	public final int getIndex()
	{
		return this.index;
	}

	public final long getTimeout()
	{
		return this.timeout;
	}

	@Override
	public final String apply(Player player)
	{
		if (this.hasTimeout)
		{
			try
			{
				return Bukkit.getScheduler().callSyncMethod(Lib.get(), () -> this.get(player)).get(this.timeout, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException|ExecutionException|TimeoutException e)
			{
				e.printStackTrace();

				return null;
			}
		}

		return this.get(player);
	}

	public abstract String get(Player player);

}