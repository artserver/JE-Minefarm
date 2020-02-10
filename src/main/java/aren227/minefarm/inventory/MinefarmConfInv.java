package aren227.minefarm.inventory;

import aren227.minefarm.Manager;
import aren227.minefarm.minefarm.Minefarm;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MinefarmConfInv implements InventoryProvider{

    Player player;

    enum Page {MAIN}
    Page status;

    public static SmartInventory create(Player player){
        return SmartInventory.builder()
                .provider(new MinefarmConfInv(player))
                .size(3, 9)
                .title("마인팜 설정")
                .build();
    }

    private MinefarmConfInv(Player player){
        this.player = player;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        changePage(Page.MAIN, contents);
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

    public void changePage(Page page, InventoryContents contents){
        if(status == page) return;

        switch(page){
            case MAIN:
                ItemStack nameTag = new ItemStack(Material.NAME_TAG);
                nameTag.getItemMeta().setDisplayName("마인팜 이름 변경");

                contents.set(0, 0, ClickableItem.of(nameTag, e -> {
                    if(e.isLeftClick()) {
                        new AnvilGUI.Builder()
                                .onClose(player -> {
                                    player.sendMessage("You closed the inventory.");
                                })
                                .onComplete((player, text) -> {
                                    Minefarm minefarm = Manager.getInstance().getMinefarm(player);
                                    minefarm.setName(text);
                                    return AnvilGUI.Response.close();
                                })
                                .title("")
                                .item(new ItemStack(Material.EMERALD_BLOCK))
                                .plugin(Manager.getInstance().getPlugin())
                                .open(player);
                    }
                }));
        }

        status = page;
    }

}
