package com.gmail.trentech.pji.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.spongepowered.api.world.World;

import com.gmail.trentech.pji.Main;
import com.gmail.trentech.pji.sql.SQLSettings;
import com.gmail.trentech.pji.utils.Help;

public class CMDInfo implements CommandExecutor {

	public CMDInfo() {
		Help help = new Help("list", "list", " List all inventories");
		help.setSyntax(" /inventory list\n /inv l");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		for(World world : Sponge.getServer().getWorlds()) {
			Optional<String> optionalInv = SQLSettings.getWorld(world);
			
			if(optionalInv.isPresent()) {
				list.add(Text.of(TextColors.GREEN, world.getName(), " : ", TextColors.WHITE, " ", optionalInv.get()));
			} else {
				list.add(Text.of(TextColors.GREEN, world.getName(), " : ", TextColors.WHITE, " default"));
			}
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Inventories")).build());

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
