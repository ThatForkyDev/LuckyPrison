package com.ulfric.guard.command

import com.ulfric.guard.region.Flag
import com.ulfric.guard.region.Flags
import com.ulfric.lib.api.command.arg.ArgStrategy

object FlagStrategy : ArgStrategy<Flag<*>>
{
	override fun match(string: String): Flag<*>?
	{
		return Flags.getFlag(string)
	}
}
