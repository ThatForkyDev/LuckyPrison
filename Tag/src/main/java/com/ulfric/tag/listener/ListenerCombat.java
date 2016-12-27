package com.ulfric.tag.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.PetsHook.PetSpawnEvent;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.server.ServerPreRebootEvent;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.tag.CombatTag;
import com.ulfric.tag.Tag;
import com.ulfric.uspigot.event.player.PlayerFlightCapabilitiesEvent;
import com.ulfric.uspigot.event.server.ServerShutdownEvent;

public class ListenerCombat implements Listener {

	@EventHandler
	public void onPetSpawn(PetSpawnEvent event)
	{
		if (!CombatTag.isTagged(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		event.setDeathMessage(null);

		Player player = event.getEntity();

		if (!CombatTag.isTagged(player)) return;

		Tasks.cancel(Metadata.getValueAsInt(player, "_ulf_combattag"));

		Metadata.remove(player, "_ulf_combattag");
	}

	@EventHandler
	public void onTFly(PlayerFlightCapabilitiesEvent event)
	{
		if (!event.isAllowed()) return;

		if (!CombatTag.isTagged(event.getPlayer())) return;

		event.setAllowed(false);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER) return;

		Player attacker = null;
		Entity damager = event.getDamager();

		if (damager instanceof Player)
		{
			attacker = (Player) damager;
		}
		else if (damager instanceof Tameable)
		{
			Tameable tame = (Tameable) damager;

			AnimalTamer tamer = tame.getOwner();

			if (!(tamer instanceof Player)) return;

			attacker = (Player) tamer;
		}
		else if (damager instanceof Projectile)
		{
			Projectile projectile = (Projectile) damager;

			ProjectileSource source = projectile.getShooter();

			if (!(source instanceof Player)) return;

			attacker = (Player) source;
		}
		else if (damager instanceof TNTPrimed)
		{
			Entity source = ((TNTPrimed) damager).getSource();

			if (!(source instanceof Player)) return;

			attacker = (Player) source;
		}
		else return;

		if (attacker.getGameMode() == GameMode.CREATIVE) return;

		if (!attacker.getWorld().getPVP() || attacker.getWorld().getName().toLowerCase().equals("world_plots") /* TODO TEMP FIX */) return;

		this.tag((Player) event.getEntity(), attacker);
	}

	private void tag(Player victim, Player attacker)
	{
		if (!CombatTag.tag(victim, 17))
		{
			Locale.send(victim, "tag.damaged_by", attacker.getName());
		}

		if (CombatTag.tag(attacker, 17)) return;

		Locale.send(attacker, "tag.damaged", victim.getName());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		if (!CombatTag.isTagged(event.getPlayer())) return;

		Tasks.cancel(Metadata.getValueAsInt(player, "_ulf_combattag"));

		Metadata.remove(player, "_ulf_combattag");

		if (player.isDead()) return;

		EntityUtils.kill(player);

		Tag.get().log(Strings.format(Locale.getMessage("tag.admin_notify"), player.getName()));
		Locale.sendMassPerm("tag.notify", "tag.admin_notify", player.getName());
	}

	@EventHandler
	public void onPreReboot(ServerPreRebootEvent event)
	{
		CombatTag.disabled = true;
	}

	@EventHandler
	public void onShutdown(ServerShutdownEvent event)
	{
		CombatTag.disabled = true;
	}

}