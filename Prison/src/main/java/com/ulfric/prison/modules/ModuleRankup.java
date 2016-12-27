package com.ulfric.prison.modules;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.block.BlockPattern;
import com.ulfric.lib.api.block.BlockUtils;
import com.ulfric.lib.api.command.Cooldown;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.hook.EconHook.Price;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.hook.PermissionsExHook.User;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.prison.events.PlayerRankupEvent;

public class ModuleRankup extends SimpleModule {

	private static final ModuleRankup INSTANCE = new ModuleRankup();
	public static ModuleRankup get() { return ModuleRankup.INSTANCE; }

	private ModuleRankup()
	{
		super("rankup", "Rankup schema module", "Packet", "1.0.0-REL");

		this.withConf();

		this.addCommand("rankup", new CommandRankup());

		this.addListener(new Listener()
		{
			@EventHandler
			public void onRankup(PlayerRankupEvent event)
			{
				Player player = event.getPlayer();

				switch (event.getNewGroup().getName().toLowerCase())
				{
					case "b":
						Locale.sendSuccess(player, "prison.rankup_re_first");
						break;

					case "d":
						Locale.sendSuccess(player, "prison.rankup_re_plot");
						break;

					case "e":
						Locale.sendSuccess(player, "prison.rankup_re_ench");
						break;

					case "f":
						Locale.sendSuccess(player, "prison.rankup_re_pick");
						break;

					case "g":
						// TODO gangs
						break;

					case "j":
						// TODO give the player a coupon 5% off or something
						break;

					case "z":
						Locale.sendSuccess(player, "prison.rankup_re_z");
						break;

					default:
						break;
				}
			}
		});
	}

	private Map<Group, Price> ranks;
	public Price getPrice(Group rank)
	{
		if (!this.isModuleEnabled()) return null;

		return this.ranks.get(rank);
	}
	public Set<Entry<Group, Price>> getPrices()
	{
		if (!this.isModuleEnabled()) return null;

		return this.ranks.entrySet();
	}

	@Override
	public void postEnable()
	{
		this.ranks = Maps.newTreeMap();

		FileConfiguration conf = this.getConf().getConf();

		for (String key : conf.getKeys(false))
		{
			Group group = Hooks.PERMISSIONS.group(key);

			if (group == null)
			{
				Bukkit.getLogger().warning(Strings.format("BAD GROUP: '{0}'", key));

				continue;
			}

			Price price = Hooks.ECON.price(conf.getString(key));

			if (price == null)
			{
				Bukkit.getLogger().warning(Strings.format("BAD PRICE: '{0}'", conf.getString(key)));

				continue;
			}

			this.ranks.put(group, price);
		}
	}

	private class CommandRankup extends SimpleCommand
	{
		private CommandRankup()
		{
			this.withEnforcePlayer();

			this.withCooldown(Cooldown.builder().withName("rankup").withDefaultDelay(Milliseconds.SECOND));
		}

		private Set<Player> players = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			if (!this.players.add(player))
			{
				Locale.sendError(player, "prison.rank_running");

				return;
			}

			User user = Hooks.PERMISSIONS.user(player);

			if (user == null)
			{
				this.players.remove(player);

				return;
			}

			Group old = user.getRankLadderGroup("mines");

			if (old == null)
			{
				this.players.remove(player);

				return;
			}

			Group target = old.getNext();

			if (target == null)
			{
				Locale.sendError(player, "prison.rank_max");

				this.players.remove(player);

				return;
			}

			String rankName = WordUtils.capitalizeFully(target.getName().replace('_', ' '));

			Price price = ModuleRankup.this.getPrice(target);

			if (price == null)
			{
				Locale.sendError(player, "prison.rank_max");

				this.players.remove(player);

				return;
			}

			UUID uuid = player.getUniqueId();

			if (!price.take(uuid, "Purchase of Rank " + rankName))
			{
				Locale.sendError(player, "prison.rank_cannot_afford", price.isToken() ? StringUtils.formatNumber(price.getRemaining(uuid)) + "T" : StringUtils.formatMoneyFully(price.getRemaining(uuid).doubleValue()), rankName);

				this.players.remove(player);

				return;
			}

			Tasks.runAsync(() ->
			{
				if (!user.rankup("mines"))
				{
					this.players.remove(player);

					return;
				}

				Locale.sendSuccess(player, "prison.rank_rankup", rankName);

				Tasks.run(() ->
				{
					Firework firework = EntityUtils.spawnEntity(EntityType.FIREWORK, player.getEyeLocation());

					FireworkMeta meta = firework.getFireworkMeta();

					Type type = null;
					switch (RandomUtils.nextInt(3))
					{
						case 1:
							type = Type.BURST;
							break;

						case 2:
							type = Type.STAR;
							break;

						default:
							type = Type.BALL;
							break;
					}

					meta.addEffect(FireworkEffect.builder().with(type).flicker(RandomUtils.nextBoolean()).trail(RandomUtils.nextBoolean()).withColor(RandomUtils.randomColor(), RandomUtils.randomColor(), RandomUtils.randomColor()).withFade(RandomUtils.randomColor(), RandomUtils.randomColor(), RandomUtils.randomColor()).build());

					meta.setPower(1);

					firework.setFireworkMeta(meta);

					ItemPair pair = MaterialUtils.pair(Material.GOLD_BLOCK);
					Location location = player.getLocation();
					Location under = location.clone().subtract(0, 1, 0);

					player.playSound(location, Sound.LEVEL_UP, 20, 1);
					for (int x = 3, delay = 0; x < 6; x++)
					{
						for (Location vector : BlockPattern.getPattern("circles_3").getLocations(under))
						{
							Tasks.runLater(() ->
							{
								BlockUtils.playTemporaryBlock(player, vector, pair, 15);
								BlockUtils.playBlockEffect(player, location, pair.getType());
							}, delay);
						}

						Tasks.runLater(() -> player.playSound(location, Sound.NOTE_PLING, 15, 1), delay);

						delay += 17;
					}

					Events.call(new PlayerRankupEvent(player, old, target));

					this.players.remove(player);
				});
			});
		}
	}

}