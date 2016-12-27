package com.ulfric.lib.api.java;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.bukkit.command.CommandSender;

import com.ulfric.lib.api.collect.CollectModule;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.MathModule;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.reflect.ReflectModule;
import com.ulfric.lib.api.time.TimeModule;

public final class JavaModule extends SimpleModule {


	public JavaModule()
	{
		super("java", "Low-level java modules", "Packet", "1.0.0-REL");

		this.withSubModule(new AssertModule());
		this.withSubModule(new ReflectModule());
		this.withSubModule(new StringModule());
		this.withSubModule(new BooleansModule());
		this.withSubModule(new MathModule());
		this.withSubModule(new TimeModule());
		this.withSubModule(new CollectModule());
		this.withSubModule(new UuidsModule());
	}

	private static final class AssertModule extends SimpleModule {
		AssertModule()
		{
			super("assert", "Assertions module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Assert.impl = new Assert.IAssert() {
				@Override
				public void isTrue(boolean value)
				{
					Assert.isTrue(value, "Boolean must be true!");
				}

				@Override
				public <T> Assertion<T> of(T object)
				{
					return new Assertion<>(object, false);
				}

				@Override
				public <T> Assertion<T> ofNullable(T object)
				{
					return new Assertion<>(object, true);
				}

				@Override
				public void isTrue(boolean value, String message)
				{
					if (value) return;

					throw new IllegalArgumentException(message);
				}

				@Override
				public <T> T notNull(T object)
				{
					this.notNull(object, "Object must not be null!");

					return object;
				}

				@Override
				public <T> T notNull(T object, String message)
				{
					this.isFalse(object == null, message);

					return object;
				}

				@Override
				public void noneNull(Object... array)
				{
					this.notNull(array, "Array must not be null!");

					for (Object object : array)
					{
						this.notNull(object, "Array element must not be null!");
					}
				}

				@Override
				public void noneNull(Iterable<?> iterable)
				{
					this.notNull(iterable, "Iterable must not be null!");

					for (Object object : iterable)
					{
						this.notNull(object, "Iterable element must not be null!");
					}
				}

				@Override
				public <T> T isNull(T object)
				{
					this.isNull(object, "Object must be null!");

					return object;
				}

				@Override
				public <T> T isNull(T object, String message)
				{
					this.isTrue(object == null, message);

					return null;
				}

				@Override
				public void isFalse(boolean value)
				{
					this.isFalse(value, "Boolean must be false!");
				}

				@Override
				public void isFalse(boolean value, String message)
				{
					this.isTrue(!value, message);
				}

				@Override
				public void isNotEmpty(String string)
				{
					this.isNotEmpty(string, "String must not be null!", "String must not be empty!");
				}

				@Override
				public void isNotEmpty(String string, String nullMessage, String emptyMessage)
				{
					this.notNull(string, nullMessage);

					this.isFalse(string.isEmpty(), emptyMessage);
				}

				@Override
				public void isInstanceof(Class<?> clazz, Object object)
				{
					this.isInstanceof(clazz, object, "Object must be an instance of the class!");
				}

				@Override
				public void isInstanceof(Class<?> clazz, Object object, String message)
				{
					this.isTrue(clazz.isInstance(object), message);
				}

				@Override
				public void isNotInstanceof(Class<?> clazz, Object object)
				{
					this.isNotInstanceof(clazz, object, "Object must NOT be an instance of the class!");
				}

				@Override
				public void isNotInstanceof(Class<?> clazz, Object object, String message)
				{
					this.isFalse(clazz.isInstance(object), message);
				}
			};
		}

		@Override
		public void postDisable()
		{
			Assert.impl = Assert.IAssert.EMPTY;
		}
	}

	private static final class BooleansModule extends SimpleModule {
		BooleansModule()
		{
			super("booleans", "Primitive type boolean utilities module", "Packet", "1.0.0-REL");

			this.withSubModule(new PredicatesModule());
		}

		@Override
		public void postEnable()
		{
			Booleans.impl = new Booleans.IBooleans() {
				@Override
				public boolean parseBoolean(String string)
				{
					string = string.toLowerCase().trim();
					if (string.isEmpty()) return false;

					char first = string.charAt(0);

					return (first == 't' || first == 'a');
				}

				@Override
				public boolean isFalse(Object value)
				{
					return !((boolean) value);
				}

				@Override
				public boolean isTrue(Object value)
				{
					return (boolean) value;
				}

				@Override
				public String fancify(boolean value)
				{
					return Booleans.fancify(value, "enabled", "disabled");
				}

				@Override
				public String fancify(boolean value, CommandSender sender)
				{
					return Locale.getMessage(sender, value ? "system.enabled" : "system.disabled");
				}

				@Override
				public String fancify(boolean value, String good, String bad)
				{
					return value ? good : bad;
				}
			};
		}

