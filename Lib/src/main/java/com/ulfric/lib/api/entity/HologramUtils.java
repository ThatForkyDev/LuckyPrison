package com.ulfric.lib.api.entity;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public final class HologramUtils {

	static IHologramUtils impl = IHologramUtils.EMPTY;

	private HologramUtils()
	{
	}

	public static ArmorStand[] spawn(Location location, String text)
	{
		return impl.spawn(location, text);
	}

	protected interface IHologramUtils {
		IHologramUtils EMPTY = new IHologramUtils() {
		};

		default ArmorStand[] spawn(Location location, String text)
		{
			return null;
		}
	}

}
