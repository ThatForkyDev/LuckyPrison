package com.ulfric.guard.region

import org.bukkit.util.Vector

class Point(point: Vector) : Shape
{
	override val maxPoint = Vector(point.x, point.y, point.z)
	override val minPoint = maxPoint
	private val hash = maxPoint.hashCode()

	override fun getName(): String
	{
		return "point"
	}

	override fun hashCode(): Int
	{
		return hash
	}

	override fun equals(other: Any?): Boolean
	{
		if (this === other) return true
		if (other !is Point) return false

		return maxPoint == other.maxPoint
	}

	override fun containsPoint(point: Vector): Boolean
	{
		return maxPoint == point
	}

	override fun iterator(): Iterator<Vector>
	{
		return listOf(maxPoint).iterator()
	}

	override fun serialize(): Map<String, Any>
	{
		return mapOf("type" to name, "point" to maxPoint)
	}

	companion object
	{
		@JvmStatic
		fun deserialize(serialized: Map<String, Any>): Point
		{
			return Point(serialized["point"] as Vector)
		}
	}
}
