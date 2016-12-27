package com.ulfric.lib.api.command.arg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexArg extends StringArg {


	private final Pattern pattern;
	private final boolean find;

	private RegexArg(String pattern)
	{
		this(pattern, false);
	}

	private RegexArg(String pattern, boolean allowFind)
	{
		this.pattern = Pattern.compile("(?i)" + pattern);

		this.find = allowFind;
	}

	public static RegexArg of(String pattern)
	{
		return new RegexArg(pattern);
	}

	public static RegexArg of(String pattern, boolean allowFind)
	{
		return new RegexArg(pattern, allowFind);
	}

	public static RegexArg quote(String pattern)
	{
		return new RegexArg(Pattern.quote(pattern));
	}

	public static RegexArg quote(String pattern, boolean allowFind)
	{
		return new RegexArg(Pattern.quote(pattern), allowFind);
	}

	@Override
	public String match(String string)
	{
		Matcher matcher = this.pattern.matcher(string);

		if (this.find)
		{
			return matcher.find() ? string : null;
		}

		return matcher.matches() ? string : null;
	}


}
