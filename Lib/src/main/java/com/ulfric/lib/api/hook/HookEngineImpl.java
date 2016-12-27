package com.ulfric.lib.api.hook;

public interface HookEngineImpl extends HookImpl {

	default void buildEngine(boolean cache)
	{
	}

	default void clearEngine()
	{
	}

}
