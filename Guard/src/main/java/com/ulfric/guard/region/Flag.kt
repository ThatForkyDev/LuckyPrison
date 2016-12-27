package com.ulfric.guard.region

import com.ulfric.guard.data.DataValue
import com.ulfric.lib.api.java.Named

class Flag<T>(private val name: String, private val ordinal: Int, val defaultData: DataValue<T>) : Named
{
	override fun getName(): String
	{
		return this.name
	}

	fun ordinal(): Int
	{
		return this.ordinal
	}

	fun newData(): DataValue<T>
	{
		return this.defaultData.copy()
	}

	override fun equals(other: Any?): Boolean
	{
		if (this === other) return true
		if (other !is Flag<*>) return false

		if (name != other.name) return false
		if (ordinal != other.ordinal) return false
		if (defaultData != other.defaultData) return false

		return true
	}

	override fun hashCode(): Int
	{
		var result = name.hashCode()
		result = 31 * result + ordinal
		result = 31 * result + defaultData.hashCode()
		return result
	}

	override fun toString(): String
	{
		return "Flag(name='$name', ordinal=$ordinal, defaultData=$defaultData)"
	}
}
