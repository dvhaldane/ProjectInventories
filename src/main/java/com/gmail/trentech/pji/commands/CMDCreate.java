package com.gmail.trentech.pji.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pji.service.InventoryService;
import com.gmail.trentech.pji.service.settings.InventorySettings;

public class CMDCreate implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String> getOne("inv").get().toUpperCase();

		InventorySettings inventorySettings = Sponge.getServiceManager().provideUnchecked(InventoryService.class).getInventorySettings();
		
		if (inventorySettings.exists(name)) {
			throw new CommandException(Text.of(TextColors.RED, name, " already exists"), false);
		}

		inventorySettings.create(name);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Created inventory ", name));

		return CommandResult.success();
	}

}
