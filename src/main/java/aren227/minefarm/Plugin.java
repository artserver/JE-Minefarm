package aren227.minefarm;

import aren227.minefarm.command.MinefarmCommand;
import aren227.minefarm.generator.MinefarmGenerator;
import aren227.minefarm.inventory.MinefarmConfInv;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin implements Listener {

    private Manager manager;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        manager = new Manager(this);

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
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                MinefarmConfInv.create(event.getPlayer()).open(event.getPlayer());
            }
            else{
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(event.getBlock().getType().equals(Material.STRUCTURE_BLOCK)) event.setCancelled(true);
    }
}
