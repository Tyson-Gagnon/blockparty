package Events;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

import java.util.Locale;
import java.util.Optional;

public class SettingEvents {

    public static Vector3i pos1;
    public static Vector3i pos2;

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary e){
        Player player = e.getCause().last(Player.class).get();

        Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);
        if(optionalItemStack.isPresent()){
            ItemStack itemStack = optionalItemStack.get();
            if(itemStack.getType() == ItemTypes.DIAMOND_HOE){
                String displayName = itemStack.get(Keys.DISPLAY_NAME).toString();
                if(displayName.contains("BlockParty")){
                    if(player.hasPermission("blockparty.wand")){
                        pos1 = e.getTargetBlock().getPosition();
                        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE,"[Block Party] " ,TextColors.AQUA,"Position 1 set to ", pos1.getX(), " " ,pos1.getY(), " ", pos1.getZ()));
                        e.setCancelled(true);
                    }
                }
            }
        }


    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary e){
        Player player = e.getCause().last(Player.class).get();

        Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);
        if(optionalItemStack.isPresent()){
            ItemStack itemStack = optionalItemStack.get();
            if(itemStack.getType() == ItemTypes.DIAMOND_HOE){
                String displayName = itemStack.get(Keys.DISPLAY_NAME).toString();
                if(displayName.contains("BlockParty")){
                    if(player.hasPermission("blockparty.wand")){
                        pos2 = e.getTargetBlock().getPosition();
                        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE,"[Block Party] " ,TextColors.AQUA,"Position 2 set to ", pos2.getX(), " " ,pos2.getY(), " ", pos2.getZ()));
                        e.setCancelled(true);
                    }
                }
            }
        }

    }

}
