package com.ulfric.control.entity;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Unique;
import com.ulfric.lib.api.player.PlayerUtils;

public class PunishmentSender implements Named, Unique {

	public static final PunishmentSender CONSOLE = new PunishmentSender("CONSOLE");

	public static final PunishmentSender AGENT = new PunishmentSender("AGENT");

	public static PunishmentSender fromPlayer(Player player)
	{
		return new PunishmentSender(player);
	}

	public static PunishmentSender fromUniqueId(UUID uuid)
	{
		return new PunishmentSender(uuid);
	}

	public static PunishmentSender fromString(String sender)
	{
		String uSender = sender.toUpperCase();

		if (uSender.equals(PunishmentSender.AGENT.getName())) return PunishmentSender.AGENT;

		else if (uSender.equals(PunishmentSender.CONSOLE.getName())) return PunishmentSender.CONSOLE;

		return new PunishmentSender(sender);
	}


	private PunishmentSender(Player player)
	{
		this.uuid = player.getUniqueId();

		this.name = player.getName();
	}
	private PunishmentSender(UUID uuid)
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

		if (player == null) throw new IllegalArgumentException(uuid.toString());

		this.name = player.getName();
	}
	private PunishmentSender(String sender)
	{
		OfflinePlayer player = PlayerUtils.getOffline(sender);

		if (player == null)
		{
			this.name = sender;

			return;
		}

		this.uuid = player.getUniqueId();

		this.name = player.getName();
	}

	private UUID uuid;
	@Override
	public UUID getUniqueId() { return this.uuid; }

	private String name;
	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public String toString()
	{
		if (this.uuid == null) return this.name;

		return this.uuid.toString();
	}

	public String resolveName()
	{
		if (this.uuid == null) return this.name;

		return Bukkit.getOfflinePlayer(this.uuid).getName();
	}

}