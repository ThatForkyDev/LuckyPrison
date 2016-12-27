package com.ulfric.prison.modules;

import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;
import com.ulfric.lib.api.hook.EconHook.Price;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUseSignEvent;

public class ModuleRepair extends SimpleModule {

	@Override
	public void runTest(Object object)
	{
		CommandSender sender = object instanceof Player ? (Player) object : Bukkit.getConsoleSender();

		for (Material material : Material.values())
		{
			if (material.getMaxDurability() == 0) continue;

			sender.sendMessage(material.name());
		}
	}

	private static final Set<Material> REPAIRABLE;

	static
	{
		Set<Material> set = Sets.newHashSet();

		for (Material material : Material.values())
		{
			if (material.getMaxDurability() == 0) continue;

			set.add(material);
		}

		// This cannot be qualified with "ModuleRepair." - fuck Java.
		REPAIRABLE = Sets.immutableEnumSet(set);
	}

	public ModuleRepair()
	{
		super("repair", "Repair items with signs", "evilmidget38", "1.0.0-REL");

		this.withConf();
	}

	private Price price;

	@Override
	public void postEnable()
	{
		FileConfiguration conf = this.getConf().getConf();

		this.price = Hooks.ECON.priceTokens(conf.getInt("price", 6));
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@EventHandler
			public void onSignUse(PlayerUseSignEvent signEvent)
			{
				String line0 = signEvent.getSign().getLine(0);

				if (StringUtils.isEmpty(line0)) return;

				if (!ChatColor.stripColor(line0.toLowerCase()).equals("[repair]")) return;

				Player player = signEvent.getPlayer();

				ItemStack item = player.getItemInHand();

				if (!ModuleRepair.this.canRepair(item)) return;

				UUID uuid = player.getUniqueId();

				if (!ModuleRepair.this.price.take(uuid, ""))
				{
					Locale.sendError(player, "prison.repair_too_poor", ModuleRepair.this.price.getRemaining(uuid));

					return;
				}

				item.setDurability((short) 0);

				Locale.sendSuccess(player, "prison.repair_success");
			}
		});
	}

	private boolean canRepair(ItemStack item)
	{
		if (ItemUtils.isEmpty(item)) return false;

		if (item.getDurability() == 0) return false;

		return ModuleRepair.REPAIRABLE.contains(item.getType());
	}

}