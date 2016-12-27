package com.ulfric.ess.hook;

import com.ulfric.ess.entity.Kit.KitCooldown;
import com.ulfric.lib.api.hook.EssHook.IKitCooldown;
import com.ulfric.lib.api.hook.EssHook.IKitUsageType;

public class KitCooldownImpl implements IKitCooldown {

	protected KitCooldownImpl(KitCooldown kitCooldown)
	{
		this.kitCooldown = kitCooldown;
	}

	private final KitCooldown kitCooldown;

	@Override
	public IKitUsageType getType()
	{
		IKitUsageType type;

		switch (this.kitCooldown.getType())
		{
			case CANNOT_USE_COOLDOWN:
				type = IKitUsageType.CANNOT_USE_COOLDOWN;
				break;

			case CANNOT_USE_SINGULAR:
				type = IKitUsageType.CANNOT_USE_SINGULAR;
				break;

			case CAN_USE:
				type = IKitUsageType.CAN_USE;
				break;

			case CAN_USE_COOLDOWN:
				type = IKitUsageType.CAN_USE_COOLDOWN;
				break;

			default:
				throw new UnsupportedOperationException("Missing kit cooldown type: " + this.kitCooldown.getType());	
		}

		return type;
	}

	@Override
	public long getCooldown()
	{
		return this.kitCooldown.getCooldown();
	}

}
