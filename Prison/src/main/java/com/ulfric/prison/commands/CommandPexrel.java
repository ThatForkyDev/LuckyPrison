package com.ulfric.prison.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.reflect.Reflect;

public class CommandPexrel extends SimpleCommand {

	@Override
	public void run()
	{
		Field field = Reflect.getPrivateField(Bukkit.getPluginManager(), "permissions");

		field.setAccessible(true);

		Reflect.trySet(field, Bukkit.getPluginManager(), Maps.newHashMap());

		Method method = Reflect.getMethod(Bukkit.getServer().getClass(), "loadCustomPermissions");

		method.setAccessible(true);

		Reflect.tryInvokeMethod(method, Bukkit.getServer());

		for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
		{
			for (Permission permission : plugin.getDescription().getPermissions())
			{
				Bukkit.getPluginManager().addPermission(permission);
			}
		}
	}

}