package com.ulfric.lib.api.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import org.apache.commons.collections4.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class Command implements ICommand, CommandExecutor {

	private final Map<String, Object> objects;
	private boolean enforcePlayer;
	private boolean indexUnused;
	private String unused;
	private boolean subcommand;
	private Map<Set<String>, SubCommand> subcommands;
	private Map<SubCommand, List<Argument>> arguments;
	private Cooldown cooldown;
	private CommandSender sender;
	private Player player;
	private boolean isPlayer;
	private org.bukkit.command.Command command;
	private String label;
	private String[] args;

	protected Command()
	{
		this.objects = Maps.newHashMap();
	}

	@Override
	public boolean isEnforcePlayer()
	{
		return this.enforcePlayer;
	}

	@Override
	public void withEnforcePlayer()
	{
		this.enforcePlayer = true;
	}

	@Override
	public boolean isSubcommand()
	{
		return this.subcommand;
	}

	@Override
	public void withArgument(Argument argument)
	{
		this.withArgument(null, argument);
	}

	@Override
	public List<Argument> getArguments()
	{
		return this.getArguments(null);
	}

	@Override
	public boolean doesIndexUnusedArgs()
	{
		return this.indexUnused;
	}

	@Override
	public void withIndexUnusedArgs()
	{
		this.indexUnused = true;
	}

	@Override
	public String getUnusedArgs()
	{
		return this.unused;
	}

	@Override
	public Cooldown getCooldown()
	{
		return this.cooldown;
	}

	@Override
	public void withCooldown(Cooldown cooldown)
	{
		this.cooldown = cooldown;
	}

	@Override
	public CommandSender getSender()
	{
		return this.sender;
	}

	@Override
	public Player getPlayer()
	{
		return this.player;
	}

	@Override
	public boolean isPlayer()
	{
		return this.isPlayer;
	}

	@Override
	public org.bukkit.command.Command getCommand()
	{
		return this.command;
	}

	@Override
	public String getLabel()
	{
		return this.label;
	}

	@Override
	public String[] getArgs()
	{
		return this.args;
	}

	@Override
	public String getArg(int index)
	{
		return this.args[index];
	}

	@Override
	public Object getObject(String path)
	{
		return this.objects.get(path);
	}

	@Override
	public boolean hasObject(String path)
	{
		return this.objects.containsKey(path);
	}

	public void withSubcommand(SubCommand subcommand)
	{
		this.subcommand = true;

		if (this.subcommands == null) this.subcommands = Maps.newHashMap();

		Set<String> strings = Sets.newHashSet(subcommand.getAliases());

		strings.add(subcommand.getName());

		this.subcommands.put(strings, subcommand);
	}

	public SubCommand getSubcommand(String name)
	{
		for (Map.Entry<Set<String>, SubCommand> entry : this.subcommands.entrySet())
		{
			if (!entry.getKey().contains(name)) continue;

			return entry.getValue();
		}

		return null;
	}

	public List<Argument> getArguments(SubCommand command)
	{
		if (this.arguments == null || this.arguments.isEmpty()) return null;

		return this.arguments.get(command);
	}

	public void withArgument(SubCommand command, Argument argument)
	{
		if (this.arguments == null) this.arguments = Maps.newHashMap();

		List<Argument> arguments = this.arguments.get(command);

		if (arguments == null)
		{
			arguments = Lists.newArrayList();

			this.arguments.put(command, arguments);
		}

		arguments.add(argument);
	}

	public void sanitize()
	{
		this.sender = null;
		this.player = null;
		this.command = null;
		this.label = null;
		this.args = null;
		this.unused = null;
		this.objects.clear();
	}

	@Override
	public UUID getUniqueId()
	{
		return this.player.getUniqueId();
	}

	@Override
	public String getName()
	{
		return this.sender.getName();
	}

	@Override
	public Location getLocation()
	{
		return this.player.getLocation();
	}

	public boolean hasArgs()
	{
		return this.args.length > 0;
	}

	public boolean hasObjects()
	{
		return !this.objects.isEmpty();
	}

	@Override
	public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
	{
		this.sanitize();

		if (sender instanceof Player)
		{
			this.isPlayer = true;

			this.player = (Player) sender;
		}
		else if (this.enforcePlayer)
		{
			Locale.sendError(sender, "system.cmd_player_only");

			return true;
		}
		else
		{
			this.isPlayer = false;
		}

		this.sender = sender;
		this.command = command;
		this.label = label;

		SubCommand sub = null;

		List<String> entered = Lists.newArrayList(args);

		int size = args.length;

		if (this.subcommand && !entered.isEmpty())
		{
			sub:
			{
				Iterator<String> iter = entered.iterator();

				while (iter.hasNext())
				{
					sub = this.getSubcommand(iter.next().toLowerCase());

					if (sub == null) continue;

					iter.remove();

					break;
				}

				if (sub == null) break sub;

				size--;

				if (!sub.hasNode() || this.hasPermission(sub.getNode())) break sub;

				Locale.sendError(this.sender, sub.getNodeErr());

				return true;
			}
		}

		this.args = entered.toArray(new String[size]);

		List<Argument> arguments = this.getArguments(sub);
		if (CollectionUtils.isNotEmpty(arguments))
		{
			for (Argument carg : arguments)
			{
				ArgStrategy<?> strat = carg.getStrategy();

				Object value = null;

				Iterator<String> iter = entered.iterator();

				int times = 0;
				while (iter.hasNext())
				{
					if (carg.hasCap() && carg.getCap() <= times++) break;

					Object tempvalue = strat.match(iter.next());

					if (tempvalue == null) continue;

					if (!carg.hasRemovalExclusion())
					{
						iter.remove();
					}

					value = tempvalue;

					break;
				}

				if (value != null)
				{
					if (carg.hasNode() && !this.hasPermission(carg.getNode()))
					{
						if (carg.hasDefault())
						{
							value = carg.isDefaultCallable() ? ((Supplier<Object>) carg.getDefault()).get() : carg.getDefault();
						}
						else
						{
							Locale.sendError(this.sender, carg.getNodeErr());

							return true;
						}
					}

					this.objects.put(carg.getPath(), value);

					continue;
				}

				else if (carg.hasDefault())
				{
					value = carg.getDefault();

					this.objects.put(carg.getPath(), carg.isDefaultCallable() ? ((Supplier<Object>) value).get() : carg.getDefault());

					continue;
				}

				if (!carg.isRequired()) continue;

				Locale.sendError(this.sender, carg.getUsage());

				return true;
			}
		}

		if (this.isPlayer)
		{
			Cooldown cooldown = this.cooldown;
			if (sub != null)
			{
				cooldown = Optional.ofNullable(sub.getCooldown()).orElse(cooldown);
			}

			if (cooldown != null)
			{
				if (cooldown.test(this.getUniqueId()).error(this.player)) return true;
			}
		}

		if ((this.indexUnused || (sub != null && sub.doesIndexUnusedArgs())) && !entered.isEmpty())
		{
			StringBuilder builder = new StringBuilder();

			for (String unused : entered)
			{
				builder.append(unused);

				builder.append(' ');
			}

			String builderString = builder.toString();

			this.unused = builderString.substring(0, builderString.length() - 1);
		}

		return sub == null ? this.dispatch() : sub.dispatch();
	}

}
