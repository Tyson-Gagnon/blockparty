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
import java.util.List;
import java.util.stream.Collectors;

public class delete implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You need to be player to run this command dipshit"));
            return CommandResult.success();
        }
        Player player = (Player)src;

        String arenaName = args.<String>getOne("ArenaName").get();
        arenaName = arenaName.toUpperCase();
        ConfigurationNode node = MainClass.getInstance().getNode();

        if((!node.getNode("Arenas",arenaName).isVirtual())){
            node.getNode("Arenas").removeChild(arenaName);
            List<String> arenaList = node.getNode("Arenas", "ArenasList").getChildrenList().stream()
                    .map(ConfigurationNode::getString).collect(Collectors.toList());

            arenaList.remove(arenaName);
            node.getNode("Arenas","ArenasList").setValue(arenaList);

            player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.AQUA,"Arena ", arenaName ," deleted"));
        }else{
            player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED,"Arena not found!"));
        }

        try {
            MainClass.getInstance().getConfigManager().save(node);
        }catch (IOException e){
            e.printStackTrace();
        }


        return CommandResult.success();
    }
}
