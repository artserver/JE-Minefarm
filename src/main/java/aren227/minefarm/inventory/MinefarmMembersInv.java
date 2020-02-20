package aren227.minefarm.inventory;

import aren227.minefarm.Manager;
import aren227.minefarm.Plugin;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.MinefarmID;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MinefarmMembersInv implements InventoryProvider{

    Player player;

    public static SmartInventory create(Player player){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmMembersInv(player))
                .size(3, 9)
                .title("현재 마인팜 구성원")
                .build();
    }

    private MinefarmMembersInv(Player player){
        this.player = player;
    }

    private ItemStack getSkull(OfflinePlayer player, Minefarm minefarm, boolean openerOp){
        SkullMeta meta = (SkullMeta)Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getName());
        if(minefarm.isOp(player.getUniqueId())){
            if(openerOp) meta.setLore(Arrays.asList("관리자", ChatColor.WHITE + "클릭해서 설정 화면을 여세요."));
            else meta.setLore(Arrays.asList("관리자"));
        }
        else{
            if(openerOp) meta.setLore(Arrays.asList(ChatColor.WHITE + "클릭해서 설정 화면을 여세요."));
        }
        ItemStack stack = new ItemStack(Material.SKULL_ITEM,1 , (byte)3);
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        //TODO: 페이지 넘김 처리
        Minefarm minefarm = Manager.getInstance().getCurrentMinefarm(player.getUniqueId());
        if(minefarm == null) return;
        List<UUID> members = minefarm.getPlayers();
        int idx = 0;
        for(UUID uuid : members){
            OfflinePlayer offline = Manager.getInstance().getPlugin().getServer().getOfflinePlayer(uuid);
            contents.set(idx / 9, idx % 9, ClickableItem.of(getSkull(offline, minefarm, minefarm.isOp(player.getUniqueId())), e -> {
                if(e.isLeftClick()) {
                    if(minefarm.isOp(player.getUniqueId())){
                        InvManager.close(player);
                        InvManager.open(player, MinefarmMemberConfInv.create(player, offline));
                    }
                }
            }));
            idx++;
        }
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
