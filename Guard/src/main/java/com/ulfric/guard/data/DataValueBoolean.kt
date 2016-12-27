package com.ulfric.guard.data

import org.apache.commons.lang3.BooleanUtils

class DataValueBoolean(name: String, override var data: Boolean) : DataValue<Boolean>(name)
{
	override val dataAsString: String
		get() = data.toString()

	override fun setData(data: Any?): Boolean
	{
		when (data)
		{
			is Boolean ->
			{
				this.data = data
				return true
			}
			is String  ->
			{
				val bool = BooleanUtils.toBooleanObject(data) ?: return false
				this.data = bool
				return true
			}
			else       -> return false
		}
	}

	companion object
	{
		@JvmStatic
		fun deserialize(serialized: Map<String, Any>): DataValueBoolean
		{
			val name = serialized["name"] as String
			val data = serialized["data"] as Boolean
			return DataValueBoolean(name, data)
		}
	}
}
