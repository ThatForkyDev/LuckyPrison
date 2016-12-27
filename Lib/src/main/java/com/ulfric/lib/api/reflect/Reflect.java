package com.ulfric.lib.api.reflect;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public final class Reflect {

	static IReflect impl = IReflect.EMPTY;

	private Reflect()
	{
	}

	public static <T> T clone(T t)
	{
		if (t == null) return null;

		if (t instanceof Cloneable)
		{
			@SuppressWarnings("unchecked")
			T value = (T) invokeMethod(getMethod(t.getClass(), "clone"), t);

			return value;
		}

		if (t instanceof Serializable)
		{
			@SuppressWarnings("unchecked")
			T value = (T) SerializationUtils.clone((Serializable) t);

			return value;
		}

		return null;
	}

	public static <T extends Enum<T>> T valueOf(Class<T> clazz, String name)
	{
		return impl.valueOf(clazz, name);
	}

	public static boolean trySet(String field, Object object, Object value)
	{
		return impl.trySet(field, object, value);
	}

	public static boolean trySet(Field field, Object object, Object value)
	{
		return impl.trySet(field, object, value);
	}

	public static <T> T newInstance(Class<? extends T> clazz)
	{
		return impl.newInstance(clazz);
	}

	public static <T> T newInstance(Constructor<T> constructor, Object... arguments)
	{
		return impl.newInstance(constructor, arguments);
	}

	public static <T> Constructor<T> getConstructor(Class<?> clazz, Class<?>... arguments)
	{
		return impl.getConstructor(clazz, arguments);
	}

	public static Field getPrivateField(Object object, String fieldName)
	{
		return impl.getPrivateField(object, fieldName);
	}

	public static Field getPrivateField(Class<?> clazz, String fieldName)
	{
		return impl.getPrivateField(clazz, fieldName);
	}

	public static <T> T getPrivateVariable(Object object, String fieldName)
	{
		return impl.getPrivateVariable(object, fieldName);
	}

	public static Class<?> getClass(String className)
	{
		return impl.getClass(className);
	}

	public static Method getMethod(Class<?> clazz, String name)
	{
		return impl.getMethod(clazz, name);
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... arguments)
	{
		return impl.getMethod(clazz, name, arguments);
	}

	public static Object invokeMethod(Method method, Object object, Object... arguments)
	{
		return impl.invokeMethod(method, object, arguments);
	}

	public static boolean tryInvokeMethod(Method method, Object object, Object... arguments)
	{
		return impl.tryInvokeMethod(method, object, arguments);
	}

	public static <T extends Annotation> Collection<Method> getAnnotatedMethods(Object object, Class<T> annotationType)
	{
		return impl.getAnnotatedMethods(object, annotationType);
	}

	protected interface IReflect {
		IReflect EMPTY = new IReflect() {
		};

		default boolean trySet(String field, Object object, Object value)
		{
			return false;
		}

		default <T extends Enum<T>> T valueOf(Class<T> clazz, String name)
		{
			return null;
		}

		default boolean trySet(Field field, Object object, Object value)
		{
			return false;
		}

		default <T> T newInstance(Class<? extends T> clazz)
		{
			return null;
		}

		default <T> T newInstance(Constructor<T> constructor, Object... arguments)
		{
			return null;
		}

		default <T> Constructor<T> getConstructor(Class<?> clazz, Class<?>... arguments)
		{
			return null;
		}

		default Field getPrivateField(Object object, String fieldName)
		{
			return null;
		}

		default Field getPrivateField(Class<?> clazz, String fieldName)
		{
			return null;
		}

		default <T> T getPrivateVariable(Object object, String fieldName)
		{
			return null;
		}

		default Class<?> getClass(String className)
		{
			return null;
		}

		default Method getMethod(Class<?> clazz, String name)
		{
			return null;
		}

		default Method getMethod(Class<?> clazz, String name, Class<?>... arguments)
		{
			return null;
		}

		default Object invokeMethod(Method method, Object object, Object... arguments)
		{
			return null;
		}

		default boolean tryInvokeMethod(Method method, Object object, Object... arguments)
		{
			return false;
		}

		default <T extends Annotation> Collection<Method> getAnnotatedMethods(Object object, Class<T> annotationType)
		{
			return ImmutableList.of();
		}
	}

}
