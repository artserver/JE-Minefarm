package aren227.minefarm;


import aren227.minefarm.generator.MinefarmGenerator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

public class Manager {

    private static final String WORLD_NAME = "minefarm";

    private Plugin plugin;

    public Manager(Plugin plugin){
        this.plugin = plugin;
    }

    public void goToMinefarm(Player player){
        World skyblockWorld = plugin.getServer().getWorld(WORLD_NAME);

        if(skyblockWorld == null){ //월드가 존재하지 않습니다
            skyblockWorld = WorldCreator
                    .name(WORLD_NAME)
                    .type(WorldType.FLAT)
                    .generateStructures(false)
                    .environment(World.Environment.NORMAL)
                    .generator(new MinefarmGenerator())
                    .createWorld();
            skyblockWorld.save();
        }

        player.teleport(new Location(skyblockWorld, 0, 10, 0));
    }

}
