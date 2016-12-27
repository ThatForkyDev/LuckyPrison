package com.ulfric.lib.api.command;

import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Unique;
import com.ulfric.uspigot.Locatable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

interface ICommand extends Unique, Named, Locatable {

	boolean dispatch();

	boolean isEnforcePlayer();

	void withEnforcePlayer();

	boolean isSubcommand();

	default void withArgument(String path, ArgStrategy<?> strategy)
	{
		this.withArgument(Argument.builder().withPath(path).withStrategy(strategy));
	}

	default void withArgument(String path, ArgStrategy<?> strategy, Object defaultValue)
	{
		this.withArgument(Argument.builder().withPath(path).withStrategy(strategy).withDefault(defaultValue));
	}

	default void withArgument(String path, ArgStrategy<?> strategy, Supplier<Object> defaultValue)
	{
		this.withArgument(Argument.builder().withPath(path).withStrategy(strategy).withDefaultCallable(defaultValue));
	}

	default void withArgument(String path, ArgStrategy<?> strategy, Object defaultValue, String node)
	{
		this.withArgument(Argument.builder().withPath(path).withStrategy(strategy).withDefault(defaultValue).withNode(node));
	}

	default void withArgument(String path, ArgStrategy<?> strategy, Supplier<Object> defaultValue, String node)
	{
		this.withArgument(Argument.builder().withPath(path).withStrategy(strategy).withDefaultCallable(defaultValue).withNode(node));
	}

	default void withArgument(String path, ArgStrategy<?> strategy, String usage)
	{
		this.withArgument(Argument.builder().withPath(path).withStrategy(strategy).withUsage(usage));
	}

	default void withArgument(String path, ArgStrategy<?> strategy, String usage, String node, String nodeErr)
	{
		this.withArgument(Argument.builder().withPath(path).withStrategy(strategy).withUsage(usage).withNode(node, nodeErr));
	}

	default void withArgument(String path, ArgStrategy<?> strategy, String node, String nodeErr)
	{
		this.withArgument(Argument.builder().withPath(path).withStrategy(strategy).withNode(node, nodeErr));
	}

	default void withArgument(Argument.Builder argument)
	{
		this.withArgument(argument.build());
	}

	void withArgument(Argument argument);

	List<Argument> getArguments();

	boolean doesIndexUnusedArgs();

	void withIndexUnusedArgs();

	String getUnusedArgs();

	Cooldown getCooldown();

	void withCooldown(Cooldown cooldown);

	default void withCooldown(Cooldown.Builder cooldown)
	{
		this.withCooldown(cooldown.build());
	}

	CommandSender getSender();

	Player getPlayer();

	default boolean hasPermission(String node)
	{
		return this.getSender().hasPermission(node);
	}

	default boolean isPlayer()
	{
		return this.getSender() instanceof Player;
	}

	org.bukkit.command.Command getCommand();

	String getLabel();

	String[] getArgs();

	String getArg(int index);

	Object getObject(String path);

	default boolean hasObject(String path)
	{
		return this.getObject(path) != null;
	}

}
