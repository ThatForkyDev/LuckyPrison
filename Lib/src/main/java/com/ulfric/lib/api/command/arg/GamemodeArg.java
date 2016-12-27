package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.player.Gamemodes;
import org.bukkit.GameMode;

final class GamemodeArg implements ArgStrategy<GameMode> {

	@Override
	public GameMode match(String string)
	{
		return Gamemodes.parse(string);
	}

}
