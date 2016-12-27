package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.world.Worlds;
import org.bukkit.World;

final class WorldArg implements ArgStrategy<World> {


	@Override
	public World match(String string)
	{
		return Worlds.parse(string);
	}


}