		@Override
		public void postDisable()
		{
			Booleans.impl = Booleans.IBooleans.EMPTY;
		}
	}

	@SuppressWarnings("unchecked")
	private static final class PredicatesModule extends SimpleModule {
		PredicatesModule()
		{
			super("predicates", "Predicate utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Predicates.impl = new Predicates.IPredicates() {
				private final Predicate<?> alwaysTrue = val -> true;
				private final Predicate<?> alwaysFalse = val -> false;
				private final Predicate<?> alwaysRandom = val -> RandomUtils.nextBoolean();
				private final BiPredicate<?, ?> alwaysBiTrue = (val1, val2) -> true;
				private final BiPredicate<?, ?> alwaysBiFalse = (val1, val2) -> false;
				private final BiPredicate<?, ?> alwaysBiRandom = (val1, val2) -> RandomUtils.nextBoolean();

				@Override
				public <T> Predicate<T> always(boolean value)
				{
					return value ? this.alwaysTrue() : this.alwaysFalse();
				}

				@Override
				public <T> Predicate<T> alwaysTrue()
				{
					return (Predicate<T>) this.alwaysTrue;
				}

				@Override
				public <T> Predicate<T> alwaysFalse()
				{
					return (Predicate<T>) this.alwaysFalse;
				}

				@Override
				public <T> Predicate<T> alwaysRandom()
				{
					return (Predicate<T>) this.alwaysRandom;
				}

				@Override
				public <T, U> BiPredicate<T, U> alwaysBi(boolean value)
				{
					return value ? this.alwaysBiTrue() : this.alwaysBiFalse();
				}

				@Override
				public <T, U> BiPredicate<T, U> alwaysBiTrue()
				{
					return (BiPredicate<T, U>) this.alwaysBiTrue;
				}

				@Override
				public <T, U> BiPredicate<T, U> alwaysBiFalse()
				{
					return (BiPredicate<T, U>) this.alwaysBiFalse;
				}

				@Override
				public <T, U> BiPredicate<T, U> alwaysBiRandom()
				{
					return (BiPredicate<T, U>) this.alwaysBiRandom;
				}
			};
		}
	}

