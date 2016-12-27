package com.ulfric.lib.api.location;

import com.ulfric.uspigot.Locatable;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public final class LocationUtils {

	static ILocationUtils impl = ILocationUtils.EMPTY;

	private LocationUtils()
	{
	}

	public static String toString(Location location)
	{
		return impl.toString(location);
	}

	public static String toString(Location location, boolean tiny)
	{
		return impl.toString(location, tiny);
	}

	public static String toString(Location location, boolean small, boolean round)
	{
		return impl.toString(location, small, round);
	}

	public static Location fromString(String string)
	{
		return impl.fromString(string);
	}

	public static LocationProxy proxyFromString(String string)
	{
		return impl.proxyFromString(string);
	}

	public static Vector vectorFromString(String string)
	{
		return impl.vectorFromString(string);
	}

	public static LocationProxy proxy(Location location)
	{
		return impl.proxy(location);
	}

	public static Location getExact(Locatable locatable)
	{
		return impl.getExact(locatable);
	}

	public static Location getExact(Location location)
	{
		return impl.getExact(location);
	}

	public static Location round(Location location)
	{
		return impl.round(location);
	}

	public static Location center(Location location)
	{
		return impl.center(location);
	}

	public static Location centerFully(Location location)
	{
		return impl.centerFully(location);
	}

	public static Location notate(Location location)
	{
		return impl.notate(location);
	}

	protected interface ILocationUtils {
		ILocationUtils EMPTY = null;

		default String toString(Location location)
		{
			return null;
		}

		default LocationProxy proxy(Location location)
		{
			return null;
		}

		default String toString(Location location, boolean tiny)
		{
			return null;
		}

		default String toString(Location location, boolean small, boolean round)
		{
			return null;
		}

		default Location fromString(String string)
		{
			return null;
		}

		default Vector vectorFromString(String string)
		{
			return null;
		}

		default LocationProxy proxyFromString(String string)
		{
			return null;
		}

		default Location getExact(Locatable locatable)
		{
			return null;
		}

		default Location getExact(Location location)
		{
			return null;
		}

		default Location round(Location location)
		{
			return location;
		}

		default Location center(Location location)
		{
			return location;
		}

		default Location centerFully(Location location)
		{
			return location;
		}

		default Location notate(Location location)
		{
			return location;
		}
	}

}
