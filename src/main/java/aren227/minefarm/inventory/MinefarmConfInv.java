package aren227.minefarm.inventory;

import aren227.minefarm.Manager;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.MinefarmID;
import aren227.minefarm.util.Time2String;
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

public class MinefarmConfInv implements InventoryProvider{

    Player player;

    public static SmartInventory create(Player player){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmConfInv(player))
                .size(3, 9)
                .title("마인팜 관리")
                .build();
    }

    private MinefarmConfInv(Player player){
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
        final Minefarm minefarm = Manager.getInstance().getCurrentMinefarm(player.getUniqueId());

        ItemStack chest = getItemStackWithName(new ItemStack(Material.CHEST), "마인팜 유저 목록");

        ItemStack bookQuill = getItemStackWithName(new ItemStack(Material.BOOK_AND_QUILL), "초대장 보내기");

        contents.set(0, 3, ClickableItem.of(chest, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                InvManager.open(player, MinefarmMembersInv.create(player));
            }
        }));

        contents.set(0, 5, ClickableItem.of(bookQuill, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                InvManager.open(player, SuccessInv.create(player, "아이템 이름 입력 창에 " + ChatColor.GREEN + "초대할 플레이어의 이름" + ChatColor.RESET + "를 입력한 뒤, " + ChatColor.RED + "오른쪽 아이템" + ChatColor.RESET + "을 꺼내주세요!", () -> {
                    new AnvilGUI.Builder()
                            .onClose(p -> {
                            })
                            .onComplete((p, text) -> {
                                try{
                                    Manager.getInstance().createMinefarmInvitation(player, text);
                                }
                                catch (RuntimeException e1){
                                    player.sendMessage(ChatColor.RED + e1.getMessage());
                                }
                                return AnvilGUI.Response.close();
                            })
                            .item(new ItemStack(Material.EMERALD_BLOCK))
                            .text("이름")
                            .plugin(Manager.getInstance().getPlugin())
                            .open(player);
                }));
            }
        }));
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
