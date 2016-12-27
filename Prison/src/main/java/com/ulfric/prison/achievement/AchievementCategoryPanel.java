package com.ulfric.prison.achievement;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.gui.Panel;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.Numbers;

public class AchievementCategoryPanel extends Panel {

	public static AchievementCategoryPanel create(Player player)
	{
		return new AchievementCategoryPanel(player);
	}

	private AchievementCategoryPanel(Player player)
	{
		super(player.getName() + "achs.panel", Locale.getMessage(player, "prison.achievement_category_panel"), (int) Numbers.roundUp(AchievementCategory.totalSize(), 9), player);
	}

	@Override
	public void onClick(Player player, ItemStack item, int click)
	{
		Material material = item.getType();
		String name = item.getItemMeta().getDisplayName();

		AchievementCategory found = null;

		for (AchievementCategory category : AchievementCategory.getCategories())
		{
			ItemStack citem = category.getItem();

			if (!citem.getType().equals(material)) continue;

			if (!name.equals(citem.getItemMeta().getDisplayName())) continue;

			found = category;

			break;
		}

		if (found == null) return;

		this.close(player);

		AchievementPanel.open(found, player);
	}

	@Override
	public void onOpen(Player player)
	{
		UUID uuid = player.getUniqueId();

		for (AchievementCategory category : AchievementCategory.getCategories())
		{
			int size = category.size();

			if (size == 0) continue;

			ItemStack item = category.getItem().clone();

			ItemMeta meta = item.getItemMeta();

			List<String> lore = meta.hasLore() ? meta.getLore() : Lists.newArrayList();

			lore.add(Strings.BLANK);

			lore.add(Strings.formatF("&a{0}&7/&2{1} Achievements", category.count(uuid), size));

			meta.setLore(lore);

			item.setItemMeta(meta);

			this.addItem(item);
		}
	}

}