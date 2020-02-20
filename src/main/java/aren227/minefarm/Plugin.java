package aren227.minefarm;

import aren227.minefarm.command.MinefarmCommand;
import aren227.minefarm.generator.MinefarmGenerator;
import aren227.minefarm.inventory.InvManager;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.MinefarmID;
import kr.laeng.datastorage.DataStorageAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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

        boolean pass = true;
        for(int i = 0; i < 10; i++){
            UUID uuid = MinefarmID.generateUuid();
            if(!uuid.equals(MinefarmID.stringToUuid(MinefarmID.uuidToString(uuid)))){
                pass = false;
            }
        }
        getLogger().info("PASS : " + pass);
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
        if(event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.STRUCTURE_BLOCK)){
            /*if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                MinefarmConfInv.create(event.getPlayer()).open(event.getPlayer());
            }
            else{
                event.setCancelled(true);
            }*/
            event.setCancelled(true);
        }
        //아이템을 들었다
        if(event.getItem() != null){
            if(event.getItem().getType().equals(Material.KNOWLEDGE_BOOK)){
                event.setCancelled(true);

                if(event.getItem().getLore() != null && event.getItem().getLore().size() == 2){
                    if(event.getItem().getLore().get(1).equals(event.getPlayer().getUniqueId().toString())){
                        Minefarm minefarm = manager.createMinefarm();
                        minefarm.addPlayer(event.getPlayer().getUniqueId());
                        minefarm.setMain(event.getPlayer().getUniqueId());
                        minefarm.addOp(event.getPlayer().getUniqueId());
                        minefarm.setName(event.getPlayer().getName() + "의 마인팜");

                        event.getPlayer().sendMessage("마인팜이 " + ChatColor.GREEN + "생성" + ChatColor.RESET + "되었습니다!");
                        event.getPlayer().sendMessage(ChatColor.RED + "/mf " + ChatColor.RESET + "명령어를 통해 마인팜으로 이동하세요.");
                    }
                    else{
                        event.getPlayer().sendMessage(ChatColor.RED + "이 생성권은 아이템의 원래 소유자만 사용할 수 있습니다.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(event.getBlock().getType().equals(Material.STRUCTURE_BLOCK)) event.setCancelled(true);
    }
}
