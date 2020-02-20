package aren227.minefarm.inventory;

import aren227.minefarm.Manager;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.MinefarmID;
import aren227.minefarm.util.MinefarmInvitation;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinefarmSelectIconInv implements InventoryProvider {

    Player player;
    int page;

    public static SmartInventory create(Player player, int page){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmSelectIconInv(player, page))
                .size(5, 9)
                .title("아이콘 선택 - 페이지 " + (page + 1))
                .build();
    }

    private MinefarmSelectIconInv(Player player, int page){
        this.player = player;
        this.page = page;
    }

    private ItemStack getItemStackWithName(ItemStack itemStack, String name){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Material[] values = Material.values();
        List<Material> filtered = new ArrayList<>();
        for(Material material : values){
            if(!material.isTransparent()) filtered.add(material);
        }

        for(int i = 0; i < 36; i++){
            if(filtered.size() <= page * 36 + i) break;

            ItemStack item = new ItemStack(filtered.get(page * 36 + i));

            contents.set(i / 9, i % 9, ClickableItem.of(item, e -> {
                if(e.isLeftClick()) {
                    try{
                        Minefarm minefarm = Manager.getInstance().getCurrentMinefarm(player.getUniqueId());
                        if(minefarm == null) throw new RuntimeException("해당 마인팜은 존재하지 않습니다.");

                        minefarm.setIcon(item.getType());
                        player.sendMessage("마인팜의 아이콘이 변경되었습니다!");
                    }
                    catch (RuntimeException e1){
                        player.sendMessage(ChatColor.RED + e1.getMessage());
                    }
                    InvManager.close(player);
                }
            }));
        }

        ItemStack prev = getItemStackWithName(new ItemStack(Material.EMERALD_BLOCK), "이전 페이지");
        ItemStack next = getItemStackWithName(new ItemStack(Material.EMERALD_BLOCK), "다음 페이지");

        int maxPage = (int)Math.ceil(filtered.size() / 36.0) - 1;

        if(page > 0){
            contents.set(4, 0, ClickableItem.of(prev, e -> {
                if(e.isLeftClick()) {
                    InvManager.close(player);
                    InvManager.open(player, MinefarmSelectIconInv.create(player, page - 1));
                }
            }));
        }

        if(page < maxPage){
            contents.set(4, 8, ClickableItem.of(next, e -> {
                if(e.isLeftClick()) {
                    InvManager.close(player);
                    InvManager.open(player, MinefarmSelectIconInv.create(player, page + 1));
                }
            }));
        }
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
