package com.ulfric.prison.crate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.gui.Panel;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Annihilate;
import com.ulfric.lib.api.java.Container;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.java.Weighted;
import com.ulfric.lib.api.location.LocationProxy;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.prison.crate.Crate.Reward;

public class Crate implements Named, Container<Block>, Iterable<Reward>, Annihilate {

	private Crate(String name, int noKeys, List<Reward> rewards, List<LocationProxy> locations)
	{
		this.name = name;
		this.rewards = rewards;
		this.locations = locations;
		this.totalWeight = rewards.stream().mapToInt(Reward::getWeight).sum();
		this.noKeys = noKeys;

		this.totalSize = rewards.size();
		this.invSize = (int) (Numbers.roundUp(rewards.size(), 9) + 18);

		this.showOff.start();
	}

	private final String name;
	private final List<Reward> rewards;
	private final List<LocationProxy> locations;
	private final int totalWeight;
	private final int totalSize;
	private final int invSize;
	private final int noKeys;

	private static final int DELAY = (int) Ticks.fromSeconds(2.2);

	private final ATask showOff = new ATask()
	{
		@Override
		public void start()
		{
			super.start();

			this.setTaskId(Tasks.runRepeating(this, 3).getTaskId());
		}

		@Override
		public void run()
		{
			Reward reward = RandomUtils.getWeighted(Crate.this.rewards, Crate.this.totalWeight);

			// We construct a new item rather than cloning the old one,
			// because all we really need is the type and durability
			ItemStack bigItem = reward.getItem();
			ItemStack item = new ItemStack(bigItem.getType(), 1, bigItem.getDurability());
			String name = bigItem.getItemMeta().getDisplayName();

			for (LocationProxy location : Crate.this.locations)
			{
				Item drop = location.dropItemNaturally(item, 0.5, 0.25, 0.5);

				if (drop == null) return;

				drop.setPickupDelay(Integer.MAX_VALUE);

				drop.setCustomName(name);
				drop.setCustomNameVisible(true);

				Metadata.apply(drop, "_ulf_nostack", RandomUtils.nextInt(1000));

				Tasks.runLater(drop::remove, Crate.DELAY);
			}
		}
	};

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public boolean contains(Block block)
	{
		for (LocationProxy proxy : this.locations)
		{
			Location pLocation = proxy.getLocation();

			if (pLocation == null) continue;

			if (!pLocation.getBlock().equals(block)) continue;

			return true;
		}

		return false;
	}

	@Override
	public Iterator<Reward> iterator()
	{
		return this.rewards.iterator();
	}

	public CratePanel open(Player player)
	{
		CratePanel panel = new CratePanel(player);

		player.updateInventory();

		return panel;
	}

	public class CratePanel extends Panel
	{
		private CratePanel(Player player)
		{
			super(Crate.this.name.toLowerCase().replace(' ', '_'), ChatColor.GOLD + Crate.this.name + " Crate", Crate.this.invSize, player, false, false);
		}

		private final int keySlot = Crate.this.invSize - 5;
		private int rewardSlot = -1;
		private Reward reward = null;

		@Override
		public void onOpen(Player player)
		{
			Tasks.run(() ->
			{
				Crate.this.rewards.stream().map(Reward::getItem).forEach(this::addItem);

				this.setItem(this.keySlot, new ItemBuilder().withType(Material.TRIPWIRE_HOOK).withName(this.getName() + " Crate").withLore("", Chat.color("&c>>> &aCLICK TO OPEN &c<<<"), "", Strings.formatF("&6Keys: &7{0}", Hooks.DATA.getPlayerDataAsInt(player.getUniqueId(), "prison.crates." + this.getIdentifier()))).build());
			});
		}

