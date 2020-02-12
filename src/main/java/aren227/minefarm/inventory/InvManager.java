package aren227.minefarm.inventory;

import aren227.minefarm.Plugin;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

//인벤토리를 2틱 뒤에 열어 인벤토리 아이템 빼먹기를 방지합니다.
//매우 심각한 문제이니 꼭 유념하시길
public class InvManager {

    public static InvManager instance;

    private Plugin plugin;

    private HashMap<UUID, SmartInventory> opened = new HashMap<>();

    public InvManager(Plugin plugin){
        instance = this;

        this.plugin = plugin;
    }

    public static void open(Player player, SmartInventory inv){
        close(player);

        instance.plugin.getServer().getScheduler().scheduleSyncDelayedTask(instance.plugin, () -> {
            inv.open(player);
        }, 2);
    }

    public static void close(Player player){
        if(instance.opened.containsKey(player.getUniqueId())){
            instance.opened.get(player.getUniqueId()).close(player);
            instance.opened.remove(player.getUniqueId());
        }
    }

}
