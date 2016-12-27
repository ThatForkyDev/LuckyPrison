package com.ulfric.guard.data

import org.apache.commons.lang3.math.NumberUtils

class DataValueInteger(name: String, override var data: Int) : DataValue<Int>(name)
{
	override val dataAsString: String
		get() = data.toString()

	override fun setData(data: Any?): Boolean
	{
		when (data)
		{
			is Number ->
			{
				this.data = data.toInt()
				return true
			}
			is String ->
			{
				val int = NumberUtils.toInt(data, Int.MIN_VALUE)
				if (int == Int.MIN_VALUE) return false

				this.data = int
				return true
			}
			else      -> return false
		}
	}

	companion object
	{
		@JvmStatic
		fun deserialize(serialized: Map<String, Any>): DataValueInteger
		{
			val name = serialized["name"] as String
			val data = serialized["data"] as Int
			return DataValueInteger(name, data)
		}
	}
}
