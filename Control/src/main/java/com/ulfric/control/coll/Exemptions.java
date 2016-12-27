package com.ulfric.control.coll;

import java.util.UUID;

import com.ulfric.lib.api.java.Strings;

public class Exemptions {

	public static boolean isAdam(UUID uuid)
	{
		return uuid.equals(Strings.PACKETS_UUID);
	}

}