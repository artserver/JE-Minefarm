package aren227.minefarm;

import aren227.minefarm.command.MinefarmCommand;
import aren227.minefarm.generator.MinefarmGenerator;
import aren227.minefarm.inventory.InvManager;
import aren227.minefarm.util.MinefarmID;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class Plugin extends JavaPlugin implements Listener {

    private Manager manager;
    private InvManager invManager;
    private MinefarmID minefarmID;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        manager = new Manager(this);
        invManager = new InvManager(this);
        minefarmID = new MinefarmID(this);

        getLogger().info((new UUID(0, 0)).toString());
        getLogger().info((new UUID(1, 0)).toString());
        getLogger().info((new UUID(100, 0)).toString());
        getLogger().info((new UUID(0xff, 0)).toString());


        this.getCommand("mf").setExecutor(new MinefarmCommand(manager));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if(worldName.equals("minefarm")) return new MinefarmGenerator();
        return null;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getClickedBlock().getType().equals(Material.STRUCTURE_BLOCK)){
            /*if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                MinefarmConfInv.create(event.getPlayer()).open(event.getPlayer());
            }
            else{
                event.setCancelled(true);
            }*/
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(event.getBlock().getType().equals(Material.STRUCTURE_BLOCK)) event.setCancelled(true);
    }
}
