package Commands;

import Main.MainClass;
import Objects.LastPlayerLoc;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class JoinGame implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You need to be player to run this command dipshit"));
            return CommandResult.success();
        } Player player = (Player)src;

        double pos1x,pos1y,pos1z,pos2x,pos2y,pos2z;
        Location<World> arena;

        if(MainClass.canJoin == true){
            String arenaName = MainClass.activeGameArena;
            ConfigurationNode node = MainClass.getInstance().getNode();


            //gets the coords of both corners
            pos1x = node.getNode("Arenas",arenaName,"x1").getDouble();
            pos1z = node.getNode("Arenas",arenaName,"z1").getDouble();
            pos1y = node.getNode("Arenas",arenaName,"y1").getDouble();

            pos2x = node.getNode("Arenas",arenaName,"x2").getDouble();
            pos2z = node.getNode("Arenas",arenaName,"z2").getDouble();
            pos2y = node.getNode("Arenas",arenaName,"y2").getDouble();

            //gets the middle of the arena by averaging the 2 corners
            double posx = (pos1x + pos2x) / 2;
            double posy = (pos1y + pos2y) / 2 + 10;
            double posz = (pos1z + pos2z) / 2;

            //creates new location from the coords
            arena = new Location<>(player.getWorld(),posx,posy,posz);

        }else{
            player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "No current active games to join or game has already started"));
            return CommandResult.success();
        }

        //checks if player isnt already in the game. It does this by checking if they are in the list of players in the game. if not, it adds them
        if(!(StartGame.gamePlayers.contains(player))){
            StartGame.gamePlayers.add(player);

            //Only messages players that are participating in the game! so it doesent spam chat for everyone else
            for(Player joined : StartGame.gamePlayers){
                joined.sendMessage(Text.of(TextStyles.BOLD,TextColors.AQUA,player.getName(),TextColors.RED," has joined"));
            }
            //teleports them to arena
            player.setLocation(arena);

            //LastPlayerLoc playerLoc = new LastPlayerLoc(player,player.getLocation()); Dont mind this. Was a stpud idea that doesent work
            //MainClass.playerLocs.add(playerLoc); same with this

            //makes sure that the players cant fly and shuts them down if they are
            player.offer(Keys.CAN_FLY, false);
            player.offer(Keys.IS_FLYING, false);

        }else {
            player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You have already joined this Block Party!"));
        }


        return CommandResult.success();
    }



}
