package com.ulfric.control.entity;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;

import com.ulfric.control.coll.IPCache;
import com.ulfric.control.coll.PunishmentExecutor;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.control.entity.enums.ViolationType;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.chat.ChatMessage;
import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.lib.api.time.Timestamp;

public class IPData implements Iterable<Player> {

	public IPData(Player player, String ip)
	{
		this.players = Sets.newWeakSet();

		this.addPlayer(player);

		this.violation = new Violation(player);

		this.ip = ip;

		this.lastLogin = Timestamp.now();

		this.lastMessage = Chat.newChatMessage(Timestamp.now(), Strings.BLANK);
	}

	private final String ip;
	public String getIp() { return this.ip; }

	private Set<Player> players;
	@Override
	public Iterator<Player> iterator()
	{
		return this.players.iterator();
	}
	public void addPlayer(Player player)
	{
		player = PlayerUtils.proxy(player);

		this.players.add(player);
	}
	public void removePlayer(Player player)
	{
		this.players.remove(PlayerUtils.proxy(player));

		if (!this.players.isEmpty()) return;

		IPCache.INSTANCE.remove(this);
	}
	public int count()
	{
		return this.players.size();
	}
	public int secureCount()
	{
		return (int) this.players.stream().filter(Player::isOnline).count();
	}

	private Violation violation;
	public Violation getViolation() { return this.violation; }
	public float getViolationTotal() { return this.violation.getTotal(); }

	public void incrementViolation(ViolationType type)
	{
		this.incrementViolation(type, PunishmentType.MUTE);
	}

	public synchronized void incrementViolation(ViolationType type, PunishmentType punish)
	{
		if (!this.getViolation().increment(type)) return;

		this.violation.dump();

		Tasks.run(() -> PunishmentExecutor.execute(new Punishment(PunishmentHolder.fromIP(this.ip), PunishmentSender.AGENT, punish, "Automated detection of rule-breach", TimeUtils.formatCurrentDay(), Timestamp.future(Milliseconds.fromHours(24)), -1)));
	}

	private Timestamp lastLogin;
	public Timestamp getLastLogin() { return this.lastLogin; }

	private ChatMessage lastMessage;
	public ChatMessage getLastMessage() { return this.lastMessage; }
	public void setLastMessage(ChatMessage lastMessage) { this.lastMessage = lastMessage; }

}