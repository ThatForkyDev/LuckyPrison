package com.ulfric.lib.api.world;

import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.concurrent.Future;

public final class Worlds {

	static IWorlds impl = IWorlds.EMPTY;

	private Worlds()
	{
	}

	public static World parse(String string)
	{
		return impl.parse(string);
	}

	public static boolean isMinigames(World world)
	{
		return impl.isMinigames(world);
	}

	public static World main()
	{
		return impl.main();
	}

	public static Future<World> createAsync(WorldCreator creator)
	{
		return impl.createAsync(creator);
	}

	public static WorldProxy proxy(String name)
	{
		return impl.proxy(name);
	}

	public static WorldProxy proxy(World world)
	{
		return impl.proxy(world);
	}

	protected interface IWorlds {
		IWorlds EMPTY = new IWorlds() {
		};

		default World parse(String string)
		{
			return null;
		}

		default WorldProxy proxy(World world)
		{
			return null;
		}

		default WorldProxy proxy(String name)
		{
			return null;
		}

		default Future<World> createAsync(WorldCreator creator)
		{
			return null;
		}

		default boolean isMinigames(World world)
		{
			return false;
		}

		default World main()
		{
			return null;
		}
	}

}
