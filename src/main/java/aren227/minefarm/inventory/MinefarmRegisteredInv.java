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
import java.util.List;
import java.util.UUID;

public class MinefarmRegisteredInv implements InventoryProvider{

    Player player;

    public static SmartInventory create(Player player){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmRegisteredInv(player))
                .size(3, 9)
                .title("가입된 마인팜 목록")
                .build();
    }

    private MinefarmRegisteredInv(Player player){
        this.player = player;
    }

    private ItemStack getItemStackWithName(ItemStack itemStack, String name){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        //TODO: 페이지 넘김 처리
        List<UUID> minefarms = Manager.getInstance().getMinefarms(player.getUniqueId());
        int idx = 0;
        for(UUID uuid : minefarms){
            Minefarm minefarm = Manager.getInstance().getMinefarmByUuid(uuid);

            ItemStack grass = new ItemStack(minefarm.getIcon());
            ItemMeta meta = grass.getItemMeta();
            meta.setDisplayName(minefarm.getName());
            meta.setLore(Arrays.asList(ChatColor.WHITE + "인원: " + minefarm.getPlayers().size(), ChatColor.WHITE + "마인팜 ID: " + ChatColor.GOLD + MinefarmID.uuidToString(minefarm.getUniqueId()), ChatColor.WHITE + "명성 : " + minefarm.getReputation()));
            grass.setItemMeta(meta);

            //contents.set(idx / 9, idx % 9, ClickableItem.empty(grass));

            contents.set(idx / 9, idx % 9, ClickableItem.empty(grass));

            idx++;
        }
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
