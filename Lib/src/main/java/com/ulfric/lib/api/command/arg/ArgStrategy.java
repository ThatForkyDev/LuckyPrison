package com.ulfric.lib.api.command.arg;

@FunctionalInterface
public interface ArgStrategy<T> {

	LongArg LONG = new LongArg();
	IntegerArg INTEGER = new IntegerArg();
	PositiveIntegerArg POSITIVE_INTEGER = new PositiveIntegerArg();
	ShortArg SHORT = new ShortArg();
	ByteArg BYTE = new ByteArg();
	FloatArg FLOAT = new FloatArg();
	DoubleArg DOUBLE = new DoubleArg();
	BooleanArg BOOLEAN = new BooleanArg();
	StringArg STRING = new StringArg();
	EnteredStringArg ENTERED_STRING = new EnteredStringArg();
	ClassArg CLASS = new ClassArg();
	PlayerArg PLAYER = new PlayerArg();
	ExactPlayerArg EXACT_PLAYER = new ExactPlayerArg();
	OfflinePlayerArg OFFLINE_PLAYER = new OfflinePlayerArg();
	NeverPlayedPlayerArg NEVER_PLAYED_PLAYER = new NeverPlayedPlayerArg();
	EntityArg ENTITY = new EntityArg();
	MaterialArg MATERIAL = new MaterialArg();
	EnchantArg ENCHANT = new EnchantArg();
	GamemodeArg GAMEMODE = new GamemodeArg();
	WorldArg WORLD = new WorldArg();
	WeatherArg WEATHER = new WeatherArg();
	MctimeArg MC_TIME = new MctimeArg();
	FutureArg FUTURE = new FutureArg();
	InetArg INET = new InetArg();
	ModuleArg MODULE = new ModuleArg();

	T match(String string);

}
