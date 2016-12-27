package com.ulfric.lib.api.command.arg;

import java.util.Arrays;
import java.util.Objects;

public final class ExactArg implements ArgStrategy<String> {

	private final String arg;
	private final String[] aliases;

	public ExactArg(String arg, String... aliases)
	{
		this.arg = arg.toLowerCase();
		this.aliases = Arrays.stream(aliases)
				.map(Objects::requireNonNull)
				.map(String::toLowerCase)
				.toArray(String[]::new);
	}

	@Override
	public String match(String string)
	{
		string = string.toLowerCase();

		if (string.equals(this.arg)) return this.arg;

		for (String alias : this.aliases)
		{
			if (!alias.equals(string)) continue;

			return this.arg;
		}

		return null;
	}

}
