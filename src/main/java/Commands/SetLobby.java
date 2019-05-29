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
import org.spongepowered.api.world.Location;

import java.io.IOException;

public class SetLobby implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You need to be player to run this command dipshit"));
            return CommandResult.success();
        }
         Player player = (Player)src;

        String arenaName = args.<String>getOne("arenaName").get();
        ConfigurationNode node = MainClass.getInstance().getNode();

        if(!node.getNode("Arenas",arenaName).isVirtual()){

            Location location = player.getLocation();
            double posx,posy,posz;

            posx = location.getX();
            posy = location.getY();
            posz = location.getZ();

            node.getNode("Arenas",arenaName,"lobby","x").setValue(posx);
            node.getNode("Arenas",arenaName,"lobby","y").setValue(posy);
            node.getNode("Arenas",arenaName,"lobby","z").setValue(posz);

            player.sendMessage(Text.of(TextColors.LIGHT_PURPLE,"[Block Party] ", TextColors.AQUA, "Set lobby coords to " , posx , " ", posy , " " , posz));

            try {
                MainClass.getInstance().getConfigManager().save(node);
            }catch (IOException e){
                e.printStackTrace();
            }

        }else{
            player.sendMessage(Text.of(TextColors.RED, "Arena doesent exist you dumb fucking cunt!"));
        }



        return CommandResult.success();
    }
}
