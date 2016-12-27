package com.ulfric.lib.api.world;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Proxy;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.ref.WeakReference;

public final class WorldProxy implements /*World, */Named, Proxy<World> {

	private final String name;
	private WeakReference<World> world;

	WorldProxy(String name)
	{
		this.name = name;
	}

	WorldProxy(World world)
	{
		Assert.notNull(world);

		this.name = world.getName();
		this.world = new WeakReference<>(world);
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public World asWorld()
	{
		World bukkitWorld;

		if (this.world != null)
		{
			bukkitWorld = this.world.get();
			if (bukkitWorld != null)
			{
				return bukkitWorld;
			}
		}

		bukkitWorld = Bukkit.getWorld(this.name);
		if (bukkitWorld == null) return null;

		this.world = new WeakReference<>(bukkitWorld);

		return bukkitWorld;
	}

	@Override
	@Deprecated
	public World get()
	{
		return this.asWorld();
	}

	@Override
	public int hashCode()
	{
		World bukkitWorld = this.asWorld();
		if (bukkitWorld == null) return super.hashCode();

		return bukkitWorld.hashCode();
	}

	@Override
	public boolean equals(Object object)
	{
		if (object == null) return false;
		if (object == this) return true;

		if (object instanceof WorldProxy)
		{
			return this.name.equalsIgnoreCase(((WorldProxy) object).name);
		}

		return object instanceof World && this.name.equalsIgnoreCase(((World) object).getName());
	}

}
