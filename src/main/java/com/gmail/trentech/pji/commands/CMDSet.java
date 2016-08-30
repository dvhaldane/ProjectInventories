package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pji.sql.SQLSettings;
import com.gmail.trentech.pji.utils.Help;

public class CMDSet implements CommandExecutor {

	public CMDSet() {
		Help help = new Help("set", "set", " Set an inventory for the specified world");
		help.setSyntax(" /inventory set <world> <inventory>\n /inv s <world> <inventory>");
		help.setExample(" /inventory set DIM-1 nether");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();
		
		if (!args.hasAny("inv")) {
			List<Text> list = new ArrayList<>();

			list.add(Text.of(TextColors.GREEN, "Current Inventory: ", TextColors.WHITE, SQLSettings.getWorld(properties).get()));
			list.add(Text.of(TextColors.GREEN, "Command: ", TextColors.YELLOW, "/inventory set <world> <inventory>"));

			if (src instanceof Player) {
				PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Inventory")).build());

				pages.contents(list);

				pages.sendTo(src);
			} else {
				for (Text text : list) {
					src.sendMessage(text);
				}
			}

			return CommandResult.empty();
		}
		String name = args.<String> getOne("inv").get();

		if (!SQLSettings.getInventory(name) && !name.equalsIgnoreCase("default")) {
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " does not exist"));
			return CommandResult.empty();
		}
		
		SQLSettings.updateWorld(properties, SQLSettings.getWorld(properties).get(), name);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set inventory for ", properties.getWorldName(), " to ", name));

		return CommandResult.success();
	}
}
