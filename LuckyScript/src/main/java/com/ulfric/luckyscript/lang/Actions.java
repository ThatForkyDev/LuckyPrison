package com.ulfric.luckyscript.lang;

import java.lang.reflect.Constructor;
import java.util.Map;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.reflect.Reflect;
import com.ulfric.luckyscript.lang.act.Action;
import com.ulfric.luckyscript.lang.act.AfterAct;
import com.ulfric.luckyscript.lang.act.AsyncAct;
import com.ulfric.luckyscript.lang.act.BlockiterAct;
import com.ulfric.luckyscript.lang.act.BreakAct;
import com.ulfric.luckyscript.lang.act.CommandAct;
import com.ulfric.luckyscript.lang.act.DamageAct;
import com.ulfric.luckyscript.lang.act.DropAct;
import com.ulfric.luckyscript.lang.act.EffectAct;
import com.ulfric.luckyscript.lang.act.FlyAct;
import com.ulfric.luckyscript.lang.act.GiveAct;
import com.ulfric.luckyscript.lang.act.KillAct;
import com.ulfric.luckyscript.lang.act.LogAct;
import com.ulfric.luckyscript.lang.act.MetaAct;
import com.ulfric.luckyscript.lang.act.ParticleAct;
import com.ulfric.luckyscript.lang.act.ReflectAct;
import com.ulfric.luckyscript.lang.act.RepeatAct;
import com.ulfric.luckyscript.lang.act.RollAct;
import com.ulfric.luckyscript.lang.act.RunAct;
import com.ulfric.luckyscript.lang.act.SendAct;
import com.ulfric.luckyscript.lang.act.SleepAct;
import com.ulfric.luckyscript.lang.act.SoundAct;
import com.ulfric.luckyscript.lang.act.SpawnAct;
import com.ulfric.luckyscript.lang.act.SyncAct;
import com.ulfric.luckyscript.lang.act.TeleportAct;

class Actions {


	private static final Map<String, Constructor<? extends Action<?>>> ACTIONS = Maps.newHashMap();
	public static Constructor<? extends Action<?>> getAction(String name) { return Actions.ACTIONS.get(name); }

	static
	{
		Actions.ACTIONS.put("log!", Reflect.getConstructor(LogAct.class, String.class));
		Actions.ACTIONS.put("send!", Reflect.getConstructor(SendAct.class, String.class));
		Actions.ACTIONS.put("damage!", Reflect.getConstructor(DamageAct.class, String.class));
		Actions.ACTIONS.put("kill!", Reflect.getConstructor(KillAct.class, String.class));
		Actions.ACTIONS.put("give!", Reflect.getConstructor(GiveAct.class, String.class));
		Actions.ACTIONS.put("drop!", Reflect.getConstructor(DropAct.class, String.class));
		Actions.ACTIONS.put("teleport!", Reflect.getConstructor(TeleportAct.class, String.class));
		Actions.ACTIONS.put("spawn!", Reflect.getConstructor(SpawnAct.class, String.class));
		Actions.ACTIONS.put("effect!", Reflect.getConstructor(EffectAct.class, String.class));
		Actions.ACTIONS.put("particle!", Reflect.getConstructor(ParticleAct.class, String.class));
		Actions.ACTIONS.put("sound!", Reflect.getConstructor(SoundAct.class, String.class));
		Actions.ACTIONS.put("meta!", Reflect.getConstructor(MetaAct.class, String.class));
		Actions.ACTIONS.put("repeat!", Reflect.getConstructor(RepeatAct.class, String.class));
		Actions.ACTIONS.put("after!", Reflect.getConstructor(AfterAct.class, String.class));
		Actions.ACTIONS.put("sleep!", Reflect.getConstructor(SleepAct.class, String.class));
		Actions.ACTIONS.put("async!", Reflect.getConstructor(AsyncAct.class, String.class));
		Actions.ACTIONS.put("sync!", Reflect.getConstructor(SyncAct.class, String.class));
		Actions.ACTIONS.put("reflect!", Reflect.getConstructor(ReflectAct.class, String.class));
		Actions.ACTIONS.put("run!", Reflect.getConstructor(RunAct.class, String.class));
		Actions.ACTIONS.put("command!", Reflect.getConstructor(CommandAct.class, String.class));
		Actions.ACTIONS.put("blockiter!", Reflect.getConstructor(BlockiterAct.class, String.class));
		Actions.ACTIONS.put("break!", Reflect.getConstructor(BreakAct.class, String.class));
		Actions.ACTIONS.put("roll!", Reflect.getConstructor(RollAct.class, String.class));
		Actions.ACTIONS.put("fly!", Reflect.getConstructor(FlyAct.class, String.class));
	}


}