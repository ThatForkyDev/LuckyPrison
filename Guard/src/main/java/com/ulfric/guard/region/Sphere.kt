package com.ulfric.guard.region

import org.bukkit.util.Vector

@Deprecated("")
internal class Sphere(center: Vector, private val radius: Int) : Shape
{
	private val centerX = center.blockX
	private val centerY = center.blockY
	private val centerZ = center.blockZ

	override val maxPoint = Vector(centerX + radius, centerY + radius, centerZ + radius)
	override val minPoint = Vector(centerX - radius, centerY - radius, centerZ - radius)
	private var hash = Integer.MIN_VALUE

	override fun getName(): String
	{
		return "sphere"
	}

	override fun hashCode(): Int
	{
		if (hash == Int.MIN_VALUE)
		{
			var result = centerX
			result = 31 * result + centerY
			result = 31 * result + centerZ
			result = 31 * result + radius
			hash = result
		}

		return hash
	}

	override fun equals(other: Any?): Boolean
	{
		if (this === other) return true
		if (other !is Sphere) return false

		if (centerX != other.centerX) return false
		if (centerY != other.centerY) return false
		if (centerZ != other.centerZ) return false

		return radius == other.radius
	}

	override fun containsPoint(point: Vector): Boolean
	{
		val xd = centerX - point.blockX
		val yd = centerY - point.blockY
		val zd = centerZ - point.blockZ
		val distanceSqr = xd * xd + yd * yd + zd * zd
		return distanceSqr <= (radius * radius)
	}

	override fun iterator(): Iterator<Vector>
	{
		throw UnsupportedOperationException("#NO #MATH #SKILL")
	}

	override fun serialize(): Map<String, Any>
	{
		return mapOf("type" to name, "center" to Vector(centerX, centerY, centerZ), "radius" to radius)
	}

	companion object
	{
		@JvmStatic
		fun deserialize(serialized: Map<String, Any>): Sphere
		{
			val center = serialized["center"] as Vector
			val radius = serialized["radius"] as Int
			return Sphere(center, radius)
		}
	}
}
