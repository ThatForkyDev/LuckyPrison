package com.ulfric.lib.api.java;

import java.util.UUID;

public final class Uuids {


	static IUuid impl = IUuid.EMPTY;

	private Uuids()
	{
	}

	public static UUID parse(String string)
	{
		return impl.parse(string);
	}

	public static String dashify(String string)
	{
		return impl.dashify(string);
	}

	protected interface IUuid {
		IUuid EMPTY = new IUuid() {
		};

		default UUID parse(String string)
		{
			return null;
		}

		default String dashify(String string)
		{
			return null;
		}
	}


}
