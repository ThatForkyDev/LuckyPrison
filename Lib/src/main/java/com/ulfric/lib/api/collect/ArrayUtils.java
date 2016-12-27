package com.ulfric.lib.api.collect;

public final class ArrayUtils {

	static IArrayUtils impl = IArrayUtils.EMPTY;

	private ArrayUtils()
	{
	}

	public static String mergeToString(String[] split, int i)
	{
		return impl.mergeToString(split, i);
	}

	protected interface IArrayUtils {
		IArrayUtils EMPTY = new IArrayUtils() {
		};

		default String mergeToString(String[] split, int i)
		{
			return null;
		}
	}

}
