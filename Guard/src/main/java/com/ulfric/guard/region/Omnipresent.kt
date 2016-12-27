package com.ulfric.guard.region

import org.bukkit.util.Vector

object Omnipresent : Shape
{
	override val maxPoint: Vector
		get() = throw UnsupportedOperationException("An omnipresent shape has no bounds")
	override val minPoint: Vector
		get() = throw UnsupportedOperationException("An omnipresent shape has no bounds")

	override fun getName(): String
	{
		return "omnipresent"
	}

	override fun containsPoint(point: Vector): Boolean
	{
		return true
	}

	override fun iterator(): Iterator<Vector>
	{
		throw UnsupportedOperationException("An omnipresent shape is everywhere")
	}

	override fun serialize(): Map<String, Any>
	{
		return mapOf("type" to name)
	}

	@JvmStatic
	fun deserialize(serialized: Map<String, Any>): Omnipresent
	{
		return this
	}
}
