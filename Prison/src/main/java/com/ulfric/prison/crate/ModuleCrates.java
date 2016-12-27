package com.ulfric.prison.crate;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.block.BlockUtils;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.module.SimpleModule;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ModuleCrates extends SimpleModule {

	public ModuleCrates()
	{
		super("crates", "Crates and keys module", "Packet", "1.0.0-REL");

		//this.withSubModule(ModuleCrateitems.INSTANCE);

		this.withConf();

		this.addCommand("keys", new CommandKeys());
		this.addCommand("givekey", new CommandGivekey());
		this.addCommand("cratecast", new CommandCratecast());
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@EventHandler
			public void onInteract(PlayerInteractEvent event)
			{
				if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

				Block block = event.getClickedBlock();

				if (BlockUtils.isEmpty(block)) return;

				if (!block.getType().equals(Material.CHEST)) return;

				for (Crate crate : ModuleCrates.this.crates)
				{
					if (!crate.contains(block)) continue;

					crate.open(event.getPlayer());

					event.setCancelled(true);

					event.setUseInteractedBlock(Result.DENY);

					return;
				}
			}
		});
	}

	@Override
	public void postEnable()
	{
		if (this.crates == null)
		{
			this.crates = Lists.newArrayList();
		}
		else
		{
			this.crates.forEach(Crate::annihilate);

			this.crates.clear();
		}

		FileConfiguration conf = this.getConf().getConf();

		Set<String> keys = conf.getKeys(false);

		for (String key : keys)
		{
			ConfigurationSection sec = conf.getConfigurationSection(key);

			Crate.Builder builder = Crate.builder();

			builder.setName(sec.getString("name"));
			builder.setPackageId(sec.getInt("package"));

			sec.getStringList("locations").stream().map(LocationUtils::proxyFromString).filter(Objects::nonNull).forEach(builder::addLocation);

			sec = sec.getConfigurationSection("rewards");

			sec.getKeys(false).stream().map(sec::getConfigurationSection).forEach(secTwo -> builder.addReward(secTwo.getString("name"), secTwo.getStringList("commands"), ItemUtils.fromString(secTwo.getString("item")), secTwo.getInt("weight")));

			this.crates.add(builder.build());
		}
	}

	private List<Crate> crates;
	public Crate getCrate(String name)
	{
		name = name.toLowerCase();

		for (Crate crate : this.crates)
		{
			if (!crate.getName().toLowerCase().equals(name)) continue;

			return crate;
		}

		return null;
	}

	private final ArgStrategy<Crate> strat = new ArgStrategy<Crate>()
	{
		@Override
		public Crate match(String string)
		{
			return ModuleCrates.this.getCrate(string);
		}
	};

	private class CommandKeys extends SimpleCommand
	{
		public CommandKeys()
		{
			this.withArgument("player", ArgStrategy.OFFLINE_PLAYER, this::getPlayer);
		}

		@Override
		public void run()
		{
			StringBuilder builder = new StringBuilder();
			builder.append(Locale.getMessage(this.getSender(), "prison.crate_keys"));

			OfflinePlayer target = (OfflinePlayer) this.getObject("player");
			if (target == null)
			{
				Locale.sendError(this.getSender(), "system.specify_player");
				return;
			}

			UUID uuid = target.getUniqueId();
			for (Crate crate : ModuleCrates.this.crates)
			{
				String name = crate.getName();

				builder.append(Strings.formatF("<n>&3- &b{0} Key x {1}", name, Hooks.DATA.getPlayerDataAsInt(uuid, "prison.crates." + name)));
			}

			this.getSender().sendMessage(builder.toString());
		}
	}

	private class CommandGivekey extends SimpleCommand
	{
		public CommandGivekey()
		{
			this.withArgument("player", ArgStrategy.OFFLINE_PLAYER, this::getPlayer);
			this.withArgument("crate", ModuleCrates.this.strat, "prison.crate_not_found");
			this.withArgument("amt", ArgStrategy.INTEGER, 1);
		}

		@Override
		public void run()
		{
			OfflinePlayer target = (OfflinePlayer) this.getObject("player");
			if (target == null)
			{
				Locale.sendError(this.getSender(), "system.specify_player");
				return;
			}

			Crate crate = (Crate) this.getObject("crate");
			int amount = (int) this.getObject("amt");

			String name = crate.getName();

			UUID uuid = target.getUniqueId();

			Hooks.DATA.setPlayerData(uuid, "prison.crates." + name, Hooks.DATA.getPlayerDataAsInt(uuid, "prison.crates." + name) + amount);
		}
	}

	private class CommandCratecast extends SimpleCommand
	{
		public CommandCratecast()
		{
			this.withIndexUnusedArgs();

			this.withArgument(Argument.builder().withPath("start").withStrategy(ArgStrategy.STRING).withUsage("prison.cratecast_blank").withRemovalExclusion());
		}

		@Override
		public void run()
		{
			Locale.sendMass("prison.cratecast", Strings.formatF(this.getUnusedArgs()));
		}
	}
}
