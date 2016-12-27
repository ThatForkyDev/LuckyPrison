package com.ulfric.prison.enchantments.loader;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.block.BlockPattern;
import com.ulfric.lib.api.inventory.EnchantUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.prison.Prison;
import com.ulfric.prison.commands.CommandEnchants;
import com.ulfric.prison.enchantments.EnchantmentAutosell;
import com.ulfric.prison.enchantments.EnchantmentBlasting;
import com.ulfric.prison.enchantments.EnchantmentFlight;
import com.ulfric.prison.enchantments.EnchantmentMagic;
import com.ulfric.prison.enchantments.EnchantmentNeverbreaking;
import com.ulfric.prison.enchantments.EnchantmentSpeedyGonzales;
import com.ulfric.prison.enchantments.LoadableEnchantment;
import com.ulfric.prison.enchantments.loader.EnchantmentLoader.IEnchantmentLoader;

public class EnchantLoaderModule extends SimpleModule {

	public EnchantLoaderModule()
	{
		super("enchantloader", "Loads dynamic enchantments from the disk", "Packet", "1.0.0-REL");

		this.addCommand("enchants", new CommandEnchants());
	}

	@Override
	public void onFirstEnable()
	{
		EnchantUtils.setAcceptingNewEnchantsMutable(true);

		EnchantUtils.registerCustomEnchantments(EnchantmentAutosell.get(),
												EnchantmentBlasting.get(),
												EnchantmentFlight.get(),
												EnchantmentMagic.get(),
												EnchantmentNeverbreaking.get(),
												EnchantmentSpeedyGonzales.get());

		EnchantmentAutosell.get().getMainTask().start();

		EnchantmentLoader.impl = new IEnchantmentLoader()
		{
			private final Map<EnchantmentType, Set<LoadableEnchantment>> enchants = Maps.newEnumMap(EnchantmentType.class);

			{
				this.inst();
			}
			private final void inst()
			{
				for (EnchantmentType type : EnchantmentType.values())
				{
					this.enchants.put(type, Sets.newHashSet());
				}
				File folder = new File(Prison.get().getDataFolder(), "enchants");

				if (folder.mkdirs()) return;

				for (File file : folder.listFiles())
				{
					String name = file.getName();

					name = name.substring(0, name.length()-4);

					FileConfiguration conf = YamlConfiguration.loadConfiguration(file);

					EnchantmentType type = EnchantmentType.valueOf(conf.getString("type").toUpperCase());

					Builder builder = new Builder();
					builder.withId(conf.getInt("id"));
					builder.withName(name.toUpperCase());
					builder.withMax(conf.getInt("max"));
					builder.withStart(conf.getInt("start"));
					builder.withTarget(EnchantmentTarget.valueOf(conf.getString("target").toUpperCase()));
					conf.getIntegerList("conflict").forEach(builder::withConflict);

					String patternName = conf.getString("pattern.name");
					if (patternName != null)
					{
						BlockPattern pattern = BlockPattern.getPattern(patternName);
						if (pattern != null)
						{
							builder.withPattern(pattern);
						}
						else
						{
							builder.withPatternName(patternName);
						}
					}

					String potionName = conf.getString("potion.type");
					if (potionName != null)
					{
						builder.withType(PotionEffectType.getByName(potionName));
						builder.withEffectDefaultLevel(conf.getInt("potion.start"));
					}

					LoadableEnchantment enchant = builder.build();

					Set<LoadableEnchantment> enchants = this.enchants.get(type);

					enchants.add(enchant);
					EnchantUtils.registerCustomEnchantments(enchant);
				}
			}

			@Override
			public Set<LoadableEnchantment> getEnchants(EnchantmentType type)
			{
				return this.enchants.get(type);
			}
		};

		EnchantUtils.setAcceptingNewEnchantsMutable(false);
	}

	private class Builder implements com.ulfric.lib.api.java.Builder<LoadableEnchantment>
	{
		private int id;
		private int startLevel;
		private int max;
		private Set<Integer> conflict;
		private String name;
		private EnchantmentTarget target;
		private String patternName;
		private BlockPattern pattern;
		private PotionEffectType type;
		private int defaultLevel;
		private PotionEffect baseEffect;

		@Override
		public LoadableEnchantment build()
		{
			if (this.type != null)
			{
				this.baseEffect = new PotionEffect(this.type, Integer.MAX_VALUE, this.defaultLevel, false, false);
			}

			return new LoadableEnchantment(this.id)
			{
				@Override
				public BlockPattern getPattern()
				{
					return Builder.this.pattern;
				}

				@Override
				public String getPatternName()
				{
					return Builder.this.patternName;
				}

				@Override
				public PotionEffectType getEffect()
				{
					return Builder.this.type;
				}

				@Override
				public PotionEffect getBaseEffect()
				{
					return Builder.this.baseEffect;
				}

				@Override
				public boolean canEnchantItem(ItemStack item)
				{
					return Builder.this.target.includes(item);
				}

				@SuppressWarnings("deprecation")
				@Override
				public boolean conflictsWith(Enchantment enchant)
				{
					return Builder.this.conflict.contains(enchant.getId());
				}

				@Override
				public EnchantmentTarget getItemTarget()
				{
					return Builder.this.target;
				}

				@Override
				public int getMaxLevel()
				{
					return Builder.this.max;
				}

				@Override
				public String getName()
				{
					return Builder.this.name;
				}

				@Override
				public int getStartLevel()
				{
					return Builder.this.startLevel;
				}
			};
		}

		public Builder withId(int id)
		{
			Assert.isNull(EnchantUtils.getEnchantById(id), Strings.format("The ID '{0}' is already taken!", id));

			this.id = id;

			return this;
		}

		public Builder withMax(int max)
		{
			this.max = max;

			return this;
		}

		public Builder withStart(int start)
		{
			this.startLevel = start;

			return this;
		}

		public Builder withConflict(int id)
		{
			if (this.conflict == null) this.conflict = Sets.newHashSet();

			this.conflict.add(id);

			return this;
		}

		public Builder withName(String name)
		{
			Assert.isNotEmpty(name);

			this.name = name;

			return this;
		}

		public Builder withTarget(EnchantmentTarget target)
		{
			Assert.notNull(target, "The enchant target must not be null!");

			this.target = target;

			return this;
		}

		public Builder withPattern(BlockPattern pattern)
		{
			Assert.notNull(pattern, "The block pattern must not be null!");

			this.pattern = pattern;

			return this;
		}

		public Builder withPatternName(String patternName)
		{
			Assert.isNotEmpty(patternName, "The pattern name must not be null!", "The pattern name must not be empty!");

			this.patternName = patternName;

			return this;
		}

		public Builder withType(PotionEffectType type)
		{
			Assert.notNull(type, "The potion type must not be null!");

			this.type = type;

			return this;
		}

		public Builder withEffectDefaultLevel(int level)
		{
			this.defaultLevel = level;

			return this;
		}
	}

}