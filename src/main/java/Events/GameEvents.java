package Events;

import Commands.StartGame;
import Main.MainClass;
import Objects.LastPlayerLoc;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;


public class GameEvents {

    @Listener
    public void onDamageEventByLava(DamageEntityEvent e, @Root DamageSource source){
        Entity entity = e.getTargetEntity();

        if(!(entity instanceof Player)){
            return;
        }
        Player player = (Player) entity;

        if(StartGame.gamePlayers.contains(player) && StartGame.gamePlayers.size() > 1){
            if(source.getType() == DamageTypes.FIRE || source.getType() == DamageTypes.MAGMA){

                        //TODO: SET PLAYER TO LOBBY LOCATION
                        ConfigurationNode node = MainClass.getInstance().getNode();

                        if(node.getNode("Arenas", MainClass.activeGameArena,"lobby").isVirtual()){
                            player.setLocation(player.getWorld().getSpawnLocation());
                        }else{

                            Location location = new Location(player.getWorld(), node.getNode("Arena",MainClass.activeGameArena,"lobby","x").getDouble(),
                                    node.getNode("Arena",MainClass.activeGameArena,"lobby","y").getDouble() + 2,
                                    node.getNode("Arena",MainClass.activeGameArena,"lobby","z").getDouble());

                            player.setLocation(location);

                        }

                        StartGame.gamePlayers.remove(player);
                        player.sendMessage(Text.of(TextStyles.BOLD, TextColors.AQUA,player.getName(),TextColors.RED," has lost. ", StartGame.gamePlayers.size(), " players remaining" ));
                        for(Player inGame : StartGame.gamePlayers){
                            inGame.sendMessage(Text.of(TextStyles.BOLD, TextColors.AQUA,player.getName(),TextColors.RED," has lost. ", StartGame.gamePlayers.size(), " players remaining" ));
                        }


                player.setItemInHand(HandTypes.OFF_HAND, null);
                e.setCancelled(true);
            }
        }
        if(MainClass.winner == false) {
            if (StartGame.gamePlayers.size() == 1) {
                Player winner = StartGame.gamePlayers.get(0);
                Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.AQUA, winner.getName(), " has won the Block Party event!"));
                    ConfigurationNode node = MainClass.getInstance().getNode();

                    if(node.getNode("Arenas", MainClass.activeGameArena,"lobby").isVirtual()){
                        winner.setLocation(player.getWorld().getSpawnLocation());
                    }else{

                        Location location = new Location(player.getWorld(), node.getNode("Arena",MainClass.activeGameArena,"lobby","x").getDouble(),
                                node.getNode("Arena",MainClass.activeGameArena,"lobby","y").getDouble() + 2,
                                node.getNode("Arena",MainClass.activeGameArena,"lobby","z").getDouble());

                        winner.setLocation(location);

                    }
                    StartGame.gamePlayers.remove(player.getName());
                    MainClass.winner = true;

                    //TODO: Make sounds when someone is eliminated
                    //TODO: Make sound when someone wins


            }
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect e){
        Player player = e.getTargetEntity();

        if(StartGame.gamePlayers.contains(player) && StartGame.gamePlayers.size() > 1){

                    StartGame.gamePlayers.remove(player);
                    player.setItemInHand(HandTypes.OFF_HAND, null);
                    for(Player inGame : StartGame.gamePlayers){
                        inGame.sendMessage(Text.of(TextStyles.BOLD, TextColors.AQUA,player.getName(),TextColors.RED," has lost. ", StartGame.gamePlayers.size(), " players remaining" ));
                    }

        }
        if(MainClass.winner == false) {
            if (StartGame.gamePlayers.size() == 1) {
                Player winner = StartGame.gamePlayers.get(0);
                Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.AQUA, winner.getName(), " has won the Block Party event!"));

                    MainClass.winner = true;
                    StartGame.gamePlayers.remove(player.getName());
                    //TODO: Stop Event from continuing


            }
        }
    }



}
