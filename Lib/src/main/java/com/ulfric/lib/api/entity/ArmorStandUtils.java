package com.ulfric.lib.api.entity;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public final class ArmorStandUtils {

	static IArmorStandUtils impl = IArmorStandUtils.EMPTY;

	private ArmorStandUtils() {}

	public static ArmorStand spawn(Location location)
	{
		return impl.spawn(location);
	}

	public static ArmorStand spawn(Location location, boolean notate, boolean temporary)
	{
		return impl.spawn(location, notate, temporary);
	}

	public static ArmorStand spawn(Location location, boolean temporary, Object dummy)
	{
		return impl.spawn(location, temporary, dummy);
	}

	public static void stagnate(ArmorStand stand)
	{
		impl.stagnate(stand);
	}

	public static void zero(ArmorStand stand)
	{
		impl.zero(stand);
	}

	protected interface IArmorStandUtils {
		IArmorStandUtils EMPTY = new IArmorStandUtils() {
		};

		default ArmorStand spawn(Location location)
		{
			return null;
		}

		default ArmorStand spawn(Location location, boolean notate, boolean temporary)
		{
			return null;
		}

		default ArmorStand spawn(Location location, boolean temporary, Object dummy)
		{
			return null;
		}

		default void stagnate(ArmorStand stand)
		{
		}

		default void zero(ArmorStand stand)
		{
		}
	}

}
