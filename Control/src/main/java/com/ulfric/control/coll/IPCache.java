package com.ulfric.control.coll;

import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.ulfric.control.entity.IPData;
import com.ulfric.lib.api.player.PlayerUtils;

public enum IPCache {

	INSTANCE;

	private final Map<String, IPData> ips = Maps.newHashMap();

	public void add(IPData data)
	{
		this.ips.put(data.getIp(), data);
	}

	public void remove(IPData data)
	{
		this.ips.remove(data.getIp());
	}

	public IPData getData(Player player)
	{
		return this.getData(PlayerUtils.getIP(player));
	}
	public IPData getData(String address)
	{
		return this.ips.get(address);
	}

	public int size()
	{
		return this.ips.size();
	}

}