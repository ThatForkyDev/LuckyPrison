package com.ulfric.ess.modules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.ess.entity.Kit;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.persist.ConfigFile;
import com.ulfric.lib.api.player.PlayerFirstJoinEvent;

public class ModuleKits extends SimpleModule {

	private static final ModuleKits INSTANCE = new ModuleKits();
	public static ModuleKits get() { return ModuleKits.INSTANCE; }

	public ModuleKits()
	{
		super("kits", "Kits module", "Packet", "1.0.0-REL");

		this.withConf();
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@EventHandler(priority = EventPriority.LOWEST)
			public void onJoin(PlayerFirstJoinEvent event)
			{
				Player player = event.getPlayer();

				ItemStack[] items = ModuleKits.this.getKit("default").getContents().clone();

				for (ItemStack item : items)
				{
					if (item == null) continue;

					if (!item.getType().equals(Material.WRITTEN_BOOK)) continue;

					ItemUtils.replacePagePlaceholders(item, player);
				}

				player.getInventory().setContents(items);
			}
		});
	}

	@Override
	public void postEnable()
	{
		this.kits = Maps.newHashMap();

		ConfigFile conf = this.getConf();

		for (String string : conf.getKeys(false))
		{
			ConfigurationSection section = conf.getSection(string);

			List<String> lines = section.getStringList("items");
			int max = 36;
			ItemStack[] items = new ItemStack[max];

			List<ItemStack> overflow = Lists.newArrayList();

			int x = 0;
			for (String itemString : lines)
			{
				ItemStack item = ItemUtils.fromString(itemString);

				if (item.getAmount() > item.getMaxStackSize()) {
					overflow.add(item);

					continue;
				}

				items[Integer.parseInt(StringUtils.findOption(itemString, "slot", String.valueOf(x)))] = item;

				x++;
			}

			for (ItemStack item : overflow) {
				while (item.getAmount() > 0) {
					if ((max - 1) == items.length) {
						max += 10;
						items = Arrays.copyOf(items, max);
					}
					if (item.getAmount() > item.getMaxStackSize()) {
						ItemStack less = item.clone();
						less.setAmount(less.getMaxStackSize());
						item.setAmount(item.getAmount() - item.getMaxStackSize());
						items[x] = less;
					} else {
						items[x] = item;
						x++;
						break;
					}
					x++;
				}
			}

			this.kits.put(string.toLowerCase(), new Kit(string, items, section.getLong("cooldown")));
		}
	}

	@Override
	public void postDisable()
	{
		this.kits.clear();
	}

	private Map<String, Kit> kits;
	public void registerKit(String name, Kit kit)
	{
		if (!this.isModuleEnabled()) return;

		this.kits.put(name.toLowerCase(), kit);
	}
	public Map<String, Kit> getKits()
	{
		if (!this.isModuleEnabled()) return null;

		return this.kits;
	}
	public Kit getKit(String name)
	{
		if (!this.isModuleEnabled()) return null;

		return this.kits.get(name.toLowerCase());
	}

}