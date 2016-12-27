package com.ulfric.control.coll;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.TimeUtils;

public class PunishmentExecutor {

	public static void execute(Punishment punishment)
	{
		PunishmentType type = punishment.getType();
		String reason = punishment.getReason();
		long expiry = punishment.timeTillExpiry();
		String sender = punishment.getSender().getName();
		PunishmentHolder holder = punishment.getHolder();

		if (type.getDisconnect())
		{
			if (type.equals(PunishmentType.KICK))
			{
				holder.kick(type.getMessage(), reason, sender);
			}
			else
			{
				holder.kick(type.getMessage(), punishment.getId(), reason, TimeUtils.millisecondsToString(expiry), sender);
			}
		}

		// Slack message init
		JsonObject object = new JsonObject();
		JsonObject attachment = new JsonObject();
		attachment.addProperty("color","#f60000");

		if (holder.isIp())
		{
			Locale.sendMassPerm("control.seeip", false, "control.punishmentcast", "<IP HIDDEN>", type.getPast(), sender, reason);
			Locale.sendMassPerm("control.seeip", true, "control.punishmentcast", holder.getName(), type.getPast(), sender, reason);
			// Slack message text
			object.addProperty("text", "<IP HIDDEN> has been "+type.getPast()+" by "+sender+"!");
			attachment.addProperty("text","Reason:\n"+reason+"\n\nExpiry:\n"+TimeUtils.millisecondsToString(expiry));
		}
		else
		{
			Locale.sendMass("control.punishmentcast", holder.getName(), type.getPast(), sender, reason);
			// Slack message text
			object.addProperty("text", holder.getName() + " has been "+type.getPast()+" by "+sender+"!");
			attachment.addProperty("text","Reason:\n"+reason+"\n\nExpiry:\n"+TimeUtils.millisecondsToString(expiry));
		}

		if (type.getPersist())
		{
			PunishmentCache.addPunishment(holder, punishment);
		}

		// Slack message close and send
		JsonArray attachmentArray = new JsonArray();
		attachmentArray.add(attachment);
		object.add("attachments",attachmentArray);

		if (expiry == 0) return;

		Locale.sendMass("control.expiry", TimeUtils.millisecondsToString(expiry));
	}

}