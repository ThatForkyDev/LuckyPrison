package com.ulfric.lib.api.reflect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.ulfric.lib.api.inventory.*;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.math.strategy.MathStrategy;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public final class ReflectModule extends SimpleModule {

	public ReflectModule()
	{
		super("reflect", "Reflection utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new ParserModule());
	}

	@Override
	public void postEnable()
	{
		Reflect.impl = new Reflect.IReflect() {
			@Override
			public boolean trySet(String field, Object object, Object value)
			{
				return this.trySet(Reflect.getPrivateField(object, field), object, value);
			}

			@Override
			public <T extends Enum<T>> T valueOf(Class<T> clazz, String name)
			{
				try
				{
					return Enum.valueOf(clazz, name.toUpperCase());
				}
				catch (IllegalArgumentException exception) { return null; }
			}

			@Override
			public boolean trySet(Field field, Object object, Object value)
			{
				boolean flag = false;

				if (!field.isAccessible())
				{
					field.setAccessible(true);

					flag = true;
				}

				boolean returnValue = true;
				try
				{
					field.set(object, value);
				}
				catch (IllegalArgumentException | IllegalAccessException exception) { returnValue = false; }

				if (flag)
				{
					field.setAccessible(false);
				}

				return returnValue;
			}

			@Override
			public <T> T newInstance(Class<? extends T> clazz)
			{
				try
				{
					return clazz.getConstructor().newInstance();
				}
				catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException ex)
				{
					return null;
				}
			}

			@Override
			public <T> T newInstance(Constructor<T> constructor, Object... arguments)
			{
				try
				{
					return constructor.newInstance(arguments);
				}
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception)
				{
					exception.printStackTrace();
					return null;
				}
			}

			@Override
			public <T> Constructor<T> getConstructor(Class<?> clazz, Class<?>... arguments)
			{
				try
				{
					return (Constructor<T>) clazz.getDeclaredConstructor(arguments);
				}
				catch (NoSuchMethodException | SecurityException exception) { return null; }
			}

			@Override
			public Field getPrivateField(Object object, String fieldName)
			{
				return this.getPrivateField(object.getClass(), fieldName);
			}

			@Override
			public Field getPrivateField(Class<?> clazz, String fieldName)
			{
				try
				{
					return clazz.getDeclaredField(fieldName);
				}
				catch (NoSuchFieldException exception) { return null; }
			}

			@Override
			public <T> T getPrivateVariable(Object object, String fieldName)
			{
				try
				{
					Field field = this.getPrivateField(object, fieldName);

					field.setAccessible(true);

					return (T) field.get(object);
				}
				catch (IllegalAccessException exception) { return null; }
			}

			@Override
			public Class<?> getClass(String className)
			{
				try
				{
					return Class.forName(className);
				}
				catch (ClassNotFoundException exception) { return null; }
			}

			@Override
			public Method getMethod(Class<?> clazz, String name)
			{
				try
				{
					return clazz.getDeclaredMethod(name);
				}
				catch (NoSuchMethodException | SecurityException exception) { return null; }
			}

			@Override
			public Method getMethod(Class<?> clazz, String name, Class<?>... arguments)
			{
				try
				{
					return clazz.getMethod(name, arguments);
				}
				catch (NoSuchMethodException | SecurityException exception) { return null; }
			}

			@Override
			public Object invokeMethod(Method method, Object object, Object... arguments)
			{
				try
				{
					return method.invoke(object, arguments);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception)
				{
					return null;
				}
			}

			@Override
			public boolean tryInvokeMethod(Method method, Object object, Object... arguments)
			{
				try
				{
					method.invoke(object, arguments);

					return true;
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception)
				{
					return false;
				}
			}

			@Override
			public <T extends Annotation> Collection<Method> getAnnotatedMethods(Object object, Class<T> annotationType)
			{
				Method[] methods = object.getClass().getDeclaredMethods();

				if (ArrayUtils.isEmpty(methods)) return ImmutableList.of();

				return Arrays.stream(methods).filter(method -> method.getAnnotation(annotationType) != null).collect(Collectors.toList());
			}
		};
	}

	private static final class ParserModule extends SimpleModule {
		ParserModule()
		{
			super("parser", "String -> Object conversion module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Parser.impl = new Parser.IParser() {
				private final Map<Class<?>, Method> parsers;

				{
					this.parsers = Maps.newHashMap();

					this.register(String.class, Strings.class, "fromString");
					this.register(Boolean.class, Booleans.class, "parseBoolean");
					this.register(Number.class, Numbers.class, "parseNumber");
					this.register(Long.class, Numbers.class, "parseLong");
					this.register(Integer.class, Numbers.class, "parseInteger");
					this.register(Short.class, Numbers.class, "parseShort");
					this.register(Byte.class, Numbers.class, "parseByte");
					this.register(Double.class, Numbers.class, "parseDouble");
					this.register(Float.class, Numbers.class, "parseFloat");
					this.register(ItemStack.class, ItemUtils.class, "fromString");
					this.register(ItemPair.class, MaterialUtils.class, "pair");
					this.register(Enchant.class, EnchantUtils.class, "parse");
					this.register(Player.class, PlayerUtils.class, "getOnline");
					this.register(OfflinePlayer.class, PlayerUtils.class, "getOffline");
					this.register(MathStrategy.class, MathStrategy.class, "parse");
				}

				@Override
				public <T> T parse(String string, Class<? extends T> clazz)
				{
					Method method = this.parsers.get(clazz);

					if (method == null)
					{
						Constructor<? extends T> constr = Reflect.getConstructor(clazz, String.class);

						if (constr == null) return Reflect.newInstance(clazz);

						return Reflect.newInstance(constr, string);
					}

					return (T) Reflect.invokeMethod(method, null, string);
				}

				@Override
				public void register(Class<?> value, Class<?> parser, String method)
				{
					Method methodObj = Reflect.getMethod(parser, method, String.class);

					if (methodObj == null)
					{
						ParserModule.this.log("Missing parser: {0}::{1}", parser.getName(), method);

						return;
					}

					this.parsers.put(value, methodObj);
				}
			};
		}

		@Override
		public void postDisable()
		{
			Parser.impl = Parser.IParser.EMPTY;
		}
	}

	@Override
	public void postDisable()
	{
		Reflect.impl = Reflect.IReflect.EMPTY;
	}


}
