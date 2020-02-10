package aren227.minefarm.util;

import aren227.minefarm.Manager;
import org.bukkit.Location;

public class Sector {

    public int x;
    public int z;

    public Sector(int x, int z){
        this.x = x;
        this.z = z;
    }

    public boolean isIn(double wx, double wy, double wz){
        return (x * Manager.MINEFARM_DIST <= wx && wx < (x + 1) * Manager.MINEFARM_DIST && 0 <= wy && wy < 256 && z * Manager.MINEFARM_DIST <= wz && wz < (z + 1) * Manager.MINEFARM_DIST);
    }

    public Location getDefaultSpawnLocation(){
        return new Location(Manager.getInstance().getMinefarmWorld(), Manager.MINEFARM_DIST * (x + 0.5) + 0.5, Manager.MINEFARM_Y + 1, Manager.MINEFARM_DIST * (z + 0.5) + 0.5);
    }

}
