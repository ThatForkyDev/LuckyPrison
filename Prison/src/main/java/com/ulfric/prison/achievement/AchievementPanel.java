package com.ulfric.prison.achievement;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.gui.Panel;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.task.Tasks;

public class AchievementPanel extends Panel {

	public static AchievementPanel open(AchievementCategory category, Player player)
	{
		return new AchievementPanel(category, player);
	}

	private AchievementPanel(AchievementCategory category, Player player)
	{
		super("achievement", Locale.getMessage("prison.achievement_panel_category_" + category.getName()), (int) Numbers.roundUp(category.size(), 9), player);

		this.allowNone();

		this.category = category;
	}

	private AchievementCategory category;

	@Override
	public void onOpen(Player player)
	{
		Tasks.run(() ->
		{
			Collection<Achievement> achievements = this.category.getAchievements();

			if (CollectUtils.isEmpty(achievements)) return;

			UUID uuid = player.getUniqueId();

			String completed = Locale.getMessage(player, "prison.achievement_completed");
			String progress = Locale.getMessage(player, "prison.achievement_progress");

			achievements.forEach(ach ->
			{
				double count = ach.getProgress(uuid);

				double min = ach.getMin();

				ItemStack item = ach.getItem().clone();

				ItemMeta meta = item.getItemMeta();

				List<String> lore = meta.hasLore() ? meta.getLore() : Lists.newArrayList();

				lore.add(Strings.BLANK);

				if (count >= min)
				{
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

					meta.addEnchant(Enchantment.DURABILITY, 0, false);

					lore.add(completed);
				}
				else
				{
					lore.add(Strings.format(progress, StringUtils.formatNumber(Numbers.percentage(count, min))));
				}

				if (ach.hasParents())
				{
					lore.add(Strings.BLANK);

					lore.add(Strings.format(Locale.getMessage(player, "prison.achievement_parents"),
							StringUtils.mergeNicely(ach.getParents().stream().map(Achievement::getFormattedName).collect(Collectors.toList()))));
				}

				meta.setLore(lore);

				item.setItemMeta(meta);

				this.addItem(item);
			});
		});
	}

}