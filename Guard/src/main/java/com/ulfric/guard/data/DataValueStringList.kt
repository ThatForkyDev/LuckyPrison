package com.ulfric.guard.data

import org.bukkit.Bukkit

class DataValueStringList(name: String, override var data: List<String>) : DataValue<List<String>>(name)
{
	override val dataAsString: String
		get() = data.joinToString(", ")

	override fun setData(data: Any?): Boolean
	{
		when (data)
		{
			is List<*> ->
			{
				require(data.all { it is String })

				this.data = data as List<String>
				return true
			}
			is String  ->
			{
				val items = mutableListOf<String>()
				for (split in data.toString().split("|"))
				{
					items.add(Bukkit.getPluginCommand(split)?.let { it.name } ?: split)
				}

				this.data = items
				return true
			}
			else       -> return false
		}
	}

	companion object
	{
		@JvmStatic
		fun deserialize(serialized: Map<String, Any>): DataValueStringList
		{
			val name = serialized["name"] as String
			val data = serialized["data"] as List<String>
			return DataValueStringList(name, data)
		}
	}
}
