package aren227.minefarm.inventory;

import aren227.minefarm.Manager;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.MinefarmID;
import aren227.minefarm.util.MinefarmInvitation;
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

public class MinefarmInvitationListInv implements InventoryProvider{

    Player player;

    public static SmartInventory create(Player player){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmInvitationListInv(player))
                .size(3, 9)
                .title("받은 초대장 목록")
                .build();
    }

    private MinefarmInvitationListInv(Player player){
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
        List<MinefarmInvitation> minefarmInvs = Manager.getInstance().getMinefarmInvitations(player.getUniqueId());
        int idx = 0;
        for(MinefarmInvitation inv : minefarmInvs){
            String from = Manager.getInstance().getPlugin().getServer().getOfflinePlayer(inv.from).getName();

            ItemStack bookQuill = getItemStackWithName(new ItemStack(Material.BOOK_AND_QUILL), ChatColor.GREEN + from + ChatColor.RESET + "님의 초대장");

            contents.set(idx / 9, idx % 9, ClickableItem.of(bookQuill, e -> {
                if(e.isLeftClick()) {
                    InvManager.close(player);
                    InvManager.open(player, MinefarmInvitationInv.create(player, inv));
                }
            }));
            idx++;
        }
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
