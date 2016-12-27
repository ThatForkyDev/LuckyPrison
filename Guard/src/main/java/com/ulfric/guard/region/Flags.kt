package com.ulfric.guard.region

import com.ulfric.guard.data.DataValue
import org.apache.commons.collections4.map.CaseInsensitiveMap
import java.util.ArrayList
import java.util.TreeMap
import kotlin.comparisons.compareBy

object Flags
{
	private val flags = CaseInsensitiveMap<String, Flag<*>>()

	fun getFlag(name: String): Flag<*>?
	{
		return flags[name]
	}

	fun getFlags(): List<Flag<*>>
	{
		return ArrayList(flags.values)
	}

	fun registerFlag(flag: Flag<*>)
	{
		if (!flags.containsKey(flag.name)) flags.put(flag.name, flag)
	}

	fun newEmptyFlagMap(): MutableMap<Flag<*>, DataValue<*>>
	{
		return TreeMap(compareBy { it.ordinal() })
	}
}
