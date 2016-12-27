package com.ulfric.prison.modules;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.hook.EconHook.Price;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.hook.PermissionsExHook.User;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.module.ModuleTask;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.prison.lang.Meta;

public class ModuleCandy extends SimpleModule {

	public ModuleCandy()
	{
		super("candy", "Consumable candies module", "Packet", "1.0.0-REL");

		this.addListener(new Listener()
		{
			@EventHandler
			public void onCraft(PrepareItemCraftEvent event)
			{
				ItemStack item = event.getRecipe().getResult();

				ItemMeta meta = item.getItemMeta();

				if (ItemUtils.is(item, Material.SUGAR))
				{
					meta.setDisplayName(ChatColor.AQUA + "Lucky Dip");

					meta.setLore((List<String>) Chat.color(Lists.newArrayList("&7Gives 4 secs", "&7of Speed II!")));
				}
				else if (ItemUtils.is(item, Material.SPECKLED_MELON))
				{
					meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Lucky Fruit");

					meta.setLore((List<String>) Chat.color(Lists.newArrayList("&7Gives 2 secs", "&7of Autosell!")));
				}
				else return;

				item.setItemMeta(meta);

				event.getInventory().setResult(item);
			}

			@EventHandler(ignoreCancelled = true)
			public void onSmelt(FurnaceSmeltEvent event)
			{
				ItemStack item = event.getResult();

				if (!ItemUtils.is(item, Material.INK_SACK)) return;

				if (item.getDurability() != 2) return;

				ItemMeta meta = item.getItemMeta();

				meta.setDisplayName(ChatColor.DARK_GREEN + "Lucky Rancher");

				meta.setLore((List<String>) Chat.color(Lists.newArrayList("&7Gives 4 secs", "&7of Haste II!")));

				item.setItemMeta(meta);

				event.setResult(item);
			}

			@EventHandler
			public void onInter(PlayerInteractEvent event)
			{
				Action action = event.getAction();

				if (!action.equals(Action.RIGHT_CLICK_AIR) && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;

				ItemStack item = event.getItem();

				if (ItemUtils.isEmpty(item)) return;

				PotionEffectType type = null;

				Player player = event.getPlayer();

				switch (item.getType())
				{
					case INK_SACK:
						if (item.getDurability() != 2) return;
						type = PotionEffectType.FAST_DIGGING;
						break;

					case SUGAR:
						type = PotionEffectType.SPEED;
						break;

					case SPECKLED_MELON:
						ModuleCandy.this.atask.addPlayer(player, (int) Ticks.fromSeconds(2));
						break;

					default:
						return;
				}

				if (type != null)
				{
					int extra = 0;

					for (PotionEffect effect : player.getActivePotionEffects())
					{
						if (!effect.getType().equals(type)) continue;

						if (effect.getAmplifier() < 1) continue;

						extra = effect.getDuration();

						break;
					}

					player.addPotionEffect(new PotionEffect(type, (int) (extra + Ticks.fromSeconds(4)), 1, true), true);
				}

				ItemUtils.decrementHand(player);
			}
		});

		this.addTask(new SellallTask());
	}

	private SellallATask atask = new SellallATask();

	private class SellallTask extends ModuleTask
	{
		public SellallTask()
		{
			super(ModuleCandy.this.atask, StartType.MANUAL);
		}
	}

	private class SellallATask extends ATask
	{
		private Map<Player, Integer> times = new ConcurrentHashMap<>();
		private void addPlayer(Player player, int ticks)
		{
			player = PlayerUtils.proxy(player);

			ticks /= 6;

			Integer val = this.times.get(player);

			if (val == null)
			{
				this.times.put(player, ticks);
			}
			else
			{
				this.times.put(player, val+ticks);
			}

			if (this.isRunning()) return;

			this.start();
		}

		@Override
		public void start()
		{
			super.start();

			this.setTaskId(Tasks.runRepeating(this, 3).getTaskId());
		}

		@Override
		public void run()
		{
			if (!ModuleSell.get().isModuleEnabled())
			{
				this.annihilate();

				return;
			}

			synchronized (this.times)
			{
				if (this.times.isEmpty())
				{
					this.annihilate();

					return;
				}

				for (Entry<Player, Integer> entry : this.times.entrySet())
				{
					Player player = entry.getKey();
					int value = entry.getValue();

					Inventory inventory = player.getInventory();

					if (inventory == null) continue;

					if (--value == 0)
					{
						this.times.remove(player);
					}
					else
					{
						this.times.replace(player, value);
					}

					int x = -1;
					for (ItemStack item : inventory)
					{
						x++;

						if (ItemUtils.isEmpty(item)) continue;

						User user = Hooks.PERMISSIONS.user(player);
						Group group = user.getRankLadderGroup("mines");

						int tokenTotal = 0;
						double moneyTotal = 0;

						Price price = ModuleSell.get().getItemValue(group, MaterialUtils.pair(item));

						if (price == null) continue;

						int amount = item.getAmount();

						if (price.isToken())
						{
							tokenTotal += (price.getAmount().intValue() * amount);
						}
						else
						{
							moneyTotal += (price.getAmount().doubleValue() * amount);
						}

						inventory.setItem(x, ItemUtils.blank());

						player.updateInventory();

						if (moneyTotal <= 0 && tokenTotal <= 0) continue;

						double bonus = Hooks.DATA.getPlayerDataAsDouble(player.getUniqueId(), Meta.SELL_BONUS);

						double totalBonus = bonus;

						Group donor = user.getRankLadderGroup("premium");

						if (donor != null)
						{
							totalBonus += ModuleSell.get().getMultiplier(donor);
						}

						if (totalBonus < 1 && totalBonus > 0) totalBonus += 1;
						if (totalBonus <= 0) totalBonus = 1;

						moneyTotal *= totalBonus;
						tokenTotal *= totalBonus;

						if (tokenTotal > 0)
						{
							Hooks.ECON.giveTokens(player.getUniqueId(), tokenTotal);
						}

						if (moneyTotal > 0)
						{
							Hooks.ECON.giveMoney(player.getUniqueId(), moneyTotal, "Buff Sell-All");
						}

						break;
					}
				}
			}
		}

		@Override
		public void annihilate()
		{
			super.annihilate();

			if (CollectUtils.isEmpty(this.times)) return;

			this.times.clear();
		}
	}

}