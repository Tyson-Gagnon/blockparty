package Commands;

import javafx.scene.paint.Material;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;

public class Wand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ", TextColors.RED, "You need to be player to run this command dipshit"));
            return CommandResult.success();
        }


        Player player = (Player)src;

                //creates a diamond hoe
                ItemStack itemStack = ItemStack.builder()
                .itemType(ItemTypes.DIAMOND_HOE).build();

                //gives it a display name and makes it unbreakable
                itemStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.LIGHT_PURPLE, "BlockParty Wand"));
                itemStack.offer(Keys.UNBREAKABLE, true);

        //LORE CAN ONLY BE SET BY A LIST. IDK WHY BUT K
        ArrayList<Text> arrayList = new ArrayList<>();
        arrayList.add(Text.of(TextColors.LIGHT_PURPLE,"Sets the arena of the blockparty. Left click = position 1. Right click = position 2."));
        itemStack.offer(Keys.ITEM_LORE,arrayList);

        //gives the player the hoe
        player.getInventory().offer(itemStack);
        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "[Block Party] ",TextColors.AQUA,"Here! Have a wand!"));


        return CommandResult.success();
    }
}
