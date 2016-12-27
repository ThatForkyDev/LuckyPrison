package com.ulfric.guard.command

import com.ulfric.lib.api.command.arg.ArgStrategy
import com.ulfric.lib.api.math.Numbers

object PositiveIntegerStrategy : ArgStrategy<Int>
{
	override fun match(string: String): Int?
	{
		val integer = Numbers.parseInteger(string)
		if (integer == null || integer <= 0) return null

		return integer
	}
}
