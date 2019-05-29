package Commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class HelpCMD implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You need to be player to run this command dipshit"));
            return CommandResult.success();
        } Player player = (Player)src;


        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "create <ArenaName> - ", TextColors.AQUA, "Creates a new arena with the given name using the boundary set by the wand"));
        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "delete <ArenaName> - ", TextColors.AQUA, "Deletes arena with the given name"));
        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "wand - ", TextColors.AQUA, "Gives you the wand to set arena. Uses similar controls to world edit"));
        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "join - ", TextColors.AQUA, "Joins the current active block party"));
        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "start <ArenaName> - ", TextColors.AQUA, "Starts the block party in the given arena name"));
        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "list - ", TextColors.AQUA, "List of possible arenas"));


        return CommandResult.success();
    }
}
