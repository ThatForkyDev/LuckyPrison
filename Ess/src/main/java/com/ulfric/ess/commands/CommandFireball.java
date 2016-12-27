package com.ulfric.ess.commands;

import com.ulfric.lib.api.command.Cooldown;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.entity.ProjectileType;
import com.ulfric.lib.api.time.Milliseconds;

public class CommandFireball extends SimpleCommand {

	public CommandFireball()
	{
		this.withEnforcePlayer();

		this.withCooldown(Cooldown.builder().withName("fireball").withDefaultDelay(Milliseconds.fromSeconds(30)));
	}

	@Override
	public void run()
	{
		EntityUtils.launchProjectile(this.getPlayer(), ProjectileType.FIREBALL);
	}

}