package com.ulfric.prison.commands;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.prison.lang.Meta;
import com.ulfric.prison.lang.Permissions;
import com.ulfric.prison.modules.ModuleSell;

public class CommandBonus extends SimpleCommand {

	public CommandBonus()
	{
		this.withArgument("player", ArgStrategy.OFFLINE_PLAYER, Permissions.GIVE_BONUSES, "prison.bonus_noperm_others");

		this.withArgument("amt", ArgStrategy.DOUBLE, Permissions.GIVE_BONUSES, "prison.bonus_noperm_others");
	}

	@Override
	public void run()
	{
		OfflinePlayer target = (OfflinePlayer) this.getObject("player");
		Double amt = (Double) this.getObject("amt");

		if (target == null && amt == null)
		{
			if (!this.isPlayer())
			{
				Locale.sendError(this.getSender(), "prison.bonus_console");

				return;
			}

			double current = Hooks.DATA.getPlayerDataAsDouble(this.getUniqueId(), Meta.SELL_BONUS);

			Group donor = Hooks.PERMISSIONS.user(this.getUniqueId()).getRankLadderGroup("premium");

			if (donor != null)
			{
				current += ModuleSell.get().getMultiplier(donor);
			}

			if (current <= 0)
			{
				Locale.send(this.getPlayer(), "prison.bonus_none");

				return;
			}

			Locale.send(this.getPlayer(), "prison.bonus_current", current);

			return;
		}

		if (target == null)
		{
			target = this.getPlayer();
		}

		if (amt == null)
		{
			Hooks.DATA.removePlayerData(target.getUniqueId(), Meta.SELL_BONUS);

			Locale.sendSuccess(this.getSender(), "prison.bonus_del", target.getName());

			return;
		}

		UUID uuid = target.getUniqueId();

		Hooks.DATA.setPlayerData(uuid, Meta.SELL_BONUS, Hooks.DATA.getPlayerDataAsInt(uuid, Meta.SELL_BONUS) + amt);

		Locale.sendSuccess(this.getSender(), "prison.bonus_made", amt, target.getName());
	}

}