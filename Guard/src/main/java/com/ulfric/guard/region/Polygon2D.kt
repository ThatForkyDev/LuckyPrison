package com.ulfric.guard.region

import org.bukkit.util.Vector

internal class Polygon2D(private val points: List<Vector>, min: Int, max: Int) : Shape
{
	var maxY: Int = 0
		private set
	var minY: Int = 0
		private set
	override val maxPoint: Vector
	override val minPoint: Vector

	init
	{
		require(points.size >= 3) { "Polygons require at least 3 points" }

		if (max < min)
		{
			maxY = min
			minY = max
		}
		else
		{
			maxY = max
			minY = min
		}

		val maxX = points.maxBy { it.blockX }!!.blockX
		val maxZ = points.maxBy { it.blockZ }!!.blockZ
		val minX = points.minBy { it.blockX }!!.blockX
		val minZ = points.minBy { it.blockZ }!!.blockZ

		maxPoint = Vector(maxX, 256, maxZ)
		minPoint = Vector(minX, 0, minZ)
	}

	fun getPoints(): List<Vector>
	{
		return points
	}

	override fun getName(): String
	{
		return "polygon2d"
	}

	override fun containsPoint(point: Vector): Boolean
	{
		val targetX = point.blockX
		val targetY = point.blockY
		val targetZ = point.blockZ

		if (targetY < minY || targetY > maxY) return false

		var inside = false
		var xOld = points.last().blockX
		var zOld = points.last().blockZ

		for (vector in points)
		{
			val xNew = vector.blockX
			val zNew = vector.blockZ

			// check on corner
			if (xNew == targetX && zNew == targetZ) return true

			val x1: Int
			val z1: Int
			val x2: Int
			val z2: Int

			if (xNew > xOld)
			{
				x1 = xOld
				x2 = xNew
				z1 = zOld
				z2 = zNew
			}
			else
			{
				x1 = xNew
				x2 = xOld
				z1 = zNew
				z2 = zOld
			}

			if (x1 <= targetX && x2 > targetX)
			{
				val cross = (targetZ - z1) * (x2 - x1) - (z2 - z1) * (targetX - x1)
				if (cross == 0)
				{
					// check on edge
					if ((z1 <= targetZ) == (targetZ <= z2)) return true
				}
				else if (cross < 0 && (x1 != targetX))
				{
					inside = !inside
				}
			}

			xOld = xNew
			zOld = zNew
		}

		return inside
	}

	override fun iterator(): Iterator<Vector>
	{
		throw UnsupportedOperationException("Maths is hard")
	}

	override fun serialize(): Map<String, Any>
	{
		return mapOf("type" to name, "maxY" to maxY, "minY" to minY, "points" to points)
	}

	companion object
	{
		@JvmStatic
		fun deserialize(serialized: Map<String, Any>): Polygon2D
		{
			val maxY = serialized["maxY"] as Int
			val minY = serialized["minY"] as Int
			val points = serialized["points"] as List<Vector>
			return Polygon2D(points, minY, maxY)
		}
	}
}
