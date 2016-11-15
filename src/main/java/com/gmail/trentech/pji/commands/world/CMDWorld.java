package com.gmail.trentech.pji.commands.world;

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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.help.Help;

public class CMDWorld implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (Sponge.getPluginManager().isLoaded("helpme")) {
			Help.executeList(src, Help.get("inventory world").get().getChildren());
			
			return CommandResult.success();
		}

		List<Text> list = new ArrayList<>();

		if (src.hasPermission("pji.cmd.inventory.world.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pji:inventory world list")).append(Text.of(" /inventory world list")).build());
		}
		if (src.hasPermission("pji.cmd.inventory.world.add")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pji:inventory world add")).append(Text.of(" /inventory world add")).build());
		}
		if (src.hasPermission("pji.cmd.inventory.world.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pji:inventory world remove")).append(Text.of(" /inventory world remove")).build());
		}
		
		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}

}