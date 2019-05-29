package Commands;

import Events.SettingEvents;
import Main.MainClass;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.schematic.Schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;


public class create implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Vector3i pos1 =  SettingEvents.pos1;
        Vector3i pos2 =  SettingEvents.pos2;

        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You need to be player to run this command dipshit"));
            return CommandResult.success();
        }
        Player player = (Player)src;

        String arenaName = args.<String>getOne("ArenaName").get();
        arenaName = arenaName.toUpperCase();

        if(pos1 != null && pos2 != null){
            ConfigurationNode node = MainClass.getInstance().getNode();

            if(node.getNode("Arenas",arenaName).isVirtual()){
                node.getNode("Arenas",arenaName,"x1").setValue(pos1.getX());
                node.getNode("Arenas",arenaName,"y1").setValue(pos1.getY());
                node.getNode("Arenas",arenaName,"z1").setValue(pos1.getZ());

                node.getNode("Arenas",arenaName,"x2").setValue(pos2.getX());
                node.getNode("Arenas",arenaName,"y2").setValue(pos2.getY());
                node.getNode("Arenas",arenaName,"z2").setValue(pos2.getZ());

                List<String> arenaList = node.getNode("Arenas", "ArenasList").getChildrenList().stream()
                        .map(ConfigurationNode::getString).collect(Collectors.toList());

                arenaList.add(arenaName);
                node.getNode("Arenas","ArenasList").setValue(arenaList);

                player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.AQUA,"Arena ", arenaName ," set"));



                try {
                    MainClass.getInstance().getConfigManager().save(node);
                }catch (IOException e){
                    e.printStackTrace();
                }


            }else{
                player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED,"Arena already exist!"));
            }


        }else{
            player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED,"One of the positions is not set with the wand!"));
        }



        return CommandResult.success();
    }

}
