package aren227.minefarm.inventory;

import aren227.minefarm.Manager;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.MinefarmID;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class FailInv implements InventoryProvider{

    Player player;
    String title;
    String reason;
    Runnable runnable;

    public static SmartInventory create(Player player, String title, String reason){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new FailInv(player, title, reason, null))
                .size(3, 9)
                .title(title)
                .build();
    }

    public static SmartInventory create(Player player, String title, String reason, Runnable runnable){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new FailInv(player, title, reason, runnable))
                .size(3, 9)
                .title(title)
                .build();
    }

    private FailInv(Player player, String title, String reason, Runnable runnable){
        this.player = player;
        this.title = title;
        this.reason = reason;
        this.runnable = runnable;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemStack red = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = red.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + title);
        itemMeta.setLore(Arrays.asList(reason.split("\n")));
        red.setItemMeta(itemMeta);

        ClickableItem cItem = ClickableItem.of(red, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                if(runnable != null) runnable.run();
            }
        });

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                contents.set(i, j, cItem);
            }
        }
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
