package com.ulfric.guard.region

import org.bukkit.util.Vector

@Deprecated("")
class Cylinder(center: Vector, private val radius: Int, private val halfHeight: Int) : Shape
{
	private val centerX: Int
	private val centerY: Int
	private val centerZ: Int

	override val maxPoint: Vector
	override val minPoint: Vector
	private var hash = Integer.MIN_VALUE

	init
	{
		centerX = center.blockX
		centerY = center.blockY
		centerZ = center.blockZ

		maxPoint = Vector(centerX + radius, centerY + radius, centerZ + halfHeight)
		minPoint = Vector(centerX - radius, centerY - radius, centerZ - halfHeight)
	}

	override fun getName(): String
	{
		return "cylinder"
	}

	override fun hashCode(): Int
	{
		if (hash == Int.MIN_VALUE)
		{
			var result = centerX
			result = 31 * result + centerY
			result = 31 * result + centerZ
			result = 31 * result + halfHeight
			result = 31 * result + radius
			hash = result
		}

		return hash
	}

	override fun equals(other: Any?): Boolean
	{
		if (this === other) return true
		if (other !is Cylinder) return false

		if (centerX != other.centerX) return false
		if (centerY != other.centerY) return false
		if (centerZ != other.centerZ) return false
		if (halfHeight != other.halfHeight) return false

		return radius == other.radius
	}

	override fun containsPoint(point: Vector): Boolean
	{
		// First check if the point is within the radius from our center
		val xd = centerX - point.blockX
		val yd = centerY - point.blockZ
		val distanceSqr = xd * xd + yd * yd
		if (distanceSqr > radius * radius)
		{
			return false
		}

		// Then check if it's between the caps of the cylinder
		val topZ = centerZ + halfHeight
		val bottomZ = centerZ - halfHeight
		return point.blockZ <= topZ && point.blockZ >= bottomZ
	}

	override fun iterator(): Iterator<Vector>
	{
		TODO("#NO #MATH #SKILL")
	}

	override fun serialize(): Map<String, Any>
	{
		return mapOf("type" to name, "center" to Vector(centerX, centerY, centerZ), "radius" to radius,
					 "half-height" to halfHeight)
	}

	companion object
	{
		@JvmStatic
		fun deserialize(serialized: Map<String, Any>): Cylinder
		{
			val center = serialized["center"] as Vector
			val radius = serialized["radius"] as Int
			val halfHeight = serialized["half-height"] as Int
			return Cylinder(center, radius, halfHeight)
		}
	}
}
