package OtherClasses;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LastPlayerLoc {

    private Player player;
    private Location<World> location;

    public LastPlayerLoc(Player player, Location<World> location) {
        this.player = player;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return player;
    }
}
