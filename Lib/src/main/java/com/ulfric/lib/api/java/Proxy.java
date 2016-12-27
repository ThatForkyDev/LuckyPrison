package com.ulfric.lib.api.java;

import com.ulfric.lib.api.reflect.Reflect;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Proxy<T> extends Supplier<T> {

	default void consume(Consumer<? super T> consumer)
	{
		T t = this.get();
		if (t == null) return;

		consumer.accept(t);
	}

	default void consumeClone(Consumer<? super T> consumer)
	{
		T t = this.get();
		if (t == null) return;

		consumer.accept(Reflect.clone(t));
	}

	default <R> R function(Function<T, R> function)
	{
		T t = this.get();
		if (t == null) return null;

		return function.apply(t);
	}

	default boolean isPresent()
	{
		return this.get() != null;
	}

}
