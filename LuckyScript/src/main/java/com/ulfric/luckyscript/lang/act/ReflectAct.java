package com.ulfric.luckyscript.lang.act;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.reflect.Parser;
import com.ulfric.lib.api.reflect.Reflect;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class ReflectAct extends Action<Method> {

	public static final Pattern QUOTED_STRING = Pattern.compile("[\\\"](?i)[a-z0-9[\\s]]+[\\\"]");

	public ReflectAct(String context)
	{
		super(context);
	}

	private Object[] objects;

	@Override
	protected Method parse(String context)
	{
		String clazzName = Assert.notNull(StringUtils.findOption(context, "class"), "The class name must not be null!");

		Class<?> clazz = Assert.notNull(Reflect.getClass(clazzName), "The class was not found!");

		String methodName = Assert.notNull(StringUtils.findOption(context, "method"), "The method name must not be null!");

		Matcher matcher = ReflectAct.QUOTED_STRING.matcher(context);
		List<String> matches = Lists.newArrayListWithExpectedSize(matcher.groupCount());
		while (matcher.find())
		{
			String grp = matcher.group();
			matches.add(grp.substring(1, grp.length()-1));
		}

		Method method = null;

		for (Method lmethod : clazz.getMethods())
		{
			if (!lmethod.getName().equals(methodName)) continue;

			if (lmethod.getParameterCount() != matches.size()) continue;

			method = lmethod;

			break;
		}
		method = Assert.notNull(method, "The method was not found!");

		if (method.getParameterCount() > 0)
		{
			Class<?>[] params = method.getParameterTypes();

			this.objects = new Object[params.length];

			int x = 0;
			for (String string : matches)
			{
				this.objects[x] = Parser.parse(string, params[x++]);
			}
		}

		return method;
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		Reflect.tryInvokeMethod(this.getValue(), null, this.objects);
	}

}