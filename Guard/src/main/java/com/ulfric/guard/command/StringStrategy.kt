package com.ulfric.guard.command

import com.ulfric.lib.api.command.arg.ArgStrategy

object StringStrategy : ArgStrategy<String>
{
	override fun match(string: String): String?
	{
		return string.toLowerCase()
	}
}
