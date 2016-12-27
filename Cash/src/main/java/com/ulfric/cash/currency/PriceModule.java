package com.ulfric.cash.currency;

import com.ulfric.cash.currency.Price.IPrice;
import com.ulfric.cash.currency.dollar.MoneyPrice;
import com.ulfric.cash.currency.token.TokenPrice;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.module.SimpleModule;

public class PriceModule extends SimpleModule {

	public PriceModule()
	{
		super("price", "Price API module", "Packet", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		Price.impl = new IPrice()
		{
			@Override
			public Price money(Number price)
			{
				return new MoneyPrice(price.doubleValue());
			}

			@Override
			public Price token(Number price)
			{
				return new TokenPrice(price.intValue());
			}

			@Override
			public Price of(String price)
			{
				price = price.toLowerCase();

				String numeric = StringUtils.makeNumeric(price);

				if (numeric.isEmpty()) return null;

				Number number = Numbers.parseNumber(numeric);

				if (number == null) return null; // should never happen due to regex

				if (number.longValue() <= 0) return null;

				// TODO Create a config for these

				if (price.contains("k") || price.contains("thousand"))
				{
					return new MoneyPrice(number.doubleValue() * 1000D);
				}

				if (price.contains("mil"))
				{
					return new MoneyPrice(number.doubleValue() * 1000000D);
				}

				if (price.contains("bil"))
				{
					return new MoneyPrice(number.doubleValue() * 1000000000D);
				}

				if (price.contains("tril"))
				{
					return new MoneyPrice(number.doubleValue() * 1000000000000D);
				}

				if (price.contains("t"))
				{
					return new TokenPrice(number.intValue());
				}

				// Parse scientific notation
				if (price.contains("e")) e:
				{
					String[] parts = price.split("e");

					if (parts.length < 2) break e;

					Integer part1Integer = Numbers.parseInteger(parts[0]);
					Integer part2Integer = Numbers.parseInteger(parts[1]);

					int part1 = part1Integer == null ? 1 : part1Integer;
					int part2 = part2Integer == null ? 1 : part2Integer;

					if (part1 <= 0 || part2 <= 0) return null;

					return new MoneyPrice(part1 * Math.pow(10, part2));
				}

				return new MoneyPrice(number.doubleValue());
			}
		};
	}

}