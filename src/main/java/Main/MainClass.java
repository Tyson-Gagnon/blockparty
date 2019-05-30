package Main;

import Commands.*;
import Events.GameEvents;
import Events.SettingEvents;
import Objects.LastPlayerLoc;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import jdk.internal.instrumentation.Logger;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;
import org.spongepowered.api.world.schematic.Schematic;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import static org.spongepowered.api.command.args.GenericArguments.seq;
import static org.spongepowered.api.command.args.GenericArguments.string;

@Plugin(name = "blockParty",id = "blockparty",version = "1.0")
public class MainClass {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    private static File schematicsDir;

    public static File getSchematicsDir(){return schematicsDir;}

    public File getDefaultConfig(){ return this.defaultConfig; }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigManager(){return configManager;}

    private ConfigurationNode config = null;
    private static Logger logger;

    public static MainClass instance;

    java.util.List<String> test = new ArrayList<String>();

    public static boolean gameActive = false;
    public static boolean canJoin = false;

    public static String activeGameArena;
    public static java.util.List<LastPlayerLoc> playerLocs = new ArrayList<LastPlayerLoc>();

    public static boolean winner = false;

    @Inject
    Game game;

    private final Map<UUID, PlayerData> player_data = Maps.newHashMap();

    private PlayerData get(Player pl) {
        PlayerData data = this.player_data.get(pl.getUniqueId());
        if (data == null) {
            data = new PlayerData(pl.getUniqueId());
            this.player_data.put(pl.getUniqueId(), data);
        }
        return data;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent e){

        this.schematicsDir = new File(this.defaultConfig, "schematics");
        this.schematicsDir.mkdirs();

        try{
            if(!getDefaultConfig().exists()){
                test.add("placeholder");
                this.config = getConfigManager().load();
                this.config.getNode("ConfigVersion").setValue(1);
                this.config.getNode("Arenas", "ArenasList").setValue(test);
                getConfigManager().save(this.config);
                logger.info("Created the default config!");

            }

            this.config = getConfigManager().load();

        }catch (IOException Exception){
            logger.info("Couldnt create default config");
        }
    }

    @Listener
    public void onEnable(GameStartedServerEvent e){
        instance = this;
        registerCommands();
        registerListners();
        registerCookBook();

    }

