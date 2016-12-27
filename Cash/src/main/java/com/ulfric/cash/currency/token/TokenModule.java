package com.ulfric.cash.currency.token;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;
import com.ulfric.cash.currency.token.Tokens.ITokens;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.task.Tasks;

public class TokenModule extends SimpleModule {

	public TokenModule()
	{
		super("token", "Token currency implementation", "Packet", "1.0.0-REL");

		this.addCommand("tokens", new CommandTokens());
	}

	@Override
	public void postEnable()
	{
		Tokens.impl = new ITokens()
		{
			private final Object lock = new Object();

			@Override
			public int getBalance(UUID uuid)
			{
				synchronized (this.lock)
				{
					return Hooks.DATA.getPlayerDataAsInt(uuid, "currency.tokens");
				}
			}

			@Override
			public boolean take(UUID uuid, int amount)
			{
				int value = this.getBalance(uuid) - amount;

				if (value < 0) return false;

				this.set(uuid, value);

				return true;
			}

			@Override
			public void give(UUID uuid, int amount)
			{
				this.set(uuid, amount + this.getBalance(uuid));
			}

			@Override
			public void set(UUID uuid, int balance)
			{
				synchronized (this.lock)
				{
					Hooks.DATA.setPlayerData(uuid, "currency.tokens", balance);
				}
			}
		};
	}

	@Override
	public void postDisable()
	{
		Tokens.impl = ITokens.EMPTY;
	}

	@Override
	public void runTest(Object data)
	{
		Player player = (Player) data;

		Tasks.runAsync(() ->
		{
			Set<Entry<UUID, Object>> large = Sets.newHashSet();

			Map<UUID, Object> objects = Hooks.DATA.getAllData("currency.tokens");

			for (Entry<UUID, Object> entry : objects.entrySet())
			{
				Object obj = entry.getValue();

				if (!(obj instanceof Integer)) continue;

				if (!this.func((Integer) obj)) continue;

				large.add(entry);
			}

			for (Entry<UUID, Object> entry : large)
			{
				player.sendMessage(Strings.formatF(" - {0} : {1}", Bukkit.getOfflinePlayer(entry.getKey()).getName(), entry.getValue()));
			}
		});
	}

	private boolean func(Integer intVal)
	{
		return intVal.intValue() > 200;
	}

}