package com.ulfric.prison.commands;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.prison.modules.ModuleSell;

public class CommandSellhand extends SimpleCommand {

    public CommandSellhand() {
        this.withEnforcePlayer();
    }

    @Override
    public void run() {
        ModuleSell.get().sellitem(getPlayer(), getPlayer().getItemInHand());
    }

}