		@Override
		public void onClick(Player player, ItemStack item, int slot)
		{
			if (slot < 0) return;

			if (slot == this.rewardSlot)
			{
				if (this.reward == null) return; // Better safe than sorry

				player.closeInventory();

				return;
			}

			if (slot != this.keySlot) return;

			if (this.reward != null)
			{
				player.closeInventory();

				return;
			}

			int keys = Hooks.DATA.getPlayerDataAsInt(player.getUniqueId(), "prison.crates." + this.getIdentifier());

			if (keys == 0)
			{
				player.closeInventory();

				Commands.dispatch(player, "buy " + Crate.this.noKeys);

				return;
			}

			Events.call(new CrateOpenEvent(player, Crate.this));

			Hooks.DATA.setPlayerData(player.getUniqueId(), "prison.crates." + this.getIdentifier(), --keys);

			ItemMeta meta = item.getItemMeta();

			meta.setLore(Arrays.asList("", Chat.color("&c>>> &aCLICK TO CLAIM &c<<<")));

			item.setItemMeta(meta);

			this.setItem(this.keySlot, item);

			Reward reward = RandomUtils.getWeighted(Crate.this.rewards, Crate.this.totalWeight);

			int currentSlot = 0;

			for (Reward val : Crate.this.rewards)
			{
				if (!reward.equals(val))
				{
					currentSlot++;

					continue;
				}

				this.rewardSlot = currentSlot;

				break;
			}

			if (this.rewardSlot == -1) return;

			this.reward = reward;

			this.task = new Task();

			this.task.start();
		}

		private Task task;

		private class Task extends ATask
		{
			private Task()
			{
				List<Integer> top = Lists.newArrayList();
				List<Integer> bottom = Lists.newArrayList();

				for (int x = 0; x < CratePanel.this.rewardSlot; x++)
				{
					top.add(x);
				}

				for (int x = Crate.this.totalSize; x > CratePanel.this.rewardSlot; x--)
				{
					bottom.add(x);
				}

				this.iTop = top.iterator();
				this.iBottom = bottom.iterator();
			}

			private final Iterator<Integer> iTop;
			private final Iterator<Integer> iBottom;

			@Override
			public void start()
			{
				super.start();

				this.setTaskId(Tasks.runRepeating(this, 3).getTaskId());
			}

			@Override
			public void run()
			{
				Set<Player> players = CratePanel.this.getPlayers();
				if (players.isEmpty())
				{
					this.annihilate();

					return;
				}

				for(Player player : players)
				{
					player.playSound(player.getLocation(), Sound.ITEM_BREAK, 6, 10);
				}

				boolean flag = false;

				if (this.iTop.hasNext())
				{
					flag = true;

					CratePanel.this.setItem(this.iTop.next(), ItemUtils.blank());
				}
				if (this.iBottom.hasNext())
				{
					flag = true;

					CratePanel.this.setItem(this.iBottom.next(), ItemUtils.blank());
				}

				if (flag) return;

				for (Player player : players)
				{
					player.playSound(player.getLocation(), Sound.ARROW_HIT, 6, 7);
				}

				for (LocationProxy location : Crate.this.locations)
				{
					Firework firework = (Firework) location.spawn(EntityType.FIREWORK);

					if (firework == null) continue;

					FireworkMeta meta = firework.getFireworkMeta();

					Type type = null;
					switch (RandomUtils.nextInt(4))
					{
						case 1:
							type = Type.BURST;
							break;

						case 2:
							type = Type.STAR;
							break;

						case 3:
							type = Type.CREEPER;
							break;

						default:
							type = Type.BALL;
							break;
					}

					meta.addEffect(FireworkEffect.builder().with(type).flicker(RandomUtils.nextBoolean()).trail(RandomUtils.nextBoolean()).withColor(RandomUtils.randomColor(), RandomUtils.randomColor(), RandomUtils.randomColor()).withFade(RandomUtils.randomColor(), RandomUtils.randomColor(), RandomUtils.randomColor()).build());

					meta.setPower(1);

					firework.setFireworkMeta(meta);

					Tasks.run(firework::detonate);
				}

				this.annihilate();
			}
		}

