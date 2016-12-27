package com.ulfric.lib.api.math;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Color;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Weighted;
import com.ulfric.lib.api.math.strategy.MathSolverModule;
import com.ulfric.lib.api.module.SimpleModule;

public final class MathModule extends SimpleModule {

	public MathModule()
	{
		super("math", "Math parent module", "Packet", "1.0.0-REL");

		this.withSubModule(new NumbersModule());
		this.withSubModule(new RandomModule());
		this.withSubModule(new MathSolverModule());
	}

	private final class NumbersModule extends SimpleModule {
		private NumbersModule()
		{
			super("numbers", "Number utilities module", "Packet", "1.0.0-REL");

			this.withSubModule(new NumberTranslatorModule());
		}

		@Override
		public void postEnable()
		{
			Numbers.impl = new Numbers.INumbers() {
				@Override
				public boolean isDouble(Number number)
				{
					return number.doubleValue() % 1 != 0;
				}

				@Override
				public long roundUp(long number, long multiple)
				{
					return ((number + (multiple - 1)) / multiple) * multiple;
				}

				@Override
				public double percentage(Number percent, Number total)
				{
					double returnValue = percent.doubleValue() / total.doubleValue() * 100;
					if (returnValue > 100) returnValue = 100;
					return returnValue;
				}

				@Override
				public String firstSection(Number number)
				{
					return this.sectionAt(number, 0);
				}

				@Override
				public String sectionAt(Number number, int row)
				{
					long longNumber = Math.abs(number.longValue());

					if (longNumber == 0) return "0";

					if (longNumber < 1000) return String.valueOf(longNumber);

					String[] parts = StringUtils.formatNumber(number).split(",");

					if (parts.length == 1) { return String.valueOf(longNumber); }

					else if (parts.length - 1 < row)
					{
						throw new NumberFormatException();
					}

					return parts[row];
				}

				@Override
				public Long parseLong(String number)
				{
					Number value = this.parseNumber(number);

					if (value == null) return null;

					return value.longValue();
				}

				@Override
				public Integer parseInteger(String number)
				{
					Number value = this.parseNumber(number);

					if (value == null) return null;

					return value.intValue();
				}

				@Override
				public Short parseShort(String number)
				{
					Number value = this.parseNumber(number);

					if (value == null) return null;

					return value.shortValue();
				}

				@Override
				public Byte parseByte(String number)
				{
					Number value = this.parseNumber(number);

					if (value == null) return null;

					return value.byteValue();
				}

				@Override
				public Float parseFloat(String number)
				{
					Number value = this.parseNumber(number);

					if (value == null) return null;

					return value.floatValue();
				}

				@Override
				public Double parseDouble(String number)
				{
					Number value = this.parseNumber(number);

					if (value == null) return null;

					return value.doubleValue();
				}

				@Override
				public Number parseNumber(String number)
				{
					if (number.contains("."))
					{
						try
						{
							return Double.parseDouble(number);
						}
						catch (NumberFormatException exception) { return null; }
					}

					try
					{
						return Long.parseLong(number);
					}
					catch (NumberFormatException exception) { return null; }
				}

				@Override
				public boolean isWithin(int i1, int i2, int max)
				{
					i1 = Math.abs(i1);
					i2 = Math.abs(i2);

					if (i1 == i2) return true;

					if (i1 > i2)
					{
						return (i1 - i2 >= max);
					}

					return (i2 - i1 >= max);
				}
			};
		}

		@Override
		public void postDisable()
		{
			Numbers.impl = Numbers.INumbers.EMPTY;
		}
	}

	private static final class NumberTranslatorModule extends SimpleModule {
		NumberTranslatorModule()
		{
			super("numbertranslator", "Number translator module", "Packet", "1.0.0-REL");

			this.withSubModule(new RomanNumeralModule());
		}

		@Override
		public void postEnable()
		{
			NumberTranslator.impl = new NumberTranslator.INumberTranslator() {
				@Override
				public RomanNumeral roman(int number)
				{
					return RomanNumeral.of(number);
				}

				@Override
				public RomanNumeral roman(String value)
				{
					return RomanNumeral.of(value);
				}
			};
		}

		@Override
		public void postDisable()
		{
			NumberTranslator.impl = NumberTranslator.INumberTranslator.EMPTY;
		}
	}

