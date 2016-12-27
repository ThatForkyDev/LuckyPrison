package com.ulfric.lib.api.location;

import com.ulfric.lib.api.block.BlockModule;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.teleport.TeleportModule;
import com.ulfric.lib.api.world.WorldModule;
import com.ulfric.lib.api.world.WorldProxy;
import com.ulfric.lib.api.world.Worlds;
import com.ulfric.uspigot.Locatable;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public final class LocationModule extends SimpleModule {

	public LocationModule()
	{
		super("location", "Location utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new WorldModule());
		this.withSubModule(new ChunkModule());
		this.withSubModule(new BlockModule());
		this.withSubModule(new TeleportModule());
	}

	@Override
	public void postEnable()
	{
		LocationUtils.impl = new LocationUtils.ILocationUtils() {
			@Override
			public String toString(Location location)
			{
				return this.toString(location, false, false);
			}

			@Override
			public LocationProxy proxy(Location location)
			{
				return new LocationProxy(location);
			}

			@Override
			public String toString(Location location, boolean tiny)
			{
				return this.toString(location, tiny, tiny);
			}

			@Override
			public String toString(Location location, boolean small, boolean round)
			{
				StringBuilder builder = new StringBuilder();
				builder.append(location.getWorld().getName());
				builder.append(',');
				builder.append(round ? String.valueOf((int) location.getX()) : location.getX());
				builder.append(',');
				builder.append(round ? String.valueOf((int) location.getY()) : location.getY());
				builder.append(',');
				builder.append(round ? String.valueOf((int) location.getZ()) : location.getZ());

				if (!small)
				{
					builder.append(',');
					builder.append(round ? String.valueOf((int) location.getPitch()) : location.getPitch());
					builder.append(',');
					builder.append(round ? String.valueOf((int) location.getYaw()) : location.getYaw());
				}

				return builder.toString();
			}

			@Override
			public Location fromString(String string)
			{
				String[] parts = string.split(",");

				if (parts.length < 4) return null;

				Location location = new Location(null, 0, 0, 0);

				location.setWorld(Bukkit.getWorld(parts[0]));
				location.setX(Double.parseDouble(parts[1]));
				location.setY(Double.parseDouble(parts[2]));
				location.setZ(Double.parseDouble(parts[3]));
				if (parts.length > 4)
				{
					location.setPitch(Float.parseFloat(parts[4]));
				}
				if (parts.length > 5)
				{
					location.setYaw(Float.parseFloat(parts[5]));
				}

				return location;
			}

			@Override
			public Vector vectorFromString(String string)
			{
				String[] parts = string.split(",");

				if (parts.length < 3) return null;

				return new Vector(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
			}

			@Override
			public LocationProxy proxyFromString(String string)
			{
				String[] parts = string.split(",");

				if (parts.length < 4) return null;

				WorldProxy world = Worlds.proxy(parts[0]);
				double x = (Double.parseDouble(parts[1]));
				double y = (Double.parseDouble(parts[2]));
				double z = (Double.parseDouble(parts[3]));
				float pitch = 0;
				float yaw = 0;
				if (parts.length > 4)
				{
					pitch = Float.parseFloat(parts[4]);

					yaw = parts.length > 5 ? Float.parseFloat(parts[5]) : 0;
				}

				return new LocationProxy(world, x, y, z, pitch, yaw);
			}

			@Override
			public Location getExact(Locatable locatable)
			{
				return locatable.getLocation().getBlock().getLocation();
			}

			@Override
			public Location getExact(Location location)
			{
				return location.getBlock().getLocation();
			}

			@Override
			public Location round(Location location)
			{
				return location.setX((double) location.getBlockX()).setY((double) location.getBlockY()).setZ((double) location.getBlockZ());
			}

			@Override
			public Location center(Location location)
			{
				return location.setX(location.getBlockX() + 0.5).setZ(location.getBlockZ() + 0.5);
			}

			@Override
			public Location centerFully(Location location)
			{
				return LocationUtils.center(location).setY(location.getBlockY() + 0.5);
			}

			@Override
			public Location notate(Location location)
			{
				location.setPitch(0);
				location.setYaw(0);

				return location;
			}
		};
	}

	private static final class ChunkModule extends SimpleModule {
		ChunkModule()
		{
			super("chunk", "Chunk utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			ChunkUtils.impl = new ChunkUtils.IChunkUtils() {
				@Override
				public String toString(Chunk chunk)
				{
					return chunk.getWorld().getName() + ',' + chunk.getX() + ',' + chunk.getZ();
				}

				@Override
				public Chunk fromString(String string)
				{
					String[] split = string.split(",");

					World world = Worlds.parse(split[0]);

					if (world == null) return null;

					return world.getChunkAt(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
				}
			};
		}

		@Override
		public void postDisable()
		{
			ChunkUtils.impl = ChunkUtils.IChunkUtils.EMPTY;
		}
	}

	@Override
	public void postDisable()
	{
		LocationUtils.impl = LocationUtils.ILocationUtils.EMPTY;
	}


}
