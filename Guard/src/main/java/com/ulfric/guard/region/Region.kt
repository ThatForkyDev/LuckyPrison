package com.ulfric.guard.region

import com.ulfric.guard.data.DataValue
import com.ulfric.guard.data.DataValueInteger
import com.ulfric.lib.api.java.Named
import org.bukkit.configuration.serialization.ConfigurationSerializable

@Suppress("UNCHECKED_CAST")
class Region(private val name: String, val world: String, val shape: Shape, flags: MutableMap<Flag<*>, DataValue<*>>?) : Named, Comparable<Region>, ConfigurationSerializable
{
	private val flags = flags ?: Flags.newEmptyFlagMap()
	val weight: Int get()
	{
		val flag = Flags.getFlag("weight")!!
		return ((getFlag(flag) ?: flag.defaultData) as DataValueInteger).data
	}

	fun <T> newFlag(flag: Flag<T>): DataValue<T>
	{
		return flags.getOrPut(flag, { flag.newData() }) as DataValue<T>
	}

	fun <T> removeFlag(flag: Flag<T>): Boolean
	{
		return flags.remove(flag) != null
	}

	fun <T> getFlag(flag: Flag<T>): DataValue<T>?
	{
		return flags[flag] as DataValue<T>?
	}

	fun getFlags(): Map<Flag<*>, DataValue<*>>
	{
		return flags
	}

	override fun getName(): String
	{
		return name
	}

	override fun equals(other: Any?): Boolean
	{
		if (other === this) return true
		if (other !is Region) return false

		if (world != other.world) return false

		return name.equals(other.name, ignoreCase = true)
	}

	override fun hashCode(): Int
	{
		var result = name.toLowerCase().hashCode()
		result = 31 * result + world.hashCode()
		return result
	}

	override fun toString(): String
	{
		return "Region[name='$name', world='$world']"
	}

	override fun compareTo(other: Region): Int
	{
		var compare = Integer.compare(weight, other.weight)
		if (compare != 0) return compare * -1

		compare = name.compareTo(other.name, ignoreCase = true)
		if (compare != 0) return compare

		return world.compareTo(other.world)
	}

	override fun serialize(): Map<String, Any>
	{
		return mapOf("name" to name, "world" to world, "shape" to shape, "flags" to flags.mapKeys { it.key.name })
	}

	companion object
	{
		@JvmStatic
		fun deserialize(serialized: Map<String, Any>): Region
		{
			val name = serialized["name"] as String
			val world = serialized["world"] as String
			val shape = serialized["shape"] as Shape
			val flags = (serialized["flags"] as MutableMap<String, DataValue<*>>)
					.mapKeys { Flags.getFlag(it.key) }
					.filterKeys { it != null }
			return Region(name, world, shape, flags as MutableMap<Flag<*>, DataValue<*>>)
		}
	}
}
