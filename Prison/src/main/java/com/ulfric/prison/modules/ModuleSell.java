package com.ulfric.prison.modules;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.EconHook.Price;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.hook.PermissionsExHook.User;
import com.ulfric.lib.api.inventory.InventoryUtils;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUseSignEvent;
import com.ulfric.lib.api.player.PlayerUseSignEvent.ClickType;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.prison.commands.CommandSellhand;
import com.ulfric.prison.entity.Minebuddy;
import com.ulfric.prison.lang.Meta;

public final class ModuleSell extends SimpleModule {

	private static final ModuleSell INSTANCE = new ModuleSell();

	public static final ModuleSell get()
	{
		return ModuleSell.INSTANCE;
	}

	private ModuleSell()
	{
		super("sell", "Sell economy module", "Packet, StaticShadow", "1.0.0-REL");

		this.withSubModule(this.mult = new ModuleSellmultiplier());

		this.withConf();

		this.addListener(new Listener()
		{
			@EventHandler(ignoreCancelled = true)
			public void onSignUse(PlayerUseSignEvent event)
			{
				if (!event.getType().equals(ClickType.RIGHT)) return;

				if (!ChatColor.stripColor(event.getSign().getLine(0)).toLowerCase().equals("[sellall]")) return;

				ModuleSell.this.sellall(event.getPlayer());
			}
		});

		this.addCommand("sellhand", new CommandSellhand());
	}

	private Map<Group, Map<ItemPair, Price>> prices;

	public Price getItemValue(Group group, ItemPair item)
	{
		if (!this.prices.containsKey(group)) return null;

		Map<ItemPair, Price> prices = this.prices.get(group);

		for (Entry<ItemPair, Price> entry : prices.entrySet())
		{
			if (!entry.getKey().equals(item)) continue;

			return entry.getValue();
		}

		return null;
	}

	@Override
	public void postEnable()
	{
		this.prices = Maps.newHashMap();

		FileConfiguration conf = this.getConf().getConf();

		for (String key : conf.getKeys(false))
		{
			List<String> items = conf.getStringList(key);
			Map<ItemPair, Price> prices = Maps.newHashMapWithExpectedSize(items.size());

			for (String item : items)
			{
				prices.put(MaterialUtils.pair(StringUtils.findOption(item, "id")), Hooks.ECON.price(StringUtils.findOption(item, "val")));
			}

			this.prices.put(Hooks.PERMISSIONS.group(key), prices);
		}
	}

	public void sellitem(Player player, ItemStack stack)
	{
		if (!ModuleSell.INSTANCE.isModuleEnabled()) return;

		Inventory inventory = player.getInventory();
		Optional<Minebuddy> buddy = Optional.ofNullable(player.hasMetadata(Meta.MINEBUDDY) ? Metadata.getValue(player, Meta.MINEBUDDY) : null);

		if (ItemUtils.isEmpty(stack))
		{
			Locale.sendError(player, "system.inventory_empty"); // TODO Cannot sell air

			return;
		}

		if (buddy.isPresent())
		{
			Minebuddy mbo = buddy.get();

			if (!mbo.canSell())
			{
				Locale.sendError(player, "prison.minebuddy_sell_cooldown", TimeUtils.millisecondsToString(mbo.getStarted().timeTill()));

				return;
			}
		}

		User user = Hooks.PERMISSIONS.user(player);
		Group group = user.getRankLadderGroup("mines");

		int tokenTotal = 0;
		int tokenItems = 0;

		double moneyTotal = 0;
		int moneyItems = 0;

		Price price = ModuleSell.this.getItemValue(group, MaterialUtils.pair(stack));

		if (price == null)
		{
			Locale.sendError(player, "prison.sell_cannot");
			return;
		}

		int amount = stack.getAmount();

		if (price.isToken())
		{
			tokenItems += amount;

			tokenTotal += (price.getAmount().intValue() * amount);
		}
		else
		{
			moneyItems += amount;

			moneyTotal += (price.getAmount().doubleValue() * amount);
		}

		inventory.remove(stack);


		if (moneyTotal <= 0 && tokenTotal <= 0)
		{
			Locale.sendError(player, "prison.sellall_none");

			return;
		}

		double bonus = Hooks.DATA.getPlayerDataAsDouble(player.getUniqueId(), Meta.SELL_BONUS);

		double totalBonus = bonus;

		Group donor = user.getRankLadderGroup("premium");

		if (donor != null)
		{
			totalBonus += ModuleSell.this.getMultiplier(donor);
		}

		if (totalBonus < 1 && totalBonus > 0) totalBonus += 1;
		if (totalBonus <= 0) totalBonus = 1;

		moneyTotal *= totalBonus;
		tokenTotal *= totalBonus;

		if (moneyTotal > 0)
		{
			double mtotal = moneyTotal;
			buddy.ifPresent(mb -> mb.addCashTotal(mtotal));

			Hooks.ECON.giveMoney(player.getUniqueId(), moneyTotal, "Shop Sell-Hand");

			Locale.sendSuccess(player, "prison.sellall_money", StringUtils.formatMoneyFully(moneyTotal), StringUtils.formatNumber(moneyItems));
		}

		if (tokenTotal > 0)
		{
			int ttotal = tokenTotal;
			buddy.ifPresent(mb -> mb.addTokenTotal(ttotal));

			Hooks.ECON.giveTokens(player.getUniqueId(), tokenTotal);

			Locale.sendSuccess(player, "prison.sellall_tokens", StringUtils.formatNumber(tokenTotal), StringUtils.formatNumber(tokenItems));
		}

		if (bonus == 0)
		{
			Locale.send(player, "prison.sellall_vote");

			return;
		}

		Locale.send(player, "prison.bonus_current_alt", StringUtils.formatDecimal(bonus));
	}

