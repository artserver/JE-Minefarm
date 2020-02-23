package aren227.minefarm.util;

import aren227.minefarm.Manager;
import org.bukkit.Location;

public class Sector {

    public int x;
    public int z;
    public int size;

    public Sector(int x, int z, int size){
        this.x = x;
        this.z = z;
        this.size = size;
    }

    public boolean isIn(double wx, double wy, double wz){
        double cx = Manager.MINEFARM_DIST * (x + 0.5);
        double cz = Manager.MINEFARM_DIST * (z + 0.5);
        double rad = size / 2.0;
        return (cx - rad < wx && wx < cx + rad && cz - rad < wz && wz < cz + rad && 0 <= wy && wy < 256);
    }

    public boolean isIn(int wx, int wy, int wz){
        int cx = Manager.MINEFARM_DIST * x + Manager.MINEFARM_DIST / 2;
        int cz = Manager.MINEFARM_DIST * z + Manager.MINEFARM_DIST / 2;
        int rad = size / 2;
        return (cx - rad <= wx && wx < cx + rad && cz - rad <= wz && wz < cz + rad && 0 <= wy && wy < 256);
    }

    public Location getDefaultSpawnLocation(){
        return new Location(Manager.getInstance().getMinefarmWorld(), Manager.MINEFARM_DIST * (x + 0.5) + 0.5, Manager.MINEFARM_Y + 1, Manager.MINEFARM_DIST * (z + 0.5) + 0.5);
    }

}
