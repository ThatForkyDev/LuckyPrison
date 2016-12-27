package com.ulfric.guard.region

import com.ulfric.lib.api.java.Named
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.util.Vector

interface Shape : Iterable<Vector>, Named, ConfigurationSerializable
{
	val maxPoint: Vector
	val minPoint: Vector

	fun containsPoint(point: Vector): Boolean
}
