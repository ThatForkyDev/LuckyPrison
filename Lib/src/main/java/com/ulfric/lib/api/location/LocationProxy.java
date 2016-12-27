package com.ulfric.lib.api.location;

import com.ulfric.lib.api.java.Proxy;
import com.ulfric.lib.api.world.WorldProxy;
import com.ulfric.lib.api.world.Worlds;
import com.ulfric.uspigot.Locatable;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Supplier;

public final class LocationProxy implements Locatable, Proxy<Location> {

	private final WorldProxy world;
	private final double x;
	private final double y;
	private final double z;
	private final float pitch;
	private final float yaw;
	private Location location;

	LocationProxy(Location location)
	{
		this(Worlds.proxy(location.getWorld()), location.getX(), location.getY(), location.getZ(),
			 location.getPitch(), location.getYaw());

		this.location = location;
	}

	LocationProxy(WorldProxy world, double x, double y, double z, float pitch, float yaw)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public WorldProxy getWorld()
	{
		return this.world;
	}

	public double getX()
	{
		return this.x;
	}

	public double getY()
	{
		return this.y;
	}

	public double getZ()
	{
		return this.z;
	}

	public float getPitch()
	{
		return this.pitch;
	}

	public float getYaw()
	{
		return this.yaw;
	}

	@Override
	public Location getLocation()
	{
		if (this.location == null)
		{
			World bukkitWorld = this.world.asWorld();
			if (bukkitWorld == null) return null;

			this.location = new Location(bukkitWorld, this.x, this.y, this.z, this.yaw, this.pitch);
		}

		return this.location;
	}

	public void teleport(Entity entity)
	{
		this.consume(entity::teleport);
	}

	public void cloneTeleport(Entity entity)
	{
		this.consumeClone(entity::teleport);
	}

	public Entity spawn(EntityType type)
	{
		Validate.notNull(type);

		Location loc = this.getLocation();

		if (loc == null) return null;

		return loc.getWorld().spawnEntity(loc, type);
	}

	public Item dropItem(ItemStack item)
	{
		return this.function(loc -> loc.getWorld().dropItem(loc, item));
	}

	public Item dropItemNaturally(ItemStack item)
	{
		return this.function(loc -> loc.getWorld().dropItemNaturally(loc, item));
	}

	public Item dropItemNaturally(ItemStack item, double xOffset, double yOffset, double zOffset)
	{
		return this.function(loc -> loc.getWorld().dropItemNaturally(loc.clone().add(xOffset, yOffset, zOffset), item));
	}

	public double distance(Location other)
	{
		return Optional.ofNullable(this.getLocation()).orElse(other).distance(other);
	}

	public double distanceSquared(Location other)
	{
		return Optional.ofNullable(this.getLocation()).orElse(other).distanceSquared(other);
	}

	public boolean isSameBlock(Location other)
	{
		if (other == null) return false;

		Location current = this.getLocation();
		return current != null && current.getBlock().equals(other.getBlock());
	}

	@Override
	@Deprecated
	public Location get()
	{
		return this.getLocation();
	}

	@Override
	public boolean equals(Object object)
	{
		if (object == null) return false;
		if (object == this) return true;

		if (object instanceof Location)
		{
			return object.equals(this.getLocation());
		}
		else if (object instanceof Locatable)
		{
			return ((Locatable) object).getLocation().equals(this.getLocation());
		}
		else
		{
			return object instanceof Supplier && this.equals(((Supplier<?>) object).get());
		}
	}

	@Override
	public int hashCode()
	{
		int result = this.world.hashCode();
		long xBits = Double.doubleToLongBits(this.x);
		result = 31 * result + (int) (xBits ^ (xBits >>> 32));
		long yBits = Double.doubleToLongBits(this.y);
		result = 31 * result + (int) (yBits ^ (yBits >>> 32));
		long zBits = Double.doubleToLongBits(this.z);
		result = 31 * result + (int) (zBits ^ (zBits >>> 32));
		result = 31 * result + (this.pitch != 0.0f ? Float.floatToIntBits(this.pitch) : 0);
		result = 31 * result + (this.yaw != 0.0f ? Float.floatToIntBits(this.yaw) : 0);
		return result;
	}
}