	public void sellall(Player player)
	{
		if (!ModuleSell.INSTANCE.isModuleEnabled()) return;

		Inventory inventory = player.getInventory();
		Optional<Minebuddy> buddy = Optional.ofNullable(player.hasMetadata(Meta.MINEBUDDY) ? Metadata.getValue(player, Meta.MINEBUDDY) : null);

		if (InventoryUtils.isEmpty(inventory))
		{
			Locale.sendError(player, "system.inventory_empty");

			return;
		}

		if (buddy.isPresent())
		{
			Minebuddy mbo = buddy.get();
			if (!mbo.canSell())
			{
				Locale.sendError(player, "prison.minebuddy_sell_cooldown", TimeUtils.millisecondsToString(mbo.getStarted().timeTill()));

				return;
			}
		}

		UUID uuid = player.getUniqueId();

		User user = Hooks.PERMISSIONS.user(uuid);
		Group group = user.getRankLadderGroup("mines");

		int tokenTotal = 0;
		int tokenItems = 0;

		double moneyTotal = 0;
		int moneyItems = 0;

		int times = 0;
		do
		{
			for (ItemStack cycle : inventory.getContents())
			{
				if (ItemUtils.isEmpty(cycle)) continue;

				Price price = ModuleSell.this.getItemValue(group, MaterialUtils.pair(cycle));

				if (price == null) continue;

				int amount = cycle.getAmount();

				if (price.isToken())
				{
					tokenItems += amount;

					tokenTotal += (price.getAmount().intValue() * amount);
				} else
				{
					moneyItems += amount;

					moneyTotal += (price.getAmount().doubleValue() * amount);
				}

				inventory.remove(cycle);
			}

			if (++times > 1) break;

			if (!player.hasPermission(com.ulfric.prison.lang.Permissions.SELL_FROM_ECHEST)) break;

			inventory = player.getEnderChest();
		} while (true);

		if (moneyTotal <= 0 && tokenTotal <= 0)
		{
			Locale.sendError(player, "prison.sellall_none");

			return;
		}

		double bonus = this.getMultiplier(user, uuid);

		moneyTotal *= bonus;
		tokenTotal *= bonus;

		if (moneyTotal > 0)
		{
			double mtotal = moneyTotal;
			buddy.ifPresent(mb -> mb.addCashTotal(mtotal));

			Hooks.ECON.giveMoney(uuid, moneyTotal, "Shop Sell-All");

			Locale.sendSuccess(player, "prison.sellall_money", StringUtils.formatMoneyFully(moneyTotal), StringUtils.formatNumber(moneyItems));
		}

		if (tokenTotal > 0)
		{
			int ttotal = tokenTotal;
			buddy.ifPresent(mb -> mb.addTokenTotal(ttotal));

			Hooks.ECON.giveTokens(uuid, tokenTotal);

			Locale.sendSuccess(player, "prison.sellall_tokens", StringUtils.formatNumber(tokenTotal), StringUtils.formatNumber(tokenItems));
		}

		if (bonus == 0)
		{
			Locale.send(player, "prison.sellall_vote");

			return;
		}

		Locale.send(player, "prison.bonus_current_alt", StringUtils.formatDecimal(bonus));
	}

	private ModuleSellmultiplier mult;

	public Double getMultiplier(Group group)
	{
		return this.mult.getMult(group);
	}

	public Double getMultiplier(User user, UUID uuid)
	{
		return this.mult.getMult(user, uuid);
	}

	private class ModuleSellmultiplier extends SimpleModule
	{
		public ModuleSellmultiplier()
		{
			super("sellmultiplier", "Sell multipliers for premium users", "Packet", "1.0.0-REL");

			this.withConf();
		}

		private Map<Group, Double> rankMultipliers;

		public Double getMult(Group group)
		{
			if (this.rankMultipliers == null) return 0D;

			return this.rankMultipliers.get(group);
		}

		public Double getMult(User user, UUID uuid)
		{
			double bonus = Hooks.DATA.getPlayerDataAsDouble(uuid, Meta.SELL_BONUS);

			Group donor = user.getRankLadderGroup("premium");

			if (donor != null)
			{
				bonus += this.getMult(donor);
			}

			if (bonus < 1 && bonus > 0) bonus += 1;
			if (bonus <= 0) bonus = 1;

			return bonus;
		}

		@Override
		public void postEnable()
		{
			this.rankMultipliers = Maps.newHashMap();

			FileConfiguration conf = this.getConf().getConf();

			for (String key : conf.getKeys(false))
			{
				this.rankMultipliers.put(Hooks.PERMISSIONS.group(key), conf.getDouble(key));
			}
		}

		@Override
		public void runTest(Object data)
		{
			Player player = (Player) data;
			Tasks.runAsync(() ->
			{
				Set<Entry<UUID, Object>> large = Sets.newHashSet();

				Map<UUID, Object> objects = Hooks.DATA.getAllData(Meta.SELL_BONUS);

				for (Entry<UUID, Object> entry : objects.entrySet())
				{
					Object obj = entry.getValue();

					if (!(obj instanceof Number)) continue;

					if (((Number) obj).intValue() == 0) continue;

					large.add(entry);
				}

				for (Entry<UUID, Object> entry : large)
				{
					player.sendMessage(Strings.formatF(" - {0} : {1}", Bukkit.getOfflinePlayer(entry.getKey()).getName(), entry.getValue()));
				}
			});
		}
	}

}