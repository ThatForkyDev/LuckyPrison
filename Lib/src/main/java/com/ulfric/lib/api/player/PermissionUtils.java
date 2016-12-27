package com.ulfric.lib.api.player;

import org.bukkit.permissions.Permissible;

public final class PermissionUtils {

	static IPermissionUtils impl = IPermissionUtils.EMPTY;

	private PermissionUtils()
	{
	}

	public static int getMax(Permissible permissible, String path)
	{
		return impl.getMax(permissible, path);
	}

	public static int getMax(Iterable<String> permissions, String path)
	{
		return impl.getMax(permissions, path);
	}

	public static boolean hasNestedAccess(Permissible permissible, String path, String extra)
	{
		return impl.hasNestedAccess(permissible, path, extra);
	}

	protected interface IPermissionUtils {
		IPermissionUtils EMPTY = new IPermissionUtils() {
		};

		default int getMax(Permissible permissible, String path)
		{
			return 0;
		}

		default int getMax(Iterable<String> permissions, String path)
		{
			return 0;
		}

		default boolean hasNestedAccess(Permissible permissible, String path, String extra)
		{
			return false;
		}
	}

}
