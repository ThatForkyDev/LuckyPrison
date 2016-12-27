package com.ulfric.ess.commands;

import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Annihilate;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.IPlayer;
import com.ulfric.lib.api.player.PlayerAcceptTeleportEvent;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.teleport.TeleportUtils;
import com.ulfric.lib.api.time.Ticks;

public class CommandTeleporthandshake {

	public static class Que extends SimpleCommand
	{
		public Que()
		{
			this.withEnforcePlayer();

			this.withArgument(Argument.REQUIRED_PLAYER);
		}

		@Override
		public void run()
		{
			Player target = (Player) this.getObject("player");

			final String owner = (target.equals(this.getPlayer()) ? Locale.getMessage(this.getSender(), "system.your") : target.getName() + "'s");

			if (!TeleportAskQue.INSTANCE.hasUser(target.getUniqueId()))
			{
				Locale.send(this.getPlayer(), "ess.tpque_empty", owner);

				return;
			}

			Locale.send(this.getPlayer(), "ess.tpque", owner);

			int length = 0;
			for (TeleportRequest request : TeleportAskQue.INSTANCE.getRequest(target.getUniqueId()))
			{
				this.getSender().sendMessage(ChatColor.AQUA + " - " + request.getPlayer().getName());

				length++;
			}

			Locale.send(this.getPlayer(), "ess.tpque_total", length);
		}
	}


	public static class Ask extends SimpleCommand
	{
		public Ask()
		{
			this.withEnforcePlayer();

			this.withArgument(Argument.REQUIRED_PLAYER);
		}

		@Override
		public void run()
		{
			Player target = (Player) this.getObject("player");

			if (target.equals(this.getPlayer()))
			{
				Locale.send(this.getPlayer(), "ess.teleport_not_found");

				return;
			}

			TeleportAskQue.INSTANCE.addUser(target, new TeleportRequest(this.getPlayer(), target));
		}
	}

	public static class Accept extends SimpleCommand
	{
		public Accept()
		{
			this.withEnforcePlayer();

			this.withArgument("player", ArgStrategy.PLAYER);
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			if (!TeleportAskQue.INSTANCE.hasUser(player))
			{
				Locale.sendError(player, "ess.tpaccept_none");

				return;
			}

			Player target = (Player) this.getObject("player");

			if (target != null && target.equals(player) && this.hasObjects())
			{
				Locale.send(this.getPlayer(), "ess.teleport_not_found");

				return;
			}

			TeleportRequest request = null;

			for (TeleportRequest lrequest : TeleportAskQue.INSTANCE.getUser(player))
			{
				Player ltarget = lrequest.getTarget();

				if (!ltarget.isOnline() || !ltarget.isValid()) continue;

				if (target != null && !target.equals(ltarget)) continue;

				request = TeleportAskQue.INSTANCE.getUser(this.getPlayer()).get(TeleportAskQue.INSTANCE.getUser(player).indexOf(lrequest));
			}

			if (request == null)
			{
				if (target != null)
				{
					Locale.sendError(player, "ess.tpaccept_none_from", target.getName());

					return;
				}
				do
				{
					if (TeleportAskQue.INSTANCE.getUser(player).isEmpty())
					{
						TeleportAskQue.INSTANCE.removeUser(player);

						break;
					}

					request = TeleportAskQue.INSTANCE.getUser(player).pop();
				} while (request == null || !(request.getTarget().isValid() && request.getTarget().isOnline()));
			}

			if (request == null)
			{
				Locale.sendError(player, "ess.tpaccept_none");

				return;
			}

			if (Events.call(new PlayerAcceptTeleportEvent(request.getTarget(), request.getPlayer())).isCancelled()) return;

			request.teleport();
		}
	}

	private static class TeleportAskQue
	{
		private static final TeleportAskQue INSTANCE = new TeleportAskQue();

		private TeleportAskQue()
		{
			this.requests = Maps.newHashMap();
		}

		private final Map<UUID, Stack<TeleportRequest>> requests;
		private Stack<TeleportRequest> getRequest(UUID uuid) { return this.requests.get(uuid); }

		private boolean hasUser(Player player) { return this.hasUser(player.getUniqueId()); }
		private boolean hasUser(UUID uuid) { return this.requests.containsKey(uuid) && !this.requests.get(uuid).isEmpty(); }

		private void addUser(Player player, TeleportRequest request) { this.addUser(player.getUniqueId(), request); }
		private void addUser(UUID uuid, TeleportRequest request)
		{
			Stack<TeleportRequest> stack = this.requests.get(uuid);

			if (stack == null)
			{
				stack = new Stack<>();

				stack.add(request);

				this.requests.put(uuid, stack);

				return;
			}

			stack.push(request);
		}

		private void removeUser(Player player) { this.removeUser(player.getUniqueId()); }
		private void removeUser(UUID uuid) { this.requests.remove(uuid); }

		private Stack<TeleportRequest> getUser(Player player) { return this.gerUser(player.getUniqueId()); }
		private Stack<TeleportRequest> gerUser(UUID uuid) { return this.requests.get(uuid); }
	}


	private static class TeleportRequest implements Annihilate, IPlayer
	{
		private TeleportRequest(Player sender, Player target)
		{
			this.sender = sender;

			this.target = target;

			Locale.send(sender, "ess.tpask", target.getName());

			Locale.send(target, "ess.tprequest", sender.getName());

			Tasks.runLater(this::annihilate, Ticks.MINUTE);
		}

		private final Player sender;
		@Override
		public Player getPlayer() { return this.sender; }

		private final Player target;
		public Player getTarget() { return this.target; }

		private void teleport()
		{
			Locale.send(this.getPlayer(), "ess.tpask_accept", this.getTarget().getName());

			Locale.send(this.getTarget(), "ess.tprequest_accept", this.getPlayer().getName());

			TeleportUtils.teleport(this.getPlayer(), this.getTarget().getLocation(), 5);

			this.annihilate();
		}

		@Override
		public void annihilate()
		{
			Optional.ofNullable(TeleportAskQue.INSTANCE.getUser(this.target)).ifPresent(stack -> stack.remove(this));
		}
	}

}