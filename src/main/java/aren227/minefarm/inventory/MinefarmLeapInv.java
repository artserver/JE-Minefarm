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

public class MinefarmLeapInv implements InventoryProvider{

    Player player;
    boolean exit;

    public static SmartInventory create(Player player){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmLeapInv(player, false))
                .size(3, 9)
                .title(ChatColor.LIGHT_PURPLE + "리프" + ChatColor.RESET + "할 마인팜을 선택하세요!")
                .build();
    }

    public static SmartInventory create(Player player, boolean exit){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmLeapInv(player, exit))
                .size(3, 9)
                .title(ChatColor.LIGHT_PURPLE + "리프" + ChatColor.RESET + "할 마인팜을 선택하세요!")
                .build();
    }

    private MinefarmLeapInv(Player player, boolean exit){
        this.player = player;
        this.exit = exit;
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
            meta.setLore(Arrays.asList("인원: " + minefarm.getPlayers().size(), "마인팜 ID: " + ChatColor.GOLD + MinefarmID.uuidToString(minefarm.getUniqueId())));
            grass.setItemMeta(meta);

            contents.set(idx / 9, idx % 9, ClickableItem.of(grass, e -> {
                if(e.isLeftClick()) {
                    InvManager.close(player);
                    if(Manager.getInstance().getCurrentMinefarm(player.getUniqueId()) != null && Manager.getInstance().getCurrentMinefarm(player.getUniqueId()).getUniqueId().equals(minefarm.getUniqueId())){
                        player.sendMessage(ChatColor.RED + "지금 거주하는 마인팜으로는 리프할 수 없습니다.");
                    }
                    else{
                        try{
                            Manager.getInstance().leapMinefarm(player.getUniqueId(), minefarm.getUniqueId());
                            player.sendMessage(minefarm.getName() + "으로 " + ChatColor.LIGHT_PURPLE + "리프" + ChatColor.RESET + "되었습니다!");
                            Manager.getInstance().goToCurrentMinefarm(player);
                        }
                        catch (RuntimeException exception){
                            player.sendMessage(ChatColor.RED + "마인팜 리프에 문제가 발생했습니다 :(");
                            player.sendMessage(ChatColor.RED + exception.getMessage());
                        }
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