	private static final class StringModule extends SimpleModule {
		StringModule()
		{
			super("string", "String utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			StringUtils.impl = new StringUtils.IStringUtils() {
				private final DoubleMetaphone metaphone = new DoubleMetaphone();

				@Override
				public String acronym(String string)
				{
					if (org.apache.commons.lang3.StringUtils.isBlank(string)) return Strings.BLANK;

					String[] split = string.split("\\s+");

					StringBuilder builder = new StringBuilder();

					for (String part : split)
					{
						builder.append(part.substring(0, 1).toUpperCase());
					}

					return builder.toString();
				}

				@Override
				public String prepend(String message, String prefix)
				{
					if (org.apache.commons.lang3.StringUtils.isEmpty(prefix)) return message;

					return prefix + message;
				}

				@Override
				public boolean isSimilar(String str1, String str2)
				{
					return this.encodedDiff(str1, str2) >= 3;
				}

				@Override
				public int diff(String str1, String str2)
				{
					if (str1 == null)
					{
						if (str2 == null) return 0;

						return str2.length();
					}
					else if (str2 == null)
					{
						return str1.length();
					}

					int range = Math.min(str1.length(), str2.length());

					int diff = 0;

					for (int x = 0; x < range; x++)
					{
						if (str1.charAt(x) != str2.charAt(x)) continue;

						diff++;
					}

					return diff;
				}

				@Override
				public int encodedDiff(String str1, String str2)
				{
					return this.diff(this.metaphone.encode(str1), this.metaphone.encode(str2));
				}

				@Override
				public String merge(Iterable<? extends CharSequence> iterable, char seperator)
				{
					StringBuilder builder = new StringBuilder();

					for (CharSequence str : iterable)
					{
						builder.append(str);
						builder.append(seperator);
					}

					String string = builder.toString();

					return string.substring(0, string.length() - 1);
				}

				@Override
				public String mergeNicely(Collection<? extends CharSequence> collection)
				{
					int iters = collection.size();

					if (iters == 1)
					{
						return collection.iterator().next().toString();
					}

					StringBuilder builder = new StringBuilder();

					int x = 0;
					for (CharSequence entry : collection)
					{
						x++;

						boolean flag = x == iters;

						if (flag)
						{
							builder.append("and ");
						}

						builder.append(entry);

						if (flag) continue;

						builder.append(", ");
					}

					return builder.toString();
				}

				@Override
				public String mergeNamedNicely(Collection<? extends Named> collection)
				{
					return this.mergeNicely(collection.stream().map(Named::getName).collect(Collectors.toList()));
				}

				@Override
				public String findOption(String whole, String split)
				{
					if (!whole.contains(split)) return null;

					return whole.split(Pattern.quote(split + '.'))[1].split(" ")[0];
				}

				@Override
				public String findOption(String whole, String split, String defaultValue)
				{
					if (org.apache.commons.lang3.StringUtils.isEmpty(whole)
						|| org.apache.commons.lang3.StringUtils.isEmpty(split))
					{
						return defaultValue;
					}

					String value = this.findOption(whole, split);

					if (value != null) return value;

					return defaultValue;
				}

				@Override
				public String makeNumeric(String string)
				{
					return string.replaceAll("[^0-9|\\.]", Strings.BLANK);
				}

				@Override
				public String formatMoneyFully(double amount)
				{
					return StringUtils.formatMoneyFully(amount, false);
				}

				@Override
				public String formatMoneyFully(double amount, boolean tiny)
				{
					if (tiny)
					{
						return Strings.format("${0}{1}", Numbers.firstSection(amount), StringUtils.formatShortWordNumber(amount, true));
					}

					return Strings.format("${0}{1}", StringUtils.formatNumber(amount), amount >= 1000 ? Strings.format(" ({0}{1})", Numbers.firstSection(amount), StringUtils.formatShortWordNumber(amount)) : Strings.BLANK);
				}

				@Override
				public String formatMoney(double amount)
				{
					return '$' + StringUtils.formatNumber(amount);
				}

				@Override
				public String formatDecimal(Number amount)
				{
					return new DecimalFormat("#,###.##").format(amount);
				}

				@Override
				public String formatNumber(Number amount)
				{
					if (Double.compare(amount.doubleValue(), amount.longValue()) == 0)
					{
						return new DecimalFormat("#,##0").format(amount);
					}

					return new DecimalFormat("#,##0.00").format(amount);
				}

				@Override
				public String formatShortWordNumber(Number amount)
				{
					return StringUtils.formatShortWordNumber(amount, false);
				}

				@Override
				public String formatShortWordNumber(Number total, boolean tiny)
				{
					long amount = total.longValue();

					if (amount >= 1000000000000000L)
					{
						return tiny ? "q" : "quad";
					}
					else if (amount >= 1000000000000L)
					{
						return tiny ? "t" : "trill";
					}
					else if (amount >= 1000000000L)
					{
						return tiny ? "b" : "bil";
					}
					else if (amount >= 1000000L)
					{
						return tiny ? "m" : "mil";
					}
					else if (amount >= 1000L)
					{
						return "k";
					}

					return Strings.BLANK;
				}

				@Override
				public String formatIP(String address)
				{
					return address.replaceAll("[^\\d.]", Strings.BLANK);
				}

				@Override
				public String trimStart(String string, char... characters) {
					return string.replaceAll(
							"^[" + Stream.of(characters).map(String::valueOf).collect(Collectors.joining()) + "]+",
							""
					);
				}
			};
		}

		@Override
		public void postDisable()
		{
			StringUtils.impl = StringUtils.IStringUtils.EMPTY;
		}
	}

	private static final class UuidsModule extends SimpleModule {
		UuidsModule()
		{
			super("uuids", "UUID parsing utility", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Uuids.impl = new Uuids.IUuid() {
				@Override
				public UUID parse(String string)
				{
					if (org.apache.commons.lang3.StringUtils.isBlank(string)) return null;

					if (!string.contains("-"))
					{
						try
						{
							string = this.dashify(string);
						}
						catch (IndexOutOfBoundsException exception) { return null; }
					}

					if (string == null) return null;

					try
					{
						return UUID.fromString(string);
					}
					catch (IllegalArgumentException exception) { }

					return null;
				}

				@Override
				public String dashify(String string)
				{
					if (string.length() < 30) return null;

					StringBuilder builder = new StringBuilder();

					return builder.append(string, 0, 8).append('-').append(string, 8, 12).append('-').append(string, 12, 16).append('-').append(string, 16, 20).append('-').append(string, 20, 32).toString();
				}
			};
		}

		@Override
		public void postDisable()
		{
			Uuids.impl = Uuids.IUuid.EMPTY;
		}
	}

}