    @Listener
    private void registerCommands(){
        CommandSpec blockPartySetLobby = CommandSpec.builder()
                .permission("blockparty.lobby")
                .executor(new SetLobby())
                .arguments(GenericArguments.string(Text.of("arenaName")))
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA, "Sets lobby arena"))
                .build();

        CommandSpec blockPartyGen = CommandSpec.builder()
                .permission("blockparty.generate")
                .executor(new Generate())
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA, "Generates the default platform (FLOOR1.schem)"))
                .build();

        CommandSpec blockPartyHelp = CommandSpec.builder()
                .permission("blockparty.help")
                .executor(new HelpCMD())
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA, "Shows command usages"))
                .build();

        CommandSpec blockPartyJoin = CommandSpec.builder()
                .permission("blockparty.join")
                .executor(new JoinGame())
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA, "Joins the active block party"))
                .build();

        CommandSpec blockPartyStart = CommandSpec.builder()
                .permission("blockparty.start")
                .arguments(string(Text.of("ArenaName")))
                .executor(new StartGame())
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA, "Starts a game of block party in a specific arena"))
                .build();

        CommandSpec blockPartyList = CommandSpec.builder()
                .permission("blockparty.list")
                .executor(new List())
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA, "List a name of available arenas"))
                .build();

        CommandSpec blockPartyDelete = CommandSpec.builder()
                .permission("blockparty.delete")
                .executor(new delete())
                .arguments(string(Text.of("ArenaName")))
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA, "Deletes the arena by the name its given"))
                .build();

        CommandSpec blockPartyWand = CommandSpec.builder()
                .permission("blockparty.wand")
                .executor(new Wand())
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA, "Gives you the setter wand to set the arena"))
                .build();

        CommandSpec blockPartySet = CommandSpec.builder()
                .permission("blockparty.set")
                .executor(new create())
                .arguments(string(Text.of("ArenaName")))
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA, "Creates the arena using the positions set by the wand"))
                .build();

        CommandSpec blockParty = CommandSpec.builder()
                .permission("blockparty.main")
                .executor(new BlockParty())
                .description(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] " , TextColors.AQUA,"Opens the main function of block party commands showing possible permissions and sub-commands"))
                .child(blockPartyWand, "wand")
                .child(blockPartySet,"create")
                .child(blockPartyDelete, "del", "delete")
                .child(blockPartyList,"list")
                .child(blockPartyJoin,"join")
                .child(blockPartyStart, "start")
                .child(blockPartyHelp, "help")
                .child(blockPartyGen,"gen","generate")
                .child(blockPartySetLobby,"setlobby")
                .build();

        game.getCommandManager().register(this, blockParty,"bp","blockparty");
    }

    public void registerCookBook(){
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .description(Text.of("Copies a region of the world to your clipboard"))
                .permission("command.copy")
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        src.sendMessage(Text.of(TextColors.RED, "Player only."));
                        return CommandResult.success();
                    }
                    Player player = (Player) src;
                    PlayerData data = get(player);
                    if (SettingEvents.pos1 == null || SettingEvents.pos2 == null) {
                        player.sendMessage(Text.of(TextColors.RED, "You must set both positions before copying"));
                        return CommandResult.success();
                    }
                    Vector3i min = SettingEvents.pos1.min(SettingEvents.pos2);
                    Vector3i max = SettingEvents.pos1.max(SettingEvents.pos2);

                    // Defines the volume we will be copying, using the min
                    // and max values gotten from the interact events.
                    ArchetypeVolume volume = player.getWorld().createArchetypeVolume(min, max, player.getLocation().getPosition().toInt());
                    data.setClipboard(volume);
                    player.sendMessage(Text.of(TextColors.GREEN, "Saved to clipboard."));
                    return CommandResult.success();
                })
                .build(), "copy");
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .description(Text.of("Pastes your clipboard at your current position"))
                .permission("command.paste")
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        src.sendMessage(Text.of(TextColors.RED, "Player only."));
                        return CommandResult.success();
                    }
                    Player player = (Player) src;
                    PlayerData data = get(player);
                    ArchetypeVolume volume = data.getClipboard();
                    if (volume == null) {
                        player.sendMessage(Text.of(TextColors.RED, "You must copy something before pasting"));
                        return CommandResult.success();
                    }

                    // Here we paste in the volume that we have previously
                    // copied or loaded. We specify that we want all block changes (update neighbor, observers and physics)
                    volume.apply(player.getLocation(), BlockChangeFlags.ALL);
                    player.sendMessage(Text.of(TextColors.GREEN, "Pasted clipboard into world."));
                    return CommandResult.success();
                })
                .build(), "paste");
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .description(Text.of("Saves your clipboard to disk"))
                .permission("command.save")
                .arguments(seq(string(Text.of("format")), string(Text.of("name"))))
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        src.sendMessage(Text.of(TextColors.RED, "Player only."));
                        return CommandResult.success();
                    }
                    String format = args.getOne("format").get().toString();
                    String name = args.getOne("name").get().toString();
                    Player player = (Player) src;
                    PlayerData data = get(player);
                    ArchetypeVolume volume = data.getClipboard();
                    if (volume == null) {
                        player.sendMessage(Text.of(TextColors.RED, "You must copy something before saving"));
                        return CommandResult.success();
                    }
                    if (!"legacy".equalsIgnoreCase(format) && !"sponge".equalsIgnoreCase(format)) {
                        player.sendMessage(Text.of(TextColors.RED, "Unsupported schematic format, supported formats are [legacy, sponge]"));
                        return CommandResult.success();
                    }

                    // Here we create the schematic object, set it's values,
                    // volume, and palette. The palette defines how the blocks
                    // are saved.

                    Schematic schematic = Schematic.builder()
                            .volume(data.getClipboard())
                            .metaValue(Schematic.METADATA_AUTHOR, player.getName())
                            .metaValue(Schematic.METADATA_NAME, name)
                            .paletteType(BlockPaletteTypes.LOCAL)
                            .build();

                    // We need to serialize the Schematic to a DataContainer so
                    // that we can save it using one of the DataFormats.
                    DataContainer schematicData = null;
                    if ("legacy".equalsIgnoreCase(format)) {
                        schematicData = DataTranslators.LEGACY_SCHEMATIC.translate(schematic);
                    } else if ("sponge".equalsIgnoreCase(format)) {
                        schematicData = DataTranslators.SCHEMATIC.translate(schematic);
                    }
                    File outputFile = new File(this.schematicsDir, name + ".schematic");
                    try {

                        // Normally we save NBT files as NBT files. We do this by using the NBT DataFormat.
                        DataFormats.NBT.writeTo(new GZIPOutputStream(new FileOutputStream(outputFile)), schematicData);
                        player.sendMessage(Text.of(TextColors.GREEN, "Saved schematic to " + outputFile.getAbsolutePath()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendMessage(Text.of(TextColors.DARK_RED, "Error saving schematic: " + e.getMessage()));
                        return CommandResult.success();
                    }
                    return CommandResult.success();
                })
                .build(), "save");
    }

    private void registerListners(){
        game.getEventManager().registerListeners(this, new SettingEvents());
        game.getEventManager().registerListeners(this, new GameEvents());
    }


    public void onDisable(GameStoppedServerEvent e){

    }


    public ConfigurationNode getNode(){return config;}


    public static MainClass getInstance(){
        return instance;
    }

    public static void messageGamePlayers(Text message){
        for(Player player : StartGame.gamePlayers){
            player.sendMessage(message);
        }
    }

}

 class PlayerData {

    private final UUID uid;
    private Vector3i pos1;
    private Vector3i pos2;
    private ArchetypeVolume clipboard;

    public PlayerData(UUID uid) {
        this.uid = uid;
    }

    public UUID getUid() {
        return this.uid;
    }

    public Vector3i getPos1() {
        return this.pos1;
    }

    public void setPos1(Vector3i pos) {
        this.pos1 = pos;
    }

    public Vector3i getPos2() {
        return this.pos2;
    }

    public void setPos2(Vector3i pos) {
        this.pos2 = pos;
    }

    public ArchetypeVolume getClipboard() {
        return this.clipboard;
    }

    public void setClipboard(ArchetypeVolume volume) {
        this.clipboard = volume;
    }
}