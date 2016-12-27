package com.ulfric.lib.api.hook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.ulfric.lib.api.hook.WorldEditHook.IWEHook;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Container;
import com.ulfric.lib.api.java.Strings;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class WorldEditHook extends Hook<IWEHook> {

	private IWEHook hImpl;

	WorldEditHook()
	{
		super(IWEHook.EMPTY, "WorldEdit", "WorldEdit hook module", "Packet", "1.0.0-REL");
	}

	@Override
	public void onHook()
	{
		if (this.hImpl == null)
		{
			this.hImpl = new IWEHook() {
				@Override
				public WorldEdit getWorldEditInstance()
				{
					return WorldEdit.getInstance();
				}

				@Override
				public WorldEditSelection select(Player player)
				{
					return new WorldEditSelection(((WorldEditPlugin) WorldEditHook.this.getPlugin()).getSelection(player));
				}

				@Override
				public WorldEditSelection select(World world, WorldEditVector min, WorldEditVector max)
				{
					return new WorldEditSelection(new CuboidSelection(world, min.vector, max.vector));
				}

				@Override
				public WorldEditSelection select(World world, WorldEditVector min, WorldEditVector max, Collection<WorldEditVector> vertices)
				{
					List<BlockVector2D> vectors = Lists.newArrayListWithExpectedSize(vertices.size());
					for (WorldEditVector vector : vertices)
					{
						vectors.add(new BlockVector2D(vector.getBlockX(), vector.getBlockZ()));
					}

					return new WorldEditSelection(new Polygonal2DSelection(world, vectors, min.getBlockY(), max.getBlockY()));
				}

				@Override
				public WorldEditSelection select(World world, WorldEditVector min, WorldEditVector max, List<String> strings)
				{
					List<BlockVector2D> vectors = Lists.newArrayListWithExpectedSize(strings.size());

					for (String string : strings)
					{
						String[] split = string.split(", ");

						vectors.add(new BlockVector2D(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim())));
					}

					return new WorldEditSelection(new Polygonal2DSelection(world, vectors, min.getBlockY(), max.getBlockY()));
				}

				@Override
				public WorldEditRegion region(WorldEditSelection select)
				{
					return new WorldEditRegion(select);
				}

				@Override
				public WorldEditRegion region(World world, org.bukkit.util.Vector min, org.bukkit.util.Vector max)
				{
					return this.region(this.select(world, this.vector(min), this.vector(max)));
				}

				@Override
				public WorldEditRegion region(World world, org.bukkit.util.Vector min, org.bukkit.util.Vector max, Collection<String> verticies)
				{
					return this.region(this.select(world, this.vector(min), this.vector(max), verticies.stream().map(this::vector).collect(Collectors.toList())));
				}

				@Override
				public WorldEditVector vector(String vector)
				{
					return new WorldEditVector(vector);
				}

				@Override
				public WorldEditVector vector(org.bukkit.util.Vector vector)
				{
					return new WorldEditVector(vector);
				}
			};
		}

		this.impl(this.hImpl);
	}

	public WorldEdit getWorldEditInstance()
	{
		return this.impl.getWorldEditInstance();
	}

	public WorldEditSelection select(Player player)
	{
		return this.impl.select(player);
	}

	public WorldEditSelection select(World world, WorldEditVector min, WorldEditVector max)
	{
		return this.impl.select(world, min, max);
	}

	public WorldEditSelection select(World world, WorldEditVector min, WorldEditVector max, Collection<WorldEditVector> vertices)
	{
		return this.impl.select(world, min, max, vertices);
	}

	public WorldEditSelection select(World world, WorldEditVector min, WorldEditVector max, List<String> strings)
	{
		return this.impl.select(world, min, max, strings);
	}

	public WorldEditRegion region(WorldEditSelection select)
	{
		return this.impl.region(select);
	}

	public WorldEditRegion region(World world, org.bukkit.util.Vector min, org.bukkit.util.Vector max)
	{
		return this.impl.region(world, min, max);
	}

	public WorldEditRegion region(World world, org.bukkit.util.Vector min, org.bukkit.util.Vector max, Collection<String> verticies)
	{
		return this.impl.region(world, min, max, verticies);
	}

	public WorldEditVector vector(String vector)
	{
		return this.impl.vector(vector);
	}

	public WorldEditVector vector(org.bukkit.util.Vector vector)
	{
		return this.impl.vector(vector);
	}

	public interface IWEHook extends HookImpl {
		IWEHook EMPTY = new IWEHook() {
		};

		default WorldEdit getWorldEditInstance()
		{
			return null;
		}

		default WorldEditSelection select(Player player)
		{
			return null;
		}

		default WorldEditSelection select(World world, WorldEditVector min, WorldEditVector max)
		{
			return null;
		}

		default WorldEditSelection select(World world, WorldEditVector min, WorldEditVector max, Collection<WorldEditVector> vertices)
		{
			return null;
		}

		default WorldEditSelection select(World world, WorldEditVector min, WorldEditVector max, List<String> strings)
		{
			return null;
		}

		default WorldEditRegion region(WorldEditSelection select)
		{
			return null;
		}

		default WorldEditRegion region(World world, org.bukkit.util.Vector min, org.bukkit.util.Vector max)
		{
			return null;
		}

		default WorldEditRegion region(World world, org.bukkit.util.Vector min, org.bukkit.util.Vector max, Collection<String> verticies)
		{
			return this.region(this.select(world, this.vector(min), this.vector(max), verticies.stream().map(this::vector).collect(Collectors.toList())));
		}

		default WorldEditVector vector(String vector)
		{
			return null;
		}

		default WorldEditVector vector(org.bukkit.util.Vector vector)
		{
			return null;
		}
	}

	public final class WorldEditSelection implements Container<Location> {
		private final Object selection;

		private WorldEditSelection(Object selection)
		{
			Assert.isInstanceof(Selection.class, selection);

			this.selection = selection;
		}

		public RegionSelector getSelector()
		{
			return ((Selection) this.selection).getRegionSelector();
		}

		public boolean isCuboid()
		{
			return this.selection instanceof CuboidSelection;
		}

		public boolean isPoly()
		{
			return this.selection instanceof Polygonal2DSelection;
		}

		public int getArea()
		{
			return ((Selection) this.selection).getArea();
		}

		public WorldEditRegion asRegion()
		{
			return new WorldEditRegion(this);
		}

		public WorldEditRegion cloneRegion()
		{
			return new WorldEditRegion(this, true);
		}

		public World getWorld()
		{
			return ((Selection) this.selection).getWorld();
		}

		@Override
		public boolean contains(Location location)
		{
			return ((Selection) this.selection).contains(location);
		}

		public Location getMaxPoint()
		{
			return ((Selection) this.selection).getMaximumPoint();
		}

		public Location getMinPoint()
		{
			return ((Selection) this.selection).getMinimumPoint();
		}
	}

	public static final class WorldEditRegion implements Iterable<WorldEditVector>, Container<WorldEditVector> {
		private Region region;

		private WorldEditRegion(WorldEditSelection selection)
		{
			this(selection, false);
		}

		private WorldEditRegion(WorldEditSelection selection, boolean clone)
		{
			try
			{
				RegionSelector selector = selection.getSelector();

				if (!clone)
				{
					this.region = selector.getRegion();

					return;
				}

				this.region = selector.getRegion().clone();
			}
			catch (IncompleteRegionException exception)
			{
				exception.printStackTrace();

				Assert.isFalse(true);
			}
		}

		public WorldEditVector getMaxPoint()
		{
			return new WorldEditVector(this.region.getMaximumPoint());
		}

		public WorldEditVector getMinPoint()
		{
			return new WorldEditVector(this.region.getMinimumPoint());
		}

		public String getWorldName()
		{
			return this.region.getWorld().getName();
		}

		public List<WorldEditVector> getPoints()
		{
			if (!(this.region instanceof Polygonal2DRegion))
			{
				return ImmutableList.of();
			}

			return ((Polygonal2DRegion) this.region).getPoints().stream().map(WorldEditVector::new).collect(Collectors.toList());
		}

		@Override
		public Iterator<WorldEditVector> iterator()
		{
			Iterator<BlockVector> iter = this.region.iterator();
			return new Iterator<WorldEditVector>() {
				@Override
				public boolean hasNext()
				{
					return iter.hasNext();
				}

				@Override
				public WorldEditVector next()
				{
					return new WorldEditVector(iter.next());
				}
			};
		}

		@Override
		public boolean contains(WorldEditVector vector)
		{
			return this.region.contains(vector.vector);
		}


	}

	public static final class WorldEditVector {
		private final Vector vector;

		private WorldEditVector(BlockVector2D vector)
		{
			this.vector = new Vector(vector.getBlockX(), 0, vector.getBlockZ());
		}

		private WorldEditVector(Vector vector)
		{
			this.vector = vector;
		}

		private WorldEditVector(org.bukkit.util.Vector vector)
		{
			this.vector = new Vector(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
		}

		private WorldEditVector(String string)
		{
			String[] split = string.split(",");

			this.vector = new Vector(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()), Integer.parseInt(split[2].trim()));
		}

		public double getY()
		{
			return this.vector.getY();
		}

		public double getX()
		{
			return this.vector.getX();
		}

		public double getZ()
		{
			return this.vector.getZ();
		}

		public int getBlockY()
		{
			return this.vector.getBlockY();
		}

		public int getBlockX()
		{
			return this.vector.getBlockX();
		}

		public int getBlockZ()
		{
			return this.vector.getBlockZ();
		}

		public Location toLocation(World world)
		{
			return new Location(world, this.getX(), this.getY(), this.getZ());
		}

		public void setType(World world, ItemPair item)
		{
			world.getBlockAt(this.getBlockX(), this.getBlockY(), this.getBlockZ()).setTypeAndData(item.getType(), item.getData().byteValue(), false);
		}

		public boolean isIn(Container<WorldEditVector> container)
		{
			return container.contains(this);
		}

		@Override
		public String toString()
		{
			Vector vector = this.vector;

			return Strings.format("{0},{1},{2}", vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
		}
	}

}
