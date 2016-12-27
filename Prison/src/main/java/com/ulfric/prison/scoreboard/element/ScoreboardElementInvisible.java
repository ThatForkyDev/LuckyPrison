package com.ulfric.prison.scoreboard.element;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

public abstract class ScoreboardElementInvisible extends ScoreboardElement implements Consumer<Player> {

	protected ScoreboardElementInvisible(String name, int index, long timeoutInMillis)
	{
		super(name, index, timeoutInMillis);
	}

	@Override
	public String get(Player player)
	{
		this.accept(player);

		return null;
	}

}