package com.ulfric.prison.modules;

import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.java.Annihilate;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.Timestamp;

@Deprecated
public class ModuleMinebuddy extends SimpleModule {

	private static final ModuleMinebuddy INSTANCE = new ModuleMinebuddy();

	public static ModuleMinebuddy get()
	{
		return ModuleMinebuddy.INSTANCE;
	}

	private ModuleMinebuddy()
	{
		super("minebuddy", "Minebuddy prison mining module", "Packet", "1.0.0-DEV");
	}

	private Map<Player, Stack<Player>> requests;
	public boolean request(Player asker, Player requested)
	{
		asker = PlayerUtils.proxy(asker);
		requested = PlayerUtils.proxy(requested);

		if (this.requests == null)
		{
			this.requests = Maps.newHashMap();

			Stack<Player> stack = new Stack<>();

			stack.push(requested);

			this.requests.put(asker, stack);

			return true;
		}

		Stack<Player> stack = this.requests.get(requested);

		if (stack == null)
		{
			stack = new Stack<>();

			stack.push(requested);

			this.requests.put(requested, stack);

			return true;
		}

		if (stack.contains(requested)) return false;

		stack.push(requested);

		return true;
	}
	private Player getLastRequest(Player accepter)
	{
		Stack<Player> requests = this.requests.get(accepter);
	
		if (CollectUtils.isEmpty(requests)) return null;

		return requests.pop();
	}
	public boolean removeRequest(Player accepter, Player asker)
	{
		Stack<Player> requests = this.requests.get(accepter);
		
		if (CollectUtils.isEmpty(requests)) return false;

		return requests.remove(asker);
	}

	private Map<UUID, Minebuddy> buddies;
	public Minebuddy getMinebuddy(Player player)
	{
		if (CollectUtils.isEmpty(this.buddies)) return null;

		return this.buddies.get(player.getUniqueId());
	}
	public boolean accept(Player accepter, Player asker)
	{
		if (CollectUtils.isEmpty(this.requests)) return false;

		accepter = PlayerUtils.proxy(accepter);

		if (asker == null)
		{
			asker = this.getLastRequest(accepter);

			if (asker == null)
			{
				return false;
			}
		}
		else
		{
			asker = PlayerUtils.proxy(asker);

			if (!this.removeRequest(accepter, asker))
			{
				return false;
			}
		}

		if (this.buddies == null)
		{
			this.buddies = Maps.newHashMap();
		}

		Minebuddy buddy = new Minebuddy(asker, asker, 0);

		this.buddies.put(asker.getUniqueId(), buddy);
		this.buddies.put(accepter.getUniqueId(), buddy);

		return true;
	}

	public class Minebuddy implements Annihilate
	{
		private Minebuddy(Player asker, Player accepter, int split)
		{
			this.asker = asker;
			this.accepter = accepter;

			this.sellDelay = asker.hasPermission("prison.minebuddy.shortsell") || accepter.hasPermission("prison.minebuddy.shortsell") ? 1.1 : 1.6;

			this.started = Timestamp.now();

			this.split = split;
		}

		private final Player asker;
		private final Player accepter;

		private final Timestamp started;
		public long timeSinceStart()
		{
			return this.started.timeSince();
		}

		private final int split;
		public int getSplit()
		{
			return this.split;
		}

		private int itemTotal;
		public int itemTotal() { return this.itemTotal; }
		public void addItemTotal(int amount)
		{
			this.itemTotal += amount;
		}

		private double cashTotal;
		public double cashTotal() { return this.cashTotal; }
		public void addCashTotal(double amount)
		{
			this.cashTotal += amount;
		}

		private int tokenTotal;
		public int tokenTotal() { return this.tokenTotal; }
		public void addTokenTotal(int amount)
		{
			this.tokenTotal += amount;
		}

		private double sellDelay;
		private Timestamp lastSell;
		public boolean canSell()
		{
			if (this.lastSell == null)
			{
				this.lastSell = Timestamp.future(Milliseconds.fromSeconds(this.sellDelay));

				return true;
			}

			if (!this.lastSell.hasPassed()) return false;

			this.lastSell.setFuture(Milliseconds.fromSeconds(this.sellDelay));

			return true;
		}

		@Override
		public void annihilate()
		{
			Locale.send(this.asker, "prison.minebuddy_end");
			Locale.send(this.accepter, "prison.minebuddy_end");

			ModuleMinebuddy.this.buddies.remove(this.asker.getUniqueId());
			ModuleMinebuddy.this.buddies.remove(this.accepter.getUniqueId());
		}
	}

}