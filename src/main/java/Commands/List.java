package Commands;

import Main.MainClass;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.util.stream.Collectors;

public class List implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You need to be player to run this command dipshit"));
            return CommandResult.success();
        }
        Player player = (Player)src;

        ConfigurationNode node = MainClass.getInstance().getNode();

        java.util.List<String> arenaList = node.getNode("Arenas", "ArenasList").getChildrenList().stream()
                .map(ConfigurationNode::getString).collect(Collectors.toList());

        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE,"[Block Party] " , TextColors.AQUA, "List of arenas include:"));
        for (int i = 0; i < arenaList.size();i++){
            player.sendMessage(Text.of(TextColors.AQUA,arenaList.get(i)));
        }



        return CommandResult.success();
    }
}
