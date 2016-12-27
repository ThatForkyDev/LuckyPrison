package com.ulfric.lib.api.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.Lib;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.task.Tasks;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BlockModule extends SimpleModule {

	public BlockModule()
	{
		super("block", "Block utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new SignModule());
		this.withSubModule(new BlockPatternModule());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void postEnable()
	{
		BlockUtils.impl = new BlockUtils.IBlockUtils() {
			private final Set<Material> trans;

			{
				Set<Material> set = Sets.newHashSet();
				for (Material material : Material.values())
				{
					if (!material.isBlock() || material.isOccluding()) continue;

					set.add(material);
				}

				this.trans = Sets.immutableEnumSet(set);
			}

			@Override
			public MultiBlockChange newMultiChange()
			{
				return new MultiBlockChange(-1);
			}

			@Override
			public MultiBlockChange newMultiChange(int capacity)
			{
				return new MultiBlockChange(capacity);
			}

			@Override
			public boolean isEmpty(Block block)
			{
				return (block == null || block.getType() == Material.AIR);
			}

			@Override
			public boolean isSmashable(Block block)
			{
				return !block.isEmpty() && !block.isLiquid() && block.getType() != Material.BEDROCK;
			}

			@Override
			public boolean isTransparent(Material material)
			{
				return this.trans.contains(material);
			}

			@Override
			public Set<Material> getTransparent()
			{
				return this.trans;
			}

			@Override
			public void playTemporaryBlock(Player player, Location location, ItemPair pair, long delay)
			{
				player.sendBlockChange(location, pair.getType(), pair.getData().byteValue());

				Tasks.runLater(() ->
							   {
								   Block block = location.getBlock();

								   player.sendBlockChange(location, block.getType(), block.getData());
							   }, delay);
			}

			@Override
			public void playTemporaryBlocks(Player player, MultiBlockChange change, long delay)
			{
				Set<Map.Entry<Location, ItemPair>> entries = change.getBlocks();

				for (Map.Entry<Location, ItemPair> entry : entries)
				{
					ItemPair pair = entry.getValue();

					player.sendBlockChange(entry.getKey(), pair.getType(), pair.getData().byteValue());
				}

				Tasks.runLater(() ->
							   {
								   for (Map.Entry<Location, ItemPair> entry : entries)
								   {
									   Block block = entry.getKey().getBlock();

									   player.sendBlockChange(entry.getKey(), block.getType(), block.getData());
								   }
							   }, delay);
			}

			@Override
			public void playBlock(Player player, Location location, ItemPair pair)
			{
				player.sendBlockChange(location, pair.getType(), pair.getData().byteValue());
			}

			@Override
			public void playBlocks(Player player, MultiBlockChange change)
			{
				for (Map.Entry<Location, ItemPair> entry : change.getBlocks())
				{
					ItemPair pair = entry.getValue();

					player.sendBlockChange(entry.getKey(), pair.getType(), pair.getData().byteValue());
				}
			}

			@Override
			public void playBlockEffect(Player player, Location location, Material type)
			{
				if (player == null)
				{
					for (int x = 0; x < 3; x++)
					{
						location.getWorld().playEffect(location, Effect.STEP_SOUND, type);
					}

					return;
				}
				for (int x = 0; x < 3; x++)
				{
					player.playEffect(location, Effect.STEP_SOUND, type);
				}
			}
		};
	}

	private static final class SignModule extends SimpleModule {
		SignModule()
		{
			super("sign", "Sign utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			SignUtils.impl = new SignUtils.ISignUtils() {
				@Override
				public boolean isSign(Block block)
				{
					return (block.getState() instanceof Sign);
				}

				@Override
				public Sign asSign(Block block)
				{
					return (Sign) block.getState();
				}

				@Override
				public void setLines(Sign sign, List<String> lines)
				{
					for (int x = 0; x < lines.size() && x < sign.getLines().length; x++)
					{
						sign.setLine(x, lines.get(x));
					}

					sign.update();
				}

				@Override
				public void setLines(Sign sign, String... lines)
				{
					for (int x = 0; x < lines.length && x < sign.getLines().length; x++)
					{
						sign.setLine(x, lines[x]);
					}

					sign.update();
				}
			};
		}

		@Override
		public void postDisable()
		{
			SignUtils.impl = SignUtils.ISignUtils.EMPTY;
		}
	}

	private static final class BlockPatternModule extends SimpleModule {
		BlockPatternModule()
		{
			super("blockpattern", "BlockPattern class module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			BlockPattern.impl = new BlockPattern.IBlockPattern() {
				private Map<String, BlockPattern> patterns;

				{
					this.load();
				}

				@Override
				public BlockPattern getPattern(String name)
				{
					return this.patterns.get(name.toLowerCase());
				}

				private void load()
				{
					File folder = new File(Lib.get().getDataFolder(), "patterns");

					if (folder.mkdirs())
					{
						this.patterns = ImmutableMap.of();

						return;
					}

					File[] files = folder.listFiles();

					this.patterns = Maps.newHashMapWithExpectedSize(files.length);

					for (File file : files)
					{
						String name = file.getName();

						this.patterns.put(name.substring(0, name.length() - 4), new BlockPattern(YamlConfiguration.loadConfiguration(file)));
					}
				}
			};
		}

		@Override
		public void postDisable()
		{
			BlockPattern.impl = BlockPattern.IBlockPattern.EMPTY;
		}
	}

	@Override
	public void postDisable()
	{
		BlockUtils.impl = BlockUtils.IBlockUtils.EMPTY;
	}


}
