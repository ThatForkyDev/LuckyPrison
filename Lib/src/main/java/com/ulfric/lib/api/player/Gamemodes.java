package com.ulfric.lib.api.player;

import org.bukkit.GameMode;

public final class Gamemodes {

	static IGamemodes impl = IGamemodes.EMPTY;

	private Gamemodes()
	{
	}

	public static GameMode parse(String string)
	{
		return impl.parse(string);
	}

	protected interface IGamemodes {
		IGamemodes EMPTY = new IGamemodes() {
		};

		default GameMode parse(String string)
		{
			return null;
		}
	}

}