	private static final class RomanNumeralModule extends SimpleModule {
		RomanNumeralModule()
		{
			super("romannumeral", "RomanNumeral class module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			RomanNumeral.impl = new RomanNumeral.IRomanNumeral() {
				private final Map<Integer, RomanNumeral> values;

				{
					this.values = Maps.newTreeMap((o1, o2) -> o2.compareTo(o1));

					this.values.put(1000, this.of(1000, "M"));
					this.values.put(900, this.of(900, "CM"));
					this.values.put(500, this.of(500, "D"));
					this.values.put(400, this.of(400, "CD"));
					this.values.put(100, this.of(100, "C"));
					this.values.put(90, this.of(90, "XC"));
					this.values.put(50, this.of(50, "L"));
					this.values.put(40, this.of(40, "XL"));
					this.values.put(10, this.of(10, "X"));
					this.values.put(9, this.of(9, "IX"));
					this.values.put(5, this.of(5, "V"));
					this.values.put(4, this.of(4, "IV"));
					this.values.put(1, this.of(1, "I"));
				}

				private void asrt(int number)
				{
					Assert.isTrue(number <= 4000, "The roman numeral can be a max of 4,000!");
					Assert.isTrue(number > 0, "The roman numeral must be at least 1!");
				}

				private RomanNumeral of(int number, String value)
				{
					this.asrt(number);

					return new RomanNumeral(number, value);
				}

				@Override
				public RomanNumeral of(int number)
				{
					this.asrt(number);

					RomanNumeral roman = this.values.get(number);

					if (roman != null) return roman;

					int controller = number;

					StringBuilder builder = new StringBuilder();

					for (Map.Entry<Integer, RomanNumeral> numeral : this.values.entrySet())
					{
						int lnumber = numeral.getKey();

						while (controller >= lnumber)
						{
							builder.append(numeral.getValue());

							controller -= lnumber;
						}
					}

					return new RomanNumeral(number, builder.toString());
				}

				@Override
				public RomanNumeral of(String value)
				{
					Assert.isNotEmpty(value);

					value = value.trim();

					Integer integer = Numbers.parseInteger(value);

					if (integer != null) return this.of(integer);

					value = value.toUpperCase();

					int number = 0;
					while (!value.isEmpty())
					{
						for (Map.Entry<Integer, RomanNumeral> numeral : this.values.entrySet())
						{
							if (!value.startsWith(numeral.getKey().toString())) continue;

							number += numeral.getValue().intValue();
							value = value.substring(numeral.getKey().toString().length(), value.length());
						}
					}

					return new RomanNumeral(number, value);
				}
			};
		}

		@Override
		public void postDisable()
		{
			RomanNumeral.impl = RomanNumeral.IRomanNumeral.EMPTY;
		}
	}

	private static final class RandomModule extends SimpleModule {
		private RandomModule()
		{
			super("random", "Random utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			RandomUtils.impl = new RandomUtils.IRandomUtils() {
				private Random random = new Random();

				@Override
				public Random getRandom()
				{
					return this.random;
				}

				@Override
				public Random reseedRandom()
				{
					this.random = new Random();
					return this.random;
				}

				@Override
				public boolean nextBoolean()
				{
					return this.random.nextBoolean();
				}

				@Override
				public double nextDouble()
				{
					return this.random.nextDouble();
				}

				@Override
				public int nextInt(int control)
				{
					return this.random.nextInt(control);
				}

				@Override
				public int nextIntP1(int control)
				{
					return this.nextInt(control) + 1;
				}

				@Override
				public double nextDouble(double control)
				{
					return this.randomRange(0, control);
				}

				@Override
				public int randomRange(int min, int max)
				{
					if (min == max) return max;

					return this.nextInt((max - min) + 1) + min;
				}

				@Override
				public double randomRange(double min, double max)
				{
					if (Double.compare(min, max) == 0) return max;

					return min + this.nextDouble() * (max - min);
				}

				@Override
				public boolean randomPercentage(double chance)
				{
					return chance >= this.nextDouble() * 100;
				}

				@Override
				public <T> T randomValueFromList(List<T> list)
				{
					return list.get(this.nextInt(list.size()));
				}

				@Override
				public <K, V> Map.Entry<K, V> randomEntryFromMap(Map<K, V> map)
				{
					int random = this.nextInt(map.size());

					int x = 0;
					for (Map.Entry<K, V> entry : map.entrySet())
					{
						if (x++ < random) continue;

						return entry;
					}

					return null;
				}

				@Override
				public <T> T randomValueFromCollection(Collection<T> collection)
				{
					int random = this.nextInt(collection.size());

					int x = 0;

					for (T t : collection)
					{
						if (x++ < random) continue;

						return t;
					}

					return null;
				}

				@Override
				public Color randomColor()
				{
					return Color.fromBGR(this.nextInt(255), this.nextInt(255), this.nextInt(255));
				}

				@Override
				public <E extends Weighted> E getWeighted(Collection<E> collection)
				{
					return this.getWeightedFromWeight(collection, RandomUtils.randomRange(1, CollectUtils.getTotalWeight(collection)));
				}

				@Override
				public <E extends Weighted> E getWeighted(Collection<E> collection, int weight)
				{
					return this.getWeightedFromWeight(collection, this.randomRange(1, weight), false);
				}

				@Override
				public <E extends Weighted> E getWeightedFromWeight(Collection<E> collection, int weight, boolean shuffle)
				{
					if (shuffle && collection instanceof List)
					{
						//noinspection unchecked
						Collections.shuffle((List<? extends Weighted>) collection);
					}

					return this.getWeightedFromWeight(collection, weight);
				}

				@Override
				public <E extends Weighted> E getWeightedFromWeight(Collection<E> collection, int weight)
				{
					int rot = 0;

					for (E weighted : collection)
					{
						rot += weighted.getWeight();

						if (rot < weight) continue;

						return weighted;
					}

					return null;
				}
			};
		}

		@Override
		public void postDisable()
		{
			RandomUtils.impl = RandomUtils.IRandomUtils.EMPTY;
		}
	}


}
