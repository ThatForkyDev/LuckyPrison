package com.ulfric.cash.currency;

import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.ulfric.cash.currency.dollar.DollarModule;
import com.ulfric.cash.currency.token.TokenModule;
import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.inventory.InventoryUtils;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUseSignEvent;

public class CurrencyModule extends SimpleModule {

	public CurrencyModule()
	{
		super("currency", "Front-end currency interaction", "Packet", "1.0.0-REL");

		this.withSubModule(new PriceModule());
		this.withSubModule(new DollarModule());
		this.withSubModule(new TokenModule());
		this.withSubModule(new VoucherModule());
		this.withSubModule(new EcoSignModule());

		this.addCommand("setbal", new CommandSetbal());
		this.addCommand("pay", new CommandPay());
	}

	private class EcoSignModule extends SimpleModule
	{
		public EcoSignModule()
		{
			super("ecosign", "Economy sign (buy/sell) module", "Packet", "1.0.0-REL");

			this.addListener(new Listener()
			{
				@EventHandler(ignoreCancelled = true)
				public void onSignPlace(SignChangeEvent event)
				{
					if (event.getPlayer().hasPermission("lib.signs")) return;

					String line = ChatColor.stripColor(event.getLine(0).trim()).toLowerCase();

					if (line.isEmpty()) return;

					if (!line.equals("[buy]") && !line.equals("[sell]")) return;

					event.setLine(0, "Mmm that's good!");
				}

				@EventHandler(ignoreCancelled = true)
				public void onSignUse(PlayerUseSignEvent event)
				{
					String[] lines = event.getSign().getLines();
					String line = ChatColor.stripColor(lines[0].trim().toLowerCase());

					boolean buy;

					if (line.equals("[buy]"))
					{
						buy = true;
					}
					else if (line.equals("[sell]"))
					{
						buy = false;
					}
					else return;

					Player player = event.getPlayer();

					ItemPair pair = MaterialUtils.pair(lines[2]);

					if (pair == null)
					{
						player.sendMessage(ChatColor.RED + "Invalid item: " + lines[2]);

						return;
					}

					String sAmt = lines[1].trim();

					ItemStack item = pair.toItem(sAmt.isEmpty() ? 1 : Integer.parseInt(sAmt));

					Price price = Price.of(lines[3]);

					Inventory inventory = player.getInventory();

					if (buy)
					{
						if (!InventoryUtils.hasRoomFor(inventory, item, false))
						{
							Locale.sendError(player, "cash.buy_no_room");

							return;
						}

						if (!price.take(player.getUniqueId(), Strings.format("Purchase {0}x {1}", item.getAmount(), lines[2])))
						{
							Locale.sendError(player, "cash.buy_cannot_afford");

							return;
						}

						Locale.sendSuccess(player, "cash.buy", sAmt, lines[2], price.toString());

						inventory.addItem(item);

						return;
					}

					if (!inventory.containsAtLeast(item, 1))
					{
						Locale.sendError(player, "cash.sell_not_enough");

						return;
					}

					inventory.removeItem(item);

					price.give(player.getUniqueId(), Strings.format("Sell {0} x {1}", item.getAmount(), pair.toString()));

					Locale.sendSuccess(player, "cash.sell", sAmt, lines[2], price.toString());
				}
			});
		}
	}

	private class VoucherModule extends SimpleModule
	{
		public VoucherModule()
		{
			super("voucher", "Currency voucher module", "Packet", "1.0.0-REL");

			this.addListener(new Listener()
			{
				@EventHandler
				public void onVoucherUse(PlayerInteractEvent event)
				{
					Action action = event.getAction();

					if (!action.equals(Action.RIGHT_CLICK_AIR) && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;

					ItemStack item = event.getItem();

					if (!ItemUtils.is(item, Material.PAPER)) return;

					if (!ItemUtils.hasNameAndLore(item)) return;

					ItemMeta meta = item.getItemMeta();

					if (!meta.getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Voucher")) return;

					Price value = null;
					for (String lore : meta.getLore())
					{
						value = Price.of(ChatColor.stripColor(lore));

						if (value == null) continue;

						break;
					}

					if (value == null) return;

					Player player = event.getPlayer();

					ItemUtils.decrementHand(player);

					value.give(player.getUniqueId(), "Voucher");

					Locale.send(player, "cash.voucher", value.toString());

					Firework firework = EntityUtils.spawnEntity(EntityType.FIREWORK, player.getEyeLocation());

					FireworkMeta fireworkMeta = firework.getFireworkMeta();

					fireworkMeta.addEffect(FireworkEffect.builder().with(Type.STAR).flicker(RandomUtils.nextBoolean()).trail(RandomUtils.nextBoolean()).withColor(RandomUtils.randomColor()).withFade(RandomUtils.randomColor()).build());

					fireworkMeta.setPower(1);

					firework.setFireworkMeta(fireworkMeta);
				}
			});
		}
	}

}