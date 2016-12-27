package com.ulfric.lib.api.hook;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.ulfric.lib.api.hook.PermissionsExHook.IPexHook;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.player.PermissionUtils;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import ru.tehkode.permissions.exceptions.RankingException;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public final class PermissionsExHook extends Hook<IPexHook> {

	private PermissionUserEngine userEngine;
	private PermissionGroupEngine groupEngine;
	private PermissionLadderEngine ladderEngine;

	PermissionsExHook()
	{
		super(IPexHook.EMPTY, "PermissionsEx", "PermissionsEx hook module", "Packet", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		this.userEngine = new PermissionUserEngine(false);
		this.groupEngine = new PermissionGroupEngine(true);
		this.ladderEngine = new PermissionLadderEngine(true);
	}

	@Override
	public void postDisable()
	{
		this.userEngine = null;
		this.groupEngine = null;
		this.ladderEngine = null;
	}

	private PermissionManager getManager()
	{
		if (!this.isModuleEnabled()) return null;

		return PermissionsEx.getPermissionManager();
	}

	public User user(Player player)
	{
		if (player == null) return null;

		return this.user(player.getUniqueId());
	}

	public User user(UUID uuid)
	{
		if (!this.isModuleEnabled()) return null;

		return this.userEngine.getUser(uuid);
	}

	public Group group(String name)
	{
		if (!this.isModuleEnabled()) return null;

		return this.groupEngine.getGroup(name);
	}

	public Map<Integer, Group> getRankLadder(String name)
	{
		if (!this.isModuleEnabled()) return null;

		return this.ladderEngine.getRankLadder(name);
	}

	public Set<User> getUsers(String group)
	{
		Set<PermissionUser> pusers = Optional.ofNullable(this.getManager().getUsers(group)).orElseGet(ImmutableSet::of);
		ImmutableSet.Builder<User> builder = ImmutableSet.builder();

		for (PermissionUser puser : pusers)
		{
			User user = this.user(puser.getPlayer());

			if (user == null) continue;

			builder.add(user);
		}

		return builder.build();
	}

	public PermissionUserEngine getUserEngine()
	{
		return this.userEngine;
	}

	public void setUserEngine(PermissionUserEngine engine)
	{
		this.userEngine = engine;
	}

	public PermissionGroupEngine getGroupEngine()
	{
		return this.groupEngine;
	}

	public void setGroupEngine(PermissionGroupEngine engine)
	{
		this.groupEngine = engine;
	}

	public PermissionLadderEngine getLadderEngine()
	{
		return this.ladderEngine;
	}

	public void setLadderEngine(PermissionLadderEngine engine)
	{
		this.ladderEngine = engine;
	}

	public interface IPexHook extends HookImpl {
		IPexHook EMPTY = new IPexHook() {
		};
	}

	public final class PermissionUserEngine extends CachingEngine<UUID, User> {
		public PermissionUserEngine(boolean caching)
		{
			super(caching);

			Assert.isTrue(PermissionsExHook.this.isModuleEnabled());
		}

		public User getUser(Player player)
		{
			return this.getUser(player.getUniqueId());
		}

		public User getUser(UUID uuid)
		{
			if (!this.isCaching())
			{
				return new User(PermissionsExHook.this.getManager().getUser(uuid), uuid);
			}

			User user = this.getCached(uuid);

			if (user != null) return user;

			PermissionUser puser = PermissionsExHook.this.getManager().getUser(uuid);

			if (puser == null) return null;

			user = new User(puser, uuid);

			this.cache(uuid, user);

			return user;
		}
	}

	public final class PermissionGroupEngine extends CachingEngine<String, Group> {
		public PermissionGroupEngine(boolean caching)
		{
			super(caching);

			Assert.isTrue(PermissionsExHook.this.isModuleEnabled());
		}

		public Group getGroup(String name)
		{
			name = name.toLowerCase();

			if (!this.isCaching())
			{
				return new Group(PermissionsExHook.this.getManager().getGroup(name));
			}

			Group group = this.getCached(name);

			if (group != null)
			{
				return group;
			}

			PermissionGroup pgroup = PermissionsExHook.this.getManager().getGroup(name);

			if (pgroup == null) return null;

			group = new Group(pgroup);

			this.cache(name, group);

			return group;
		}
	}

	public final class PermissionLadderEngine extends CachingEngine<String, Map<Integer, Group>> {
		public PermissionLadderEngine(boolean caching)
		{
			super(caching);

			Assert.isTrue(PermissionsExHook.this.isModuleEnabled());
		}

		public Map<Integer, Group> getRankLadder(String name)
		{
			name = name.toLowerCase();

			if (!this.isCaching())
			{
				return this.getUncached(name);
			}

			Map<Integer, Group> groups = this.getCached(name);

			if (groups != null)
			{
				return groups;
			}

			groups = this.getUncached(name);

			if (groups != null)
			{
				this.cache(name, groups);
			}

			return groups;
		}

		private Map<Integer, Group> getUncached(String name)
		{
			Map<Integer, PermissionGroup> ladder = PermissionsExHook.this.getManager().getRankLadder(name);

			if (ladder == null) return null;

			Map<Integer, Group> groups = Maps.newHashMapWithExpectedSize(ladder.size());

			for (Map.Entry<Integer, PermissionGroup> entry : ladder.entrySet())
			{
				groups.put(entry.getKey(), PermissionsExHook.this.group(entry.getValue().getName()));
			}

			return groups;
		}
	}

	public final class Group implements Named, Comparable<Group> {
		private final PermissionGroup group;

		private Group(PermissionGroup group)
		{
			this.group = group;
		}

		@Override
		public String getName()
		{
			return this.group.getName();
		}

		public String getPrefix()
		{
			return this.group.getPrefix();
		}

		public String getSuffix()
		{
			return this.group.getSuffix();
		}

		public boolean hasUser(User user, boolean inheritance)
		{
			return this.group.getActiveUsers(inheritance).contains(user.user);
		}

		public Set<User> getOnlineUsers(boolean inherit)
		{
			return this.group.getActiveUsers(inherit).stream()
					.map(PermissionUser::getPlayer)
					.filter(player -> player != null && player.isOnline())
					.map(PermissionsExHook.this::user)
					.collect(Collectors.toSet());
		}

		public int getRank()
		{
			return this.group.getRank();
		}

		public Group getNext()
		{
			Map<Integer, Group> ladder = PermissionsExHook.this.getRankLadder(this.group.getRankLadder());

			if (ladder == null || ladder.size() <= 1) return null;

			int rank = this.getRank();

			Group target = null;

			for (Map.Entry<Integer, Group> entry : ladder.entrySet())
			{
				int newGroup = entry.getValue().getRank();

				if (newGroup >= rank) continue;

				if (target != null && newGroup <= target.getRank()) continue;

				target = entry.getValue();
			}

			return target;
		}

		@Override
		public int compareTo(@Nonnull Group other)
		{
			return Integer.compare(this.getRank(), other.getRank());
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof Group)) return false;

			Group group1 = (Group) o;
			return this.getRank() == group1.getRank();
		}

		@Override
		public int hashCode()
		{
			return this.group.getRank();
		}
	}

	public final class User implements Named {
		private final PermissionUser user;
		private final UUID uuid;

		private User(PermissionUser user, UUID uuid)
		{
			this.user = user;

			this.uuid = uuid;
		}

		public UUID getUniqueId()
		{
			return this.uuid;
		}

		public void setGroup(Group group)
		{
			this.user.setGroups(new PermissionGroup[]{group.group});
		}

		public Group[] getGroups()
		{
			String[] rawGroups = this.getGroupNames();
			Group[] groups = new Group[rawGroups.length];

			for (int x = 0; x < groups.length; x++)
			{
				groups[x] = PermissionsExHook.this.group(rawGroups[x]);
			}

			return groups;
		}

		public String[] getGroupNames()
		{
			return this.user.getGroupNames();
		}		@Override
		public String getName()
		{
			return this.user.getName();
		}

		public String getGroupName()
		{
			return this.getGroupNames()[0];
		}

		public int getRank(String ladderName)
		{
			return this.user.getRank(ladderName);
		}

		public Group getRankLadderGroup(String groupName)
		{
			PermissionGroup group = this.user.getRankLadderGroup(groupName);

			if (group == null) return null;

			return PermissionsExHook.this.group(group.getName());
		}

		public void replace(Group old, Group newGroup)
		{
			this.remove(old);
			this.add(newGroup);
		}

		public boolean rankup(String ladder)
		{
			try
			{
				this.user.promote(null, ladder);

				return true;
			}
			catch (RankingException exception)
			{
				return false;
			}
		}

		public void add(Group group)
		{
			this.user.addGroup(group.group);
		}

		public void remove(Group group)
		{
			this.user.removeGroup(group.group);
		}

		public boolean hasGroup(Group group)
		{
			PermissionGroup pgroup = group.group;

			for (PermissionGroup lgroup : this.user.getGroups())
			{
				if (!pgroup.equals(lgroup)) continue;

				return true;
			}

			return false;
		}

		public List<String> getParentIdentifiers()
		{
			return this.user.getParentIdentifiers();
		}

		public void save()
		{
			this.user.save();
		}

		public boolean hasPermission(String permission)
		{
			return this.user.has(permission);
		}

		public int getAllowance(String permission)
		{
			return this.user.getAllPermissions().values().stream().mapToInt(list -> PermissionUtils.getMax(list, permission)).max().orElse(0);
		}




	}

}
