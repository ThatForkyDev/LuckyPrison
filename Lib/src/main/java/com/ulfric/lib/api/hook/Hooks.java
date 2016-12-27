package com.ulfric.lib.api.hook;

public final class Hooks {

	public static final WorldEditHook WORLDEDIT = new WorldEditHook();
	public static final PermissionsExHook PERMISSIONS = new PermissionsExHook();
	public static final XMapHook XMAP = new XMapHook();
	public static final ScriptHook SCRIPT = new ScriptHook();
	public static final DataHook DATA = new DataHook();
	public static final ActionsHook ACTIONS = new ActionsHook();
	public static final CombatTagHook COMBATTAG = new CombatTagHook();
	public static final RegionHook REGIONS = new RegionHook();
	public static final EconHook ECON = new EconHook();
	public static final EssHook ESS = new EssHook();
	public static final OpenInvHook OPENINV = new OpenInvHook();
	public static final PetsHook PETS = new PetsHook();
	public static final PrisonHook PRISON = new PrisonHook();
	public static final ControlHook CONTROL = new ControlHook();
	static IHooks impl = IHooks.EMPTY;

	private Hooks()
	{
	}

	public static void register(Hook<?> hook)
	{
		impl.register(hook);
	}

	public static void remove(Hook<?> hook)
	{
		impl.remove(hook);
	}

	public static Hook<?> forName(String name)
	{
		return impl.forName(name);
	}

	protected interface IHooks {
		IHooks EMPTY = new IHooks() {
		};

		default void register(Hook<?> hook)
		{
		}

		default void remove(Hook<?> hook)
		{
		}

		default Hook<?> forName(String name)
		{
			return null;
		}
	}

}
