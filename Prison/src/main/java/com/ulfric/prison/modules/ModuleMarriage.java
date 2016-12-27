package com.ulfric.prison.modules;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.Cooldown;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.DataHook.IPlayerData;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Uuids;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.TimeUtils;

public class ModuleMarriage extends SimpleModule {

	private static final ModuleMarriage INSTANCE = new ModuleMarriage();

	public static ModuleMarriage get()
	{
		return ModuleMarriage.INSTANCE;
	}

	private ModuleMarriage()
	{
		super("marriage", "Marriage feature module", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand("propose", new ProposeCommand());
		this.addCommand("marry", new MarryCommand());
		this.addCommand("divorce", new DivorceCommand());
		this.addCommand("marriage", new MarriageCommand());

		this.addListener(new Listener() // Safety first
		{
			@EventHandler(ignoreCancelled = true)
			public void onMarry(MarriageEvent event)
			{
				if (Hooks.DATA.getPlayerDataAsString(event.getUUID1(), "prison.marriage.partner") == null
					&& Hooks.DATA.getPlayerDataAsString(event.getUUID2(), "prison.marriage.partner") == null) return;

				event.setCancelled(true);
			}
		});
	}

	private class ProposeCommand extends SimpleCommand
	{
		private ProposeCommand()
		{
			this.withEnforcePlayer();

			this.withCooldown(Cooldown.builder().withName("propose").withDefaultDelay(Milliseconds.fromMinutes(45)));

			this.withArgument(Argument.REQUIRED_PLAYER);
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();
			UUID uuid = player.getUniqueId();

			Player partner = (Player) this.getObject("player");
			UUID partnerUUID = partner.getUniqueId();

			if (uuid.equals(partnerUUID))
			{
				Locale.sendError(player, "chat.marriage_self");

				return;
			}

			if (Hooks.DATA.getPlayerDataAsBoolean(uuid, "chat.marriage"))
			{
				Locale.sendError(player, "chat.enable_marriage");

				return;
			}

			String married = Hooks.DATA.getPlayerDataAsString(uuid, "prison.marriage.partner");

			if (married != null)
			{
				Locale.send(player, "prison.married_already", PlayerUtils.getOffline(married).getName());

				return;
			}

			married = Hooks.DATA.getPlayerDataAsString(partnerUUID, "prison.marriage.partner");

			if (married != null)
			{
				Locale.send(player, "prison.married_already_other", partner.getName(), PlayerUtils.getOffline(married).getName());

				return;
			}

			String name1 = player.getName();
			String name2 = partner.getName();

			PlayerUtils.getOnlinePlayersExceptFor(player, partner)
				  .stream()
				  .filter(lplayer -> !Hooks.DATA.getPlayerDataAsBoolean(lplayer.getUniqueId(), "chat.marriage"))
				  .forEach(lplayer -> Locale.send(lplayer, "prison.marriage_proposal_global", name1, name2));

			Locale.send(player, "prison.marriage_proposal_to", name2);
			Locale.send(partner, "prison.marriage_proposal_from", name1);

			Metadata.apply(player, "_ulf_proposed", partnerUUID);

			Set<UUID> proposals = Metadata.getValueAsSet(partner, "_ulf_proposals");

			if (proposals == null)
			{
				proposals = Sets.newHashSet();

				Metadata.apply(partner, "_ulf_proposals", proposals);
			}

			proposals.add(player.getUniqueId());
		}
	}

	private class MarryCommand extends SimpleCommand
	{
		public MarryCommand()
		{
			this.withEnforcePlayer();

			this.withArgument("player", ArgStrategy.PLAYER);

			this.withCooldown(Cooldown.builder().withName("marry").withDefaultDelay(Milliseconds.fromMinutes(5)));
		}

		// TODO don't allow using this command if marriages are disabled
		@Override
		public void run()
		{
			Player player = this.getPlayer();

			Set<UUID> proposals = Metadata.getValueAsSet(player, "_ulf_proposals");

			Player partner = (Player) this.getObject("player");

			if (CollectUtils.isEmpty(proposals))
			{
				if (partner != null)
				{
					Commands.dispatch(player, "propose " + partner.getName());

					return;
				}

				Locale.sendError(player, "prison.marriage_proposal_none");

				return;
			}
			else if (partner == null)
			{
				if (proposals.size() > 1)
				{
					Locale.sendError(player, "prison.marriage_proposal_specify");

					return;
				}

				partner = PlayerUtils.proxy(proposals.iterator().next());

				if (!player.getUniqueId().equals(Metadata.getValue(partner, "_ulf_proposed")))
				{
					Commands.dispatch(player, "propose " + partner.getName());

					return;
				}
			}
			else if (!proposals.contains(partner.getUniqueId()))
			{
				Commands.dispatch(player, "propose " + partner.getName());

				return;
			}

			Metadata.remove(partner, "_ulf_proposed");

			if (Events.call(new MarriageEvent(player.getUniqueId(), partner.getUniqueId())).isCancelled())
			{
				Locale.sendError(player, "prison.marriage_error");

				return;
			}

			String day = TimeUtils.formatCurrentDay();

			// TODO cleanup
			IPlayerData data = Hooks.DATA.getPlayerData(player.getUniqueId());
			data.set("prison.marriage.partner", partner.getUniqueId().toString());
			data.set("prison.marriage.date", day);

			data = Hooks.DATA.getPlayerData(partner.getUniqueId());
			data.set("prison.marriage.partner", player.getUniqueId().toString());
			data.set("prison.marriage.date", day);
			// end TODO

			String name1 = player.getName();
			String name2 = partner.getName();

			PlayerUtils.getOnlinePlayersExceptFor(player, partner)
			  .stream()
			  .filter(lplayer -> !Hooks.DATA.getPlayerDataAsBoolean(lplayer.getUniqueId(), "chat.marriage"))
			  .forEach(lplayer -> Locale.send(lplayer, "prison.marriage_proposal_accept", name1, name2));

			Locale.send(partner, "prison.marriage_proposal_accepted", name1);
			Locale.send(player, "prison.marriage_proposal_accepted2", name2);
		}
	}

	private class DivorceCommand extends SimpleCommand
	{
		private DivorceCommand()
		{
			this.withEnforcePlayer();
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			IPlayerData data = Hooks.DATA.getPlayerData(player.getUniqueId());

			String partnerUUID = (String) data.get("prison.marriage.partner");

			if (partnerUUID == null)
			{
				Locale.sendError(player, "prison.marriage_not_married");

				return;
			}

			Player partner = PlayerUtils.proxy(Uuids.parse(partnerUUID));

			this.remove(data);
			this.remove(Hooks.DATA.getPlayerData(partner.getUniqueId()));

			data.remove("prison.marriage.partner");
			data.remove("prison.marriage.date");

			Locale.send(player, "prison.marriage_divorced", partner.getName()); // TODO this might be null if the partner is offline

			Locale.send(partner, "prison.marriage_divorced2", player.getName()); // TODO make sure this doesn't throw an error if the partner is offline
		}

		private void remove(IPlayerData data)
		{
			data.remove("prison.marriage.partner");
			data.remove("prison.marriage.date");
		}
	}

	private class MarriageCommand extends SimpleCommand
	{
		private MarriageCommand()
		{
			this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer);
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getObject("player");

			IPlayerData data = Hooks.DATA.getPlayerData(player.getUniqueId());

			String partnerUUID = (String) data.get("prison.marriage.partner");

			if (partnerUUID == null)
			{
				Locale.send(this.getSender(), "prison.marriage_not_married");

				return;
			}

			Locale.send(this.getSender(), "prison.marriage_info", PlayerUtils.getOffline(partnerUUID).getName(), data.get("prison.marriage.date"));
		}
	}

	public static class MarriageEvent extends Event implements Cancellable
	{
		public MarriageEvent(UUID uuid1, UUID uuid2)
		{
			this.uuid1 = uuid1;

			this.uuid2 = uuid2;
		}

		private final UUID uuid1;
		public UUID getUUID1()
		{
			return this.uuid1;
		}

		private final UUID uuid2;
		public UUID getUUID2()
		{
			return this.uuid2;
		}

		private boolean cancel;
		@Override
		public boolean isCancelled()
		{
			return this.cancel;
		}
		@Override
		public void setCancelled(boolean cancel)
		{
			this.cancel = cancel;
		}

		private static final HandlerList HANDLERS = Events.newHandlerList();
		@Override
		public HandlerList getHandlers()
		{
			return MarriageEvent.HANDLERS;
		}
		public static HandlerList getHandlerList()
		{
			return MarriageEvent.HANDLERS;
		}
	}

}