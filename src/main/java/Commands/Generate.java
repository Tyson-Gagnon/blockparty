package Commands;

import Main.MainClass;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.schematic.Schematic;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

public class Generate implements CommandExecutor {


    Player player;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You need to be player to run this command dipshit"));
            return CommandResult.success();
        }
        player = (Player)src;
        loadSchem("FLOOR1");

        return CommandResult.success();
    }


    private void loadSchem(String schemName){

        File inputFile = new File(MainClass.getSchematicsDir(),schemName + ".schematic");
        if(!inputFile.exists()){
            Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.RED,"SCHEM", schemName , " NOT FOUND!"));
        }else{
            DataContainer schematicData = null;
            try {
                schematicData = DataFormats.NBT.readFrom(new GZIPInputStream(new FileInputStream(inputFile)));
            } catch (Exception e) {
                e.printStackTrace();
                Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.RED,"ERROR LOADING SCHEM FILE"));
            }

            if(schematicData != null){
                Schematic schematic = DataTranslators.LEGACY_SCHEMATIC.translate(schematicData);
                schematic.apply(player.getLocation(), BlockChangeFlags.NONE);

                player.sendMessage(Text.of(TextColors.LIGHT_PURPLE,"[Block Party] ",TextColors.AQUA," Default platform generated. Please make sure to set the arena! Position 1 should be the block that you are standing on" +
                        " when you generated the arena!"));

            }else{
                player.sendMessage(Text.of("Schem data is null"));
            }
        }
    }

}
