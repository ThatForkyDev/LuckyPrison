package com.ulfric.prison.achievement;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

public class Achievement implements Named {

	private Achievement(String name, String formattedName, ItemStack item, AchievementCategory category, Collection<AchievementListener> listeners, Collection<Achievement> parents, Collection<Consumer<Player>> consumers, double min)
	{
		this.name = name;

		this.formattedName = formattedName;

		this.path = Strings.format("achievement.{0}.{1}", category.getName(), name);

		this.item = item;

		this.category = category;

		category.addAchievement(this);

		this.parents = parents;

		this.consumers = consumers;

		listeners.forEach(listener ->
		{
			listener.supply(this);
			AchievementModule.registerListener(listener);
		});

		this.min = min;
	}

	private final String path;
	private final String name;
	private final String formattedName;
	public String getFormattedName()
	{
		return this.formattedName;
	}
	@Override
	public String getName()
	{
		return this.name;
	}

	private final ItemStack item;
	public ItemStack getItem()
	{
		return this.item;
	}

	private final AchievementCategory category;
	public AchievementCategory getCategory()
	{
		return this.category;
	}

	private final Collection<Achievement> parents;
	public Collection<Achievement> getParents()
	{
		return this.parents;
	}
	public boolean hasParents()
	{
		return !CollectUtils.isEmpty(this.parents);
	}

	private final Collection<Consumer<Player>> consumers;

	private final double min;
	public double getMin()
	{
		return this.min;
	}

	public void increment(Player player, double amount)
	{
		if (!this.increment(player.getUniqueId(), amount)) return;

		Locale.send(player, "prison.achievement_get", this.formattedName);

		this.consumers.forEach(consumer -> consumer.accept(player));
	}
	public boolean increment(UUID uuid, double amount)
	{
		if (this.has(uuid)) return false;

		for (Achievement achievement : this.parents)
		{
			if (achievement.has(uuid)) continue;

			return false;
		}

		double data = Math.min(this.min, Hooks.DATA.getPlayerDataAsDouble(uuid, this.path) + Math.abs(amount));

		Hooks.DATA.setPlayerData(uuid, this.path, data);

		return data >= this.min;
	}

	public double getProgress(UUID uuid)
	{
		return Hooks.DATA.getPlayerDataAsDouble(uuid, this.path);
	}

	public boolean has(UUID uuid)
	{
		return this.getProgress(uuid) >= this.min;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder implements com.ulfric.lib.api.java.Builder<Achievement>
	{
		private Builder() { }

		@Override
		public Achievement build()
		{
			Validate.notBlank(this.name);
			Assert.notNull(this.category);
			Assert.isFalse(ItemUtils.isEmpty(this.item));
			Collection<AchievementListener> listeners = Optional.ofNullable(this.listeners).orElseGet(Collections::emptySet);
			Collection<Achievement> parents = Optional.ofNullable(this.parents).orElseGet(Collections::emptySet);
			Collection<Consumer<Player>> rewards = Optional.ofNullable(this.rewards).orElseGet(Collections::emptySet);

			Assert.noneNull(listeners);
			Assert.noneNull(parents);
			Assert.noneNull(rewards);

			return new Achievement(this.name, this.formattedName == null ? this.name : this.formattedName, this.item, this.category, listeners, parents, rewards, Math.max(Math.abs(this.min), 0.1));
		}

		private String name;
		private String formattedName;
		private AchievementCategory category;
		private ItemStack item;
		private double min;
		private Collection<AchievementListener> listeners;
		private Collection<Achievement> parents;
		private Collection<Consumer<Player>> rewards;

		public Builder setName(String name)
		{
			this.name = name;

			return this;
		}

		public Builder setFormattedName(String name)
		{
			this.formattedName = name;

			return this;
		}

		public Builder setCategory(AchievementCategory category)
		{
			this.category = category;

			return this;
		}

		public Builder setItem(ItemStack item)
		{
			this.item = item;

			return this;
		}

		public Builder setMin(double min)
		{
			this.min = min;

			return this;
		}

		public Builder addListener(AchievementListener listener)
		{
			if (this.listeners == null)
			{
				this.listeners = Sets.newHashSet();
			}

			this.listeners.add(listener);

			return this;
		}

		public Builder addParent(Achievement achievement)
		{
			if (this.parents == null)
			{
				this.parents = Sets.newHashSet();
			}

			this.parents.add(achievement);

			return this;
		}

		public Builder addReward(Consumer<Player> consumer)
		{
			if (this.rewards == null)
			{
				this.rewards = Lists.newArrayList();
			}

			this.rewards.add(consumer);

			return this;
		}
	}

}