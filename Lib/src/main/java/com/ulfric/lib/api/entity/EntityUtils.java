package com.ulfric.lib.api.entity;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Set;

public final class EntityUtils {

	static IEntityUtils impl = IEntityUtils.EMPTY;

	private EntityUtils()
	{
	}

	public static EntityType parse(String value)
	{
		return impl.parse(value);
	}

	public static EntityType parseLiving(String value)
	{
		return impl.parseLiving(value);
	}

	public static void kill(Damageable damageable)
	{
		impl.kill(damageable);
	}

	public static <T extends Entity> T spawnEntity(EntityType type, Location location)
	{
		return impl.spawnEntity(type, location);
	}

	public static Entity spawnTemporaryEntity(EntityType type, Location location)
	{
		return impl.spawnTemporaryEntity(type, location);
	}

	public static void removeNonpermanentEntities()
	{
		impl.removeNonpermanentEntities();
	}

	public static Set<Entity> getAllEntities()
	{
		return impl.getAllEntities();
	}

	public static Set<LivingEntity> getNearbyLivingEntities(Location location, double distanceSquared)
	{
		return impl.getNearbyLivingEntities(location, distanceSquared);
	}

	public static Projectile launchProjectile(ProjectileSource sauce, ProjectileType type)
	{
		return impl.launchProjectile(sauce, type);
	}

	public static Projectile launchProjectile(ProjectileSource sauce, ProjectileType type, Vector vector)
	{
		return impl.launchProjectile(sauce, type, vector);
	}

	protected interface IEntityUtils {
		IEntityUtils EMPTY = new IEntityUtils() {
		};

		default EntityType parse(String value)
		{
			return null;
		}

		default EntityType parseLiving(String value)
		{
			return null;
		}

		default void kill(Damageable damageable)
		{
		}

		default <T extends Entity> T spawnEntity(EntityType type, Location location)
		{
			return null;
		}

		default Entity spawnTemporaryEntity(EntityType type, Location location)
		{
			return null;
		}

		default void removeNonpermanentEntities()
		{
		}

		default Set<Entity> getAllEntities()
		{
			return null;
		}

		default Set<LivingEntity> getNearbyLivingEntities(Location location, double distanceSquared)
		{
			return null;
		}

		default Projectile launchProjectile(ProjectileSource sauce, ProjectileType type)
		{
			return null;
		}

		default Projectile launchProjectile(ProjectileSource sauce, ProjectileType type, Vector vector)
		{
			return null;
		}
	}

}
