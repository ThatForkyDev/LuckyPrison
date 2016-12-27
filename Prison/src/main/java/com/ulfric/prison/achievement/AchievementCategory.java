package com.ulfric.prison.achievement;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Named;

public class AchievementCategory implements Named {

	private static final Map<String, AchievementCategory> CATEGORIES = Maps.newHashMap();

	private static final AchievementCategory DEFAULT = AchievementCategory.builder().setName("default").setItem(ItemUtils.worthless()).build();

	public static Collection<AchievementCategory> getCategories()
	{
		return AchievementCategory.CATEGORIES.values();
	}

	public static AchievementCategory fromName(String name)
	{
		return Optional.ofNullable(AchievementCategory.CATEGORIES.get(name.trim().toLowerCase())).orElse(AchievementCategory.DEFAULT);
	}

	public static int totalSize()
	{
		return AchievementCategory.CATEGORIES.size();
	}

	private AchievementCategory(String name, ItemStack item)
	{
		this.name = name;

		this.item = item;
	}

	public int count(UUID uuid)
	{
		if (CollectUtils.isEmpty(this.achievements)) return 0;

		return (int) this.achievements.stream().filter(ach -> ach.has(uuid)).count();
	}

	private final String name;
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

	private Collection<Achievement> achievements;
	public void addAchievement(Achievement achievement)
	{
		if (this.achievements == null)
		{
			this.achievements = Lists.newArrayList();
		}

		this.achievements.add(achievement);
	}
	public Collection<Achievement> getAchievements()
	{
		return this.achievements;
	}

	public int size()
	{
		if (this.achievements == null) return 0;

		return this.achievements.size();
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder implements com.ulfric.lib.api.java.Builder<AchievementCategory>
	{
		@Override
		public AchievementCategory build()
		{
			Validate.notBlank(this.name);
			Assert.isFalse(ItemUtils.isEmpty(this.item));

			AchievementCategory category = new AchievementCategory(this.name, this.item);

			AchievementCategory.CATEGORIES.put(category.getName().trim().toLowerCase(), category);

			return category;
		}

		private String name;
		private ItemStack item;

		public Builder setName(String name)
		{
			this.name = name;

			return this;
		}

		public Builder setItem(ItemStack item)
		{
			this.item = item;

			return this;
		}
	}

}