package com.ulfric.lib.api.block;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.math.Numbers;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class BlockPattern {

	static IBlockPattern impl = IBlockPattern.EMPTY;
	private final List<Vector> vectors;

	BlockPattern(FileConfiguration conf)
	{
		this.vectors = Lists.newArrayList();

		Map<Integer, List<String>> map = new TreeMap<>(Integer::compareTo);

		for (String key : conf.getKeys(false))
		{
			Integer number = Assert.notNull(Numbers.parseInteger(key), "The pattern key must be a number (y-axis)!");

			map.put(number, conf.getStringList(key));
		}

		int xBase = 0;
		int zBase = 0;

		for (Map.Entry<Integer, List<String>> entry : map.entrySet())
		{
			base:
			{
				int z = 0;
				for (String string : entry.getValue())
				{
					int length = string.length();

					for (int x = 0; x < length; x++)
					{
						if (string.charAt(x) != 'O') continue;

						xBase = x;
						zBase = z;

						break base;
					}

					z++;
				}
			}
		}

		for (Map.Entry<Integer, List<String>> entry : map.entrySet())
		{
			int y = entry.getKey();

			int z = 0;

			for (String string : entry.getValue())
			{
				if (string.startsWith("/"))
				{
					z++;

					continue;
				}

				int length = string.length();

				for (int x = 0; x < length; x++)
				{
					if (string.charAt(x) == '-') continue;

					this.vectors.add(new Vector(xBase - x, y, zBase - z));
				}

				z++;
			}
		}
	}

	public static BlockPattern getPattern(String name)
	{
		return impl.getPattern(name);
	}

	public List<Vector> getVectors(Vector vector)
	{
		List<Vector> vectors = Lists.newArrayListWithExpectedSize(this.vectors.size());

		for (Vector vec : this.vectors)
		{
			vectors.add(vec.clone().add(vector));
		}

		return vectors;
	}

	public List<Location> getLocations(Location location)
	{
		List<Location> locations = Lists.newArrayListWithExpectedSize(this.vectors.size());

		for (Vector vector : this.vectors)
		{
			locations.add(location.clone().add(vector));
		}

		return locations;
	}

	protected interface IBlockPattern {
		IBlockPattern EMPTY = new IBlockPattern() {
		};

		default BlockPattern getPattern(String name)
		{
			return null;
		}
	}


}
