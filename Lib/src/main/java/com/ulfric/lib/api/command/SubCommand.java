package com.ulfric.lib.api.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class SubCommand implements ICommand, ExecutableCommand {

	private final Command command;
	private final String name;
	private final String[] aliases;
	private String node;
	private String nodeErr;
	private boolean enforcePlayer;
	private boolean indexUnused;
	private Cooldown cooldown;

	protected SubCommand(Command command, String name, String... aliases)
	{
		this(command, name, null, aliases);
	}

	protected SubCommand(Command command, String name, String node, String... aliases)
	{
		this(command, name, node, node == null ? null : "system.no_permission", aliases);
	}

	protected SubCommand(Command command, String name, String node, String nodeErr, String... aliases)
	{
		this.command = command;
		this.name = name;
		this.node = node;
		this.nodeErr = nodeErr;
		this.aliases = Arrays.copyOf(aliases, aliases.length);
	}

	public Command getOwner()
	{
		return this.command;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public String[] getAliases()
	{
		return this.aliases;
	}

	public String getNode()
	{
		return this.node;
	}

	public boolean hasNode()
	{
		return this.node != null;
	}

	public String getNodeErr()
	{
		return this.nodeErr;
	}

	public void withNode(String node)
	{
		this.withNode(node, "system.no_permission");
	}

	public void withNode(String node, String nodeErr)
	{
		this.node = node;

		this.nodeErr = nodeErr;
	}

	@Override
	public UUID getUniqueId()
	{
		return this.command.getUniqueId();
	}

	@Override
	public Location getLocation()
	{
		return this.command.getLocation();
	}

	public boolean canUse()
	{
		if (!this.isPlayer()) return !this.enforcePlayer;

		return this.cooldown == null || !this.cooldown.test(this.getUniqueId()).error(this.getPlayer());

	}

	@Override
	public boolean dispatch()
	{
		return !this.canUse() || this.execute();

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
		return true;
	}

	@Override
	public void withArgument(Argument argument)
	{
		this.command.withArgument(this, argument);
	}

	@Override
	public List<Argument> getArguments()
	{
		return this.command.getArguments(this);
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
		return this.command.getUnusedArgs();
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
		return this.command.getSender();
	}

	@Override
	public Player getPlayer()
	{
		return this.command.getPlayer();
	}

	@Override
	public boolean isPlayer()
	{
		return this.command.isPlayer();
	}

	@Override
	public org.bukkit.command.Command getCommand()
	{
		return this.command.getCommand();
	}

	@Override
	public String getLabel()
	{
		return this.command.getLabel();
	}

	@Override
	public String[] getArgs()
	{
		return this.command.getArgs();
	}

	@Override
	public String getArg(int index)
	{
		return this.command.getArg(index + 1);
	}

	@Override
	public Object getObject(String path)
	{
		return this.command.getObject(path);
	}

}
