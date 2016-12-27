package com.ulfric.lib.api.command;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook;
import com.ulfric.lib.api.java.Named;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class Cooldown implements Named {

	private final String name;
	private final CooldownNode defaultDelay;
	private final Map<PermissionsExHook.Group, CooldownNode> delays;

	private Cooldown(String name, CooldownNode defaultDelay, Map<PermissionsExHook.Group, CooldownNode> delays)
	{
		this.name = name.toLowerCase();

		this.defaultDelay = defaultDelay;

		this.delays = delays;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public CooldownNode getDefaultDelay()
	{
		return this.defaultDelay;
	}

	public CooldownState test(UUID uuid)
	{
		if (CollectUtils.isEmpty(this.delays)) return this.defaultDelay.getDelay(uuid);

		CooldownNode node = this.defaultDelay;

		PermissionsExHook.User user = Hooks.PERMISSIONS.user(uuid);

		if (user.hasPermission("lib.nocooldown")) return CooldownState.ALLOWED;

		for (PermissionsExHook.Group group : user.getGroups())
		{
			CooldownNode lnode = this.delays.get(group);

			if (lnode == null) continue;

			node = node.getLower(lnode);
		}

		return node.getDelay(uuid);
	}

	public static final class Builder implements com.ulfric.lib.api.java.Builder<Cooldown> {
		private String name;
		private CooldownNode defaultDelay;
		private Map<PermissionsExHook.Group, CooldownNode> delays;

		@Override
		public Cooldown build()
		{
			Cooldown cooldown = new Cooldown(this.name, Optional.ofNullable(this.defaultDelay).orElseGet(CooldownNode::new), this.delays);

			this.defaultDelay.setOwner(cooldown);

			if (this.delays == null) return cooldown;

			this.delays.values().forEach(node -> node.setOwner(cooldown));

			return cooldown;
		}

		public Builder withName(String name)
		{
			this.name = name;

			return this;
		}

		public Builder withDefaultDelay(long defaultDelay)
		{
			this.defaultDelay = new CooldownNode(defaultDelay);

			return this;
		}

		public Builder withDelay(String groupName, long delay)
		{
			PermissionsExHook.Group group = Hooks.PERMISSIONS.group(groupName);

			if (group == null) return this;

			if (this.delays == null)
			{
				this.delays = Maps.newHashMap();
			}

			this.delays.put(group, new CooldownNode(delay));

			return this;
		}
	}

}
