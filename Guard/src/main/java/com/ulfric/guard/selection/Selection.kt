package com.ulfric.guard.selection

import com.ulfric.guard.region.Shape
import com.ulfric.lib.api.java.Named
import org.bukkit.util.Vector

interface Selection : Named
{
	val isComplete: Boolean

	fun pushLeft(vector: Vector)

	fun pushRight(vector: Vector)

	fun toShape(): Shape
}
