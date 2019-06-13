package Commands;

import Main.MainClass;
import Objects.Cuboid;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.schematic.Schematic;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class StartGame implements CommandExecutor {
    public static ArrayList<Player> gamePlayers = new ArrayList<>();
    MainClass instance= MainClass.getInstance();
    Text colorGone;
    int round = 1;
    int delay = 5000;
    Cuboid cuboid;

    ConfigurationNode node = MainClass.getInstance().getNode();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You need to be player to run this command dipshit"));
            return CommandResult.success();
        } Player gameStarter = (Player)src;

        String arenaName = args.<String>getOne("ArenaName").get();
        arenaName = arenaName.toUpperCase();

        java.util.List<String> arenaList = node.getNode("Arenas", "ArenasList").getChildrenList().stream()
                .map(ConfigurationNode::getString).collect(Collectors.toList());

        if(!(arenaList.contains(arenaName))){
            gameStarter.sendMessage(Text.of(TextColors.RED, "ARENA NOT FOUND!"));

        }else {
            lobbyTime(arenaName);
            MainClass.activeGameArena = arenaName;
            Location pos1 = new Location(Sponge.getServer().getWorld("world").get(),node.getNode("Arenas",arenaName,"x1").getDouble(),node.getNode("Arenas",arenaName,"y1").getDouble(),node.getNode("Arenas",arenaName,"z1").getDouble());
            Location pos2 = new Location(Sponge.getServer().getWorld("world").get(),node.getNode("Arenas",arenaName,"x2").getDouble(),node.getNode("Arenas",arenaName,"y2").getDouble(),node.getNode("Arenas",arenaName,"z2").getDouble());
            cuboid = new Cuboid(pos1,pos2);
        }


        return CommandResult.success();
    }

    private void lobbyTime(String arenaName){



        MainClass.canJoin = true;
        Text joinMessage = Text.builder("[Block Party] ").color(TextColors.LIGHT_PURPLE).onClick(TextActions.runCommand("/bp join"))
                .append(Text.builder("A Block Party Event is starting in " + "60" +" seconds.Do /blockparty join or click this text to join!").color(TextColors.AQUA).build()).build();


        Sponge.getServer().getBroadcastChannel().send(joinMessage);

        Task task = Task.builder().execute(new CancellingTimerTask())
                .interval(1, TimeUnit.SECONDS)
                .name("Self-Cancelling Timer Task").submit(instance);

        Task task2 = Task.builder().execute(() -> checkIfPlayers(arenaName))
                .delay(65, TimeUnit.SECONDS)
                .name("To-start").submit(instance);

    }

    public void checkIfPlayers(String arenaName){
        if(gamePlayers.size() < 2){
            Sponge.getServer().getBroadcastChannel().send((Text.of(TextColors.LIGHT_PURPLE,"[Block Party] " ,TextColors.AQUA, "Event canceled, not enough players joined for the event to start!")));
            endgGame();
        }else {
            neutralState(arenaName);
        }
    }

    private void neutralState(String arenaName){


        DyeColor dyeColor = chooseDissapearingBlocks();
        Task task4 = Task.builder().execute(() -> deleteBlocks(dyeColor,arenaName))
                .delay(delay, TimeUnit.MILLISECONDS)
                .name("To-delete").submit(instance);

    }

    private void deleteBlocks(DyeColor dyeColor, String arenaName){
        changeBlocks(dyeColor);
        Task task5 = Task.builder().execute(() -> reformBlocks(arenaName))
                .delay(5, TimeUnit.SECONDS)
                .name("To-start1").submit(instance);


    }

    private void reformBlocks(String arenaName){
        Random random = new Random();
        int rn = random.nextInt(6 - 1 + 1) + 1;
        String schemName = "FLOOR" + Integer.toString(rn);
        loadSchem(schemName, MainClass.activeGameArena);
        for(Player player : gamePlayers){
            player.setItemInHand(HandTypes.OFF_HAND, null);
        }
        round++;

        if(delay > 1000){
            if(round % 3 == 0){
                delay = delay - 500;
                MainClass.messageGamePlayers(Text.of(TextStyles.BOLD,TextColors.LIGHT_PURPLE,"[Block Party] ", TextColors.GRAY,"Time to go faster!"));
                for(Player players : gamePlayers){
                    players.playSound(SoundTypes.BLOCK_ANVIL_LAND,SoundCategories.MASTER,players.getPosition(),1);
                }
            }
        }

        if(round == 24){
            MainClass.messageGamePlayers(Text.of(TextStyles.BOLD,TextColors.LIGHT_PURPLE,"[Block Party] ", TextColors.GRAY,"We are now at max speed!"));
            for(Player players : gamePlayers){
                players.playSound(SoundTypes.BLOCK_ANVIL_LAND,SoundCategories.MASTER,players.getPosition(),1);
            }
        }



        if(MainClass.winner == true){
            endgGame();
        }else{
            Task task6 = Task.builder().execute(() -> neutralState(arenaName))
                    .delay(5, TimeUnit.SECONDS)
                    .name("To-start2").submit(instance);
        }


    }

    public void endgGame(){
        MainClass.activeGameArena = null;
        MainClass.gameActive = false;
        MainClass.canJoin = false;
        gamePlayers = new ArrayList<Player>();
        MainClass.winner = false;
        delay = 5000;
        round = 1;
    }

    public DyeColor chooseDissapearingBlocks(){
        DyeColor dyeColors;
        Random random = new Random();
        int rn = random.nextInt(11 - 1 + 1) + 1;
        ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.CONCRETE).build();

        switch (rn){
            case 1:
                dyeColors = DyeColors.RED;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;
            case 2:
                dyeColors = DyeColors.BLUE;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;

            case 3:
                dyeColors = DyeColors.CYAN;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;
            case 4:
                dyeColors = DyeColors.GREEN;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;
            case 5:
                dyeColors = DyeColors.LIGHT_BLUE;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;
            case 6:
                dyeColors = DyeColors.LIME;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;
            case 7:
                dyeColors = DyeColors.MAGENTA;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;
            case 8:
                dyeColors = DyeColors.ORANGE;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;
            case 9:
                dyeColors = DyeColors.PINK;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;
            case 10:
                dyeColors = DyeColors.PURPLE;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;
            case 11:
                dyeColors = DyeColors.YELLOW;
                itemStack.offer(Keys.DYE_COLOR,dyeColors);
                break;

            default:
                dyeColors = DyeColors.WHITE;
                break;

        }


        for(Player players : gamePlayers){
            players.setItemInHand(HandTypes.OFF_HAND, itemStack);
            players.playSound(SoundTypes.BLOCK_NOTE_BELL, SoundCategories.MASTER,players.getPosition(),1);
        }

        return  dyeColors;
    }

    public void loadSchem(String schemName,String arenaName){


        File inputFile = new File(MainClass.getSchematicsDir(),schemName + ".schematic");
        if(!inputFile.exists()){
            Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.RED,"ERROR404: ARENA", schemName , " NOT FOUND!"));
        }else{
                DataContainer schematicData = null;
            try {
                // Schematics and normally saved as NBT, so we use the
                // NBT DataFormat to read the data.
                schematicData = DataFormats.NBT.readFrom(new GZIPInputStream(new FileInputStream(inputFile)));
            } catch (Exception e) {
                e.printStackTrace();
                Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.RED,"ERROR LOADING SCHEM FILE"));
            }
            Schematic schematic = DataTranslators.SCHEMATIC.translate(schematicData);

            double posx = node.getNode("Arenas",arenaName,"x1").getDouble();
            double posz = node.getNode("Arenas",arenaName,"z1").getDouble();
            double posy = node.getNode("Arenas",arenaName,"y1").getDouble() + 1;


            Location<World> arena;
            arena = new Location<>(Sponge.getServer().getWorld("world").get(),posx,posy,posz);


            schematic.apply(arena, BlockChangeFlags.NONE);

        }

    }

    public void changeBlocks(DyeColor exception) {

        for (Location location : cuboid.getBlocks()){

            Optional<DyeColor> optional = location.getBlock().get(Keys.DYE_COLOR);
            if(optional.isPresent()){
                DyeColor blockDyeColor = optional.get();
                if(!(blockDyeColor == exception)){
                    location.setBlockType(BlockTypes.AIR);
                }
            }
        }


        /*Location pos1 = new Location(Sponge.getServer().getWorld("world").get(),node.getNode("Arenas",arenaName,"x1").getDouble(),node.getNode("Arenas",arenaName,"y1").getDouble(),node.getNode("Arenas",arenaName,"z1").getDouble());
        Location pos2 = new Location(Sponge.getServer().getWorld("world").get(),node.getNode("Arenas",arenaName,"x2").getDouble(),node.getNode("Arenas",arenaName,"y2").getDouble(),node.getNode("Arenas",arenaName,"z2").getDouble());

        Sponge.getServer().getBroadcastChannel().send(Text.of("Positions Found!"));

        double x1 = pos1.getBlockX();
        double y1 = pos1.getBlockY();
        double z1 = pos1.getBlockZ();

        double x2 = pos2.getBlockX();
        double y2 = pos2.getBlockY();
        double z2 = pos2.getBlockZ();

        Sponge.getServer().getBroadcastChannel().send(Text.of("Positions got!"));

        double lowestX = Math.min(x1, x2);
        double lowestY = Math.min(y1, y2);
        double lowestZ = Math.min(z1, z2);

        double highestX = lowestX == x1 ? x2 : x1;
        double highestY = lowestX == y1 ? y2 : y1;
        double highestZ = lowestX == z1 ? z2 : z1;

        for(double x = lowestX; x <= highestX; x++)
            for(double y = lowestY; x <= highestY; y++)
                for(double z = lowestZ; x <= highestZ; z++){
                    Sponge.getServer().getBroadcastChannel().send(Text.of("new block found Found!"));

                    Location<World> loc = new Location<World>(Sponge.getServer().getWorld("world").get(),x,y,z);
                    BlockState blockState = loc.getBlock();

                    if(!blockState.get(Keys.DYE_COLOR).equals(exception)){
                        Sponge.getServer().getBroadcastChannel().send(Text.of("Deleting blocks"));
                        loc.setBlockType(BlockTypes.AIR);
                        Sponge.getServer().getBroadcastChannel().send(Text.of("Blocks deleted"));
                    }
                }*/
    }


}

class CancellingTimerTask implements Consumer<Task> {
    private int seconds = 60;
    @Override
    public void accept(Task task) {
        seconds--;
        Text joinMessage = Text.builder("[Block Party] ").color(TextColors.LIGHT_PURPLE).onClick(TextActions.runCommand("/bp join"))
                .append(Text.builder("A Block Party Event is starting in " + seconds +" seconds. Do /blockparty join or click this text to join!").color(TextColors.AQUA).build()).build();
        if(seconds == 30 || seconds == 10){
            MainClass.messageGamePlayers(joinMessage);
        }

        if(seconds == 5){
            MainClass.messageGamePlayers(Text.of(TextColors.LIGHT_PURPLE, "[Block Party]" ,TextColors.AQUA, "Game has now begun. A block will appear in your off hand slot" +
                    " Run to that colour before the timer runs out to move on to the next round. The timer gets faster and faster as more round go on."));
        }

        if (seconds < 1) {
            MainClass.canJoin = false;
            MainClass.gameActive = true;
            task.cancel();
        }
    }
}
