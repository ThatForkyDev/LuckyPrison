package com.ulfric.lib.api.hook;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

import com.dsh105.echopet.api.EchoPetAPI;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.event.PetPreSpawnEvent;
import com.ulfric.lib.api.hook.PetsHook.IPetsHook;
import com.ulfric.lib.api.server.Events;
import com.ulfric.uspigot.event.player.CancellablePlayerEvent;

public final class PetsHook extends Hook<IPetsHook> {

	PetsHook()
	{
		super(IPetsHook.EMPTY, new String[]{"EchoPet", "SonarPet"}, "EchoPet hook module", "Packet", "1.1.0-REL");
	}

	@Override
	public void onHook()
	{
		this.impl(new IPetsHook() {
			@Override
			public boolean isPet(Entity entity)
			{
				if (!(entity instanceof Creature)) return false;

				for (IPet pet : EchoPetAPI.getAPI().getAllPets())
				{
					if (pet.getCraftPet().equals(entity)) return true;

					for (IPet rider = pet; rider != null; rider = rider.getRider())
					{
						if (rider.getCraftPet().equals(entity)) return true;
					}
				}

				return false;
			}

			@Override
			public boolean hasPet(Player player)
			{
				return EchoPetAPI.getAPI().hasPet(player);
			}

			@Override
			public boolean removePet(Player player, boolean message, boolean save)
			{
				if (!this.hasPet(player)) return false;

				try
				{
					EchoPetAPI.getAPI().removePet(player, message, save);
				}
				catch (RuntimeException exception)
				{
					try
					{
						EchoPetAPI.getAPI().removePet(player, message, false);
						return true;
					}
					catch (RuntimeException e)
					{
						IPet pet = EchoPetAPI.getAPI().getPet(player);

						if (pet != null)
						{
							LivingEntity craftPet = pet.getCraftPet();

							if (craftPet != null)
							{
								craftPet.remove();

								return true;
							}
						}
					}
					exception.printStackTrace();
				}

				return true;
			}
		});

		this.addListener(new Listener() {
			@EventHandler
			public void onPreSpawn(PetPreSpawnEvent event)
			{
				Events.call(new PetSpawnEvent(event));
			}

			@EventHandler(ignoreCancelled = true)
			public void onMount(EntityMountEvent event)
			{
				Entity entity = event.getEntity();

				if (!(entity instanceof Player)) return;

				Player player = (Player) entity;

				if (player.hasPermission("pets.gay.ride")) return;

				if (!PetsHook.this.isPet(event.getMount())) return;

				event.setCancelled(true);
			}
		});
	}

	public boolean isPet(Entity entity)
	{
		return this.impl.isPet(entity);
	}

	public boolean removePet(Player player, boolean message, boolean save)
	{
		return this.impl.removePet(player, message, save);
	}

	public boolean hasPet(Player player)
	{
		return this.impl.hasPet(player);
	}

	public interface IPetsHook extends HookImpl {
		IPetsHook EMPTY = new IPetsHook() {
		};

		default boolean isPet(Entity entity)
		{
			return false;
		}

		default boolean hasPet(Player player)
		{
			return false;
		}

		default boolean removePet(Player player, boolean message, boolean save)
		{
			return false;
		}
	}

	public static final class PetSpawnEvent extends CancellablePlayerEvent {
		private static final HandlerList HANDLERS = Events.newHandlerList();
		private final PetPreSpawnEvent event;

		public PetSpawnEvent(PetPreSpawnEvent event)
		{
			super(event.getPet().getOwner());

			this.event = event;
		}

		public static HandlerList getHandlerList()
		{
			return HANDLERS;
		}

		@Override
		public boolean isCancelled()
		{
			return this.event.isCancelled();
		}

		@Override
		public void setCancelled(boolean cancel)
		{
			this.event.setCancelled(cancel);
		}

		public Location getLocation()
		{
			return this.event.getSpawnLocation();
		}

		public void setLocation(Location location)
		{
			this.event.setSpawnLocation(location);
		}

		@Override
		public HandlerList getHandlers()
		{
			return HANDLERS;
		}
	}

}
