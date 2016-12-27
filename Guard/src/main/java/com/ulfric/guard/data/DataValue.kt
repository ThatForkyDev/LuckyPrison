package com.ulfric.guard.data

import com.ulfric.lib.api.java.Named
import org.apache.commons.lang3.SerializationUtils
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.io.Serializable

abstract class DataValue<T> protected constructor(val _name: String) : Named, Serializable, ConfigurationSerializable
{
	override fun getName(): String
	{
		return _name
	}

	abstract val dataAsString: String
	abstract var data: T

	abstract fun setData(data: Any?): Boolean

	fun copy(): DataValue<T>
	{
		return SerializationUtils.clone(this)
	}

	override fun toString(): String
	{
		return "${javaClass.simpleName}[name=$name, data=$dataAsString]"
	}

	override fun serialize(): Map<String, Any?>
	{
		return mapOf("name" to name, "data" to data)
	}
}
