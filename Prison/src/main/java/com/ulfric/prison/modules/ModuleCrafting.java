package com.ulfric.prison.modules;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.command.arg.ExactArg;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleCrafting extends SimpleModule {

	public ModuleCrafting()
	{
		super("crafting", "Addition and removal of crafting recipes", "Packet", "1.0.0-REL");

		this.withConf();

		this.addListener(new Listener()
		{
			@EventHandler
			public void onCraft(PrepareItemCraftEvent event)
			{
				ItemStack item = event.getRecipe().getResult();

				if (!ModuleCrafting.this.disabled.contains(MaterialUtils.pair(item))) return;

				event.getInventory().setResult(ItemUtils.blank());

				for (HumanEntity player : event.getViewers())
				{
					if (!(player instanceof Player)) continue;

					Locale.send((Player) player, "prison.chest_craft");
				}
			}
		});

		this.addCommand("enablecrafting", new EnableCrafting());
		this.addCommand("disablecrafting", new DisableCrafting());
	}

	private abstract class CraftingToggleCommand extends SimpleCommand
	{
		private CraftingToggleCommand()
		{
			this.withArgument("item", ArgStrategy.MATERIAL, "prison.material_needed");

			this.withArgument("temp", new ExactArg("--temp"));
		}
	}

	private class EnableCrafting extends CraftingToggleCommand
	{
		@Override
		public void run()
		{
			ModuleCrafting.this.enableCrafting((ItemPair) this.getObject("item"), this.hasObject("temp"));
		}
	}

	private class DisableCrafting extends CraftingToggleCommand
	{
		@Override
		public void run()
		{
			ModuleCrafting.this.disableCrafting((ItemPair) this.getObject("item"), this.hasObject("temp"));
		}
	}

	private Set<ItemPair> disabled;
	public void disableCrafting(ItemPair pair, boolean temp)
	{
		if (!this.disabled.add(pair) || temp) return;

		this.setConfObject("disabled",
							this.disabled.stream()
										 .map(ItemPair::toString)
										 .collect(Collectors.toList()));
	}
	public void enableCrafting(ItemPair pair, boolean temp)
	{
		if (!this.disabled.remove(pair) || temp) return;

		this.setConfObject("disabled",
							this.disabled.stream()
										 .map(ItemPair::toString)
										 .collect(Collectors.toList()));
	}

	@Override
	public void postEnable()
	{
		this.disabled = this.getConf()
						.getValueAsStringList("disabled")
						.stream().map(MaterialUtils::pair)
						.collect(Collectors.toSet());
	}

}