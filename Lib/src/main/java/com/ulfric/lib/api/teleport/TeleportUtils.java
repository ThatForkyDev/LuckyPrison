package com.ulfric.lib.api.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public final class TeleportUtils {

	static ITeleportUtils impl = ITeleportUtils.EMPTY;

	private TeleportUtils()
	{
	}

	static void addTask(TeleportTask task)
	{
		impl.addTask(task);
	}

	public static void clearTask(Entity entity)
	{
		impl.clearTask(entity);
	}

	public static TeleportTask teleport(Entity entity, Location location)
	{
		return impl.teleport(entity, location);
	}

	public static TeleportTask teleport(Entity entity, Location location, int warmup)
	{
		return impl.teleport(entity, location, warmup);
	}

	public static TeleportTask teleport(Entity entity, Location location, int warmup, boolean noisy)
	{
		return impl.teleport(entity, location, warmup, noisy);
	}

	public static TeleportTask teleport(Entity entity, Location location, int warmup, boolean noisy, boolean enforce)
	{
		return impl.teleport(entity, location, warmup, noisy, enforce);
	}

	public static TeleportTask teleport(Entity entity, Location location, int warmup, boolean noisy, boolean enforce, boolean nms)
	{
		return impl.teleport(entity, location, warmup, noisy, enforce, nms);
	}

	protected interface ITeleportUtils {
		ITeleportUtils EMPTY = new ITeleportUtils() {
		};

		default void addTask(TeleportTask task)
		{
		}

		default void clearTask(Entity entity)
		{
		}

		default TeleportTask teleport(Entity entity, Location location)
		{
			return null;
		}

		default TeleportTask teleport(Entity entity, Location location, int warmup)
		{
			return null;
		}

		default TeleportTask teleport(Entity entity, Location location, int warmup, boolean noisy)
		{
			return null;
		}

		default TeleportTask teleport(Entity entity, Location location, int warmup, boolean noisy, boolean enforce)
		{
			return null;
		}

		default TeleportTask teleport(Entity entity, Location location, int warmup, boolean noisy, boolean enforce, boolean nms)
		{
			return null;
		}
	}

}
