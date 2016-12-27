package com.intellectualcrafters.plot.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class RegExUtil {

	public static Map<String, Pattern> compiledPatterns;

	static
	{
		compiledPatterns = new HashMap<>();
	}

	private RegExUtil() {}
}
