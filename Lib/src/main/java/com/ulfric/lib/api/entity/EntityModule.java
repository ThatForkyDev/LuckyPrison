package com.ulfric.lib.api.entity;

import com.google.common.collect.Sets;
import com.ulfric.lib.Lib;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerModule;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.task.TimedTask;
import com.ulfric.lib.api.time.Timestamp;
import com.ulfric.lib.api.tuple.Pair;
import com.ulfric.lib.api.tuple.Tuples;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public final class EntityModule extends SimpleModule {

	public EntityModule()
	{
		super("entityutils", "Entity utilities module", "Packet", "Packet");

		this.withSubModule(new EntityBuilderModule());
		this.withSubModule(new ArmorStandsModule());
		this.withSubModule(new MetadataModule());
		this.withSubModule(new PlayerModule());
	}

	@Override
	public void runTest(Object object)
	{
		Player player = (Player) object;

		Location location = player.getLocation();

		int distanceSquared = 2 * 2;

		player.sendMessage("Count: " + player.getWorld().getEntities().stream().filter(entity -> entity.getLocation().distanceSquared(location) < distanceSquared).count());
	}

	@Override
	public void postEnable()
	{
		EntityUtils.impl = new EntityUtils.IEntityUtils() {
			@Override
			public EntityType parse(String value)
			{
				Assert.isNotEmpty(value, "The entity type name must not be null!", "The entity type name must not be empty!");

				value = value.toUpperCase().replace(' ', '_').replace("_", Strings.BLANK);

				for (EntityType type : EntityType.values())
				{
					if (!type.name().replace("_", Strings.BLANK).equals(value)) continue;

					return type;
				}

				return null;
			}

			@Override
			public EntityType parseLiving(String value)
			{
				EntityType type = this.parse(value);

				if (type == null || !type.isAlive()) return null;

				return type;
			}

			@Override
			public void kill(Damageable damageable)
			{
				damageable.setHealth(0);
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T extends Entity> T spawnEntity(EntityType type, Location location)
			{
				if (type == EntityType.LIGHTNING)
				{
					return (T) location.getWorld().strikeLightning(location);
				}

				return (T) location.getWorld().spawnEntity(location, type);
			}

			@Override
			public Entity spawnTemporaryEntity(EntityType type, Location location)
			{
				Entity entity = EntityUtils.spawnEntity(type, location);

				Metadata.applyNull(entity, "_ulf_temporary");

				return entity;
			}

			@Override
			public void removeNonpermanentEntities()
			{
				EntityUtils.getAllEntities().stream().filter(ent -> ent.hasMetadata("_ulf_temporary")).forEach(Entity::remove);
			}

			@Override
			public Set<Entity> getAllEntities()
			{
				Set<Entity> entities = Sets.newHashSet();

				Bukkit.getWorlds().forEach((world) -> world.getEntities().forEach(entities::add));

				return entities;
			}

			@Override
			public Set<LivingEntity> getNearbyLivingEntities(Location location, double distanceSquared)
			{
				Set<LivingEntity> nearby = Sets.newHashSet();

				for (LivingEntity entity : location.getWorld().getLivingEntities())
				{
					if (entity.getLocation().distanceSquared(location) > distanceSquared) continue;

					nearby.add(entity);
				}

				return nearby;
			}

			@Override
			public Projectile launchProjectile(ProjectileSource sauce, ProjectileType type)
			{
				return sauce.launchProjectile(type.getClazz());
			}

			@Override
			public Projectile launchProjectile(ProjectileSource sauce, ProjectileType type, Vector vector)
			{
				return sauce.launchProjectile(type.getClazz(), vector);
			}
		};
	}

	private static final class ArmorStandsModule extends SimpleModule {
		ArmorStandsModule()
		{
			super("armorstands", "Armor stand utilities module", "Packet", "1.0.0-REL");

			this.withSubModule(new HologramsModule());
		}

		@Override
		public void postEnable()
		{
			ArmorStandUtils.impl = new ArmorStandUtils.IArmorStandUtils() {
				@Override
				public ArmorStand spawn(Location location)
				{
					return ArmorStandUtils.spawn(location, true, true);
				}

				@Override
				public ArmorStand spawn(Location location, boolean notate, boolean temporary)
				{
					if (notate)
					{
						LocationUtils.notate(location);
					}

					return ArmorStandUtils.spawn(location, temporary, null);
				}

				@Override
				public ArmorStand spawn(Location location, boolean temporary, Object dummy)
				{
					if (temporary)
					{
						return (ArmorStand) EntityUtils.spawnTemporaryEntity(EntityType.ARMOR_STAND, location);
					}

					return EntityUtils.spawnEntity(EntityType.ARMOR_STAND, location);
				}

				@Override
				public void stagnate(ArmorStand stand)
				{
					stand.setVisible(false);

					stand.setGravity(false);

					stand.setBasePlate(false);

					ArmorStandUtils.zero(stand);
				}

				@Override
				public void zero(ArmorStand stand)
				{
					stand.teleport(LocationUtils.notate(stand.getLocation()));

					stand.setBodyPose(EulerAngle.ZERO);

					stand.setHeadPose(EulerAngle.ZERO);

					stand.setLeftArmPose(EulerAngle.ZERO);

					stand.setRightArmPose(EulerAngle.ZERO);

					stand.setLeftLegPose(EulerAngle.ZERO);

					stand.setRightLegPose(EulerAngle.ZERO);
				}
			};
		}

		@Override
		public void postDisable()
		{
			ArmorStandUtils.impl = ArmorStandUtils.IArmorStandUtils.EMPTY;
		}
	}

	private static final class HologramsModule extends SimpleModule {
		HologramsModule()
		{
			super("holograms", "Hologram manager", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			HologramUtils.impl = new HologramUtils.IHologramUtils() {
				@Override
				public ArmorStand[] spawn(Location location, String text)
				{
					String[] split = text.split("<n>");

					ArmorStand[] stands = new ArmorStand[split.length];

					int x = 0;
					for (String line : split)
					{
						ArmorStand stand = ArmorStandUtils.spawn(location.subtract(0, 0.24, 0), false, true);

						ArmorStandUtils.stagnate(stand);

						stand.setCustomName(line);

						stand.setCustomNameVisible(true);

						stands[x++] = stand;
					}

					return stands;
				}
			};
		}

		@Override
		public void postDisable()
		{
			HologramUtils.impl = HologramUtils.IHologramUtils.EMPTY;
		}
	}

	private static final class EntityBuilderModule extends SimpleModule {
		EntityBuilderModule()
		{
			super("entitybuilder", "Entity builder class", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			EntityBuilder.impl = new EntityBuilder.IEntityBuilder() {
				@Override
				public EntityBuilder create()
				{
					return new EntityBuilder();
				}
			};
		}

		@Override
		public void postDisable()
		{
			EntityBuilder.impl = EntityBuilder.IEntityBuilder.EMPTY;
		}
	}	@Override
	public void postDisable()
	{
		EntityUtils.impl = EntityUtils.IEntityUtils.EMPTY;
	}

	private static final class MetadataModule extends SimpleModule {
		MetadataModule()
		{
			super("metadata", "Metadata utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Metadata.impl = new Metadata.IMetadata() {
				private final FixedMetadataValue fixed = new FixedMetadataValue(Lib.get(), null);

				@Override
				@SuppressWarnings("unchecked")
				public <T> T getValue(Metadatable metadatable, String path)
				{
					if (!metadatable.hasMetadata(path)) return null;

					return (T) metadatable.getMetadata(path).get(0).value();
				}

				@Override
				public boolean getValueAsBoolean(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public byte getValueAsByte(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public short getValueAsShort(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public int getValueAsInt(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public long getValueAsLong(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public double getValueAsDouble(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public float getValueAsFloat(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public String getValueAsString(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public <T> List<T> getValueAsList(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public List<String> getValueAsStringList(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public <T> Set<T> getValueAsSet(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public Timestamp getValueAsTimestamp(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public ATask getValueAsTask(Metadatable metadatable, String path)
				{
					return this.getValue(metadatable, path);
				}

				@Override
				public void applyNull(Metadatable metadatable, String path)
				{
					metadatable.setMetadata(path, this.fixed);
				}

				@Override
				public void applyTrue(Metadatable metadatable, String path)
				{
					this.apply(metadatable, path, true);
				}

				@Override
				public void applyFalse(Metadatable metadatable, String path)
				{
					this.apply(metadatable, path, false);
				}

				@Override
				public boolean applyTemp(Metadatable metadatable, String path, Object object, long ticks)
				{
					Pair<TimedTask, Object> current = this.getValue(metadatable, path);

					if (current != null)
					{
						current.getA().addTime(ticks);

						return false;
					}

					this.apply(metadatable, path, Tuples.newPair(Tasks.newTimedTask(() -> this.remove(metadatable, path), ticks), object));

					return true;
				}

				@Override
				public void apply(Metadatable metadatable, String path, Object object)
				{
					metadatable.setMetadata(path, new FixedMetadataValue(Lib.get(), object));
				}

				@Override
				public void remove(Metadatable metadatable, String path)
				{
					metadatable.removeMetadata(path, Lib.get());
				}

				@Override
				public <T> T getAndRemove(Metadatable metadatable, String path)
				{
					T t = this.getValue(metadatable, path);

					if (t == null) return null;

					this.remove(metadatable, path);

					return t;
				}

				@Override
				public boolean removeIfPresent(Metadatable metadatable, String path)
				{
					if (!metadatable.hasMetadata(path)) return false;

					this.remove(metadatable, path);

					return true;
				}

				@Override
				public void tieToPlayer(Metadatable metadatable, Player player)
				{
					this.apply(metadatable, "_ulf_playercause", player);
				}

				@Override
				public Player getTied(Metadatable metadatable)
				{
					return this.getValue(metadatable, "_ulf_playercause");
				}
			};
		}

		@Override
		public void postDisable()
		{
			Metadata.impl = Metadata.IMetadata.EMPTY;
		}
	}




}