		@Override
		public void onClose(Player player)
		{
			if (this.task != null && this.task.isRunning())
			{
				this.task.annihilate();
			}

			if (this.reward == null) return;

			this.reward.accept(player);
		}
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements com.ulfric.lib.api.java.Builder<Crate>
	{
		private Builder() { }

		@Override
		public Crate build()
		{
			Validate.notBlank(this.name);
			Validate.notEmpty(this.rewards);
			Validate.notEmpty(this.locations);

			return new Crate(this.name, this.id, this.rewards, this.locations);
		}

		private String name;
		private List<Reward> rewards = Lists.newArrayList();
		private List<LocationProxy> locations = Lists.newArrayList();
		private int id;

		public Builder setName(String name)
		{
			this.name = name;

			return this;
		}

		public Builder setPackageId(int id)
		{
			this.id = id;

			return this;
		}

		public Builder addReward(String name, List<String> commands, ItemStack item, int weight)
		{
			this.rewards.add(new Reward(name, commands, item, weight));

			return this;
		}

		public Builder addLocation(Location location)
		{
			this.locations.add(LocationUtils.proxy(location));

			return this;
		}

		public Builder addLocation(LocationProxy location)
		{
			this.locations.add(location);

			return this;
		}
	}

	public static final class Reward implements Named, Weighted, Iterable<RewardCmd>, Consumer<Player>
	{
		private Reward(String name, List<String> commands, ItemStack item, int weight)
		{
			this.name = Chat.color(name);
			this.commands = ImmutableList.copyOf(commands.stream().map(RewardCmd::new).collect(Collectors.toList()));
			this.item = item;
			this.weight = weight;
		}

		private final String name;
		private final List<RewardCmd> commands;
		private final ItemStack item;
		private final int weight;

		@Override
		public String getName()
		{
			return this.name;
		}

		private ItemStack getItem()
		{
			return this.item;
		}

		@Override
		public int getWeight()
		{
			return this.weight;
		}

		@Override
		public Iterator<RewardCmd> iterator()
		{
			return this.commands.iterator();
		}

		@Override
		public void accept(Player player)
		{
			this.forEach(cmd -> cmd.accept(player));
		}

		@Override
		public String toString()
		{
			return Strings.format("{0}:{1}", this.name, ItemUtils.toString(this.item));
		}
	}

	public static final class RewardCmd implements Consumer<Player>
	{
		private static final Map<String, BiFunction<Player, String, String>> MUTATORS;
		static
		{
			Map<String, BiFunction<Player, String, String>> mutators = Maps.newHashMap();

			mutators.put("<player>", (player, str) -> str.replace("<player>", player.getName()));
			mutators.put("<ip>", (player, str) -> str.replace("<ip>", PlayerUtils.getIP(player)));
			mutators.put("<uuid>", (player, str) -> str.replace("<uuid>", player.getUniqueId().toString()));
			mutators.put("<health>", (player, str) -> str.replace("<health>", StringUtils.formatDecimal(player.getHealth())));

			MUTATORS = ImmutableMap.copyOf(mutators);
		}

		private RewardCmd(String command)
		{
			this.command = command;

			List<BiFunction<Player, String, String>> mutators = Lists.newArrayList();

			for (Entry<String, BiFunction<Player, String, String>> entry : RewardCmd.MUTATORS.entrySet())
			{
				if (!command.contains(entry.getKey())) continue;

				mutators.add(entry.getValue());
			}

			this.mutators = ImmutableList.copyOf(mutators);
		}

		private final String command;
		private final List<BiFunction<Player, String, String>> mutators;

		@Override
		public void accept(Player player)
		{
			String command = this.command;

			for (BiFunction<Player, String, String> function : this.mutators)
			{
				command = function.apply(player, command);
			}

			Commands.dispatch(command);
		}
	}

	@Override
	public void annihilate()
	{
		if (!this.showOff.isRunning()) return;

		this.showOff.annihilate();
	}

}