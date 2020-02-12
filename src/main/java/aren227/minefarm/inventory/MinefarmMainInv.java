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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class MinefarmMainInv implements InventoryProvider{

    Player player;

    public static SmartInventory create(Player player){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmMainInv(player))
                .size(2, 9)
                .title("마인팜")
                .build();
    }

    private MinefarmMainInv(Player player){
        this.player = player;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        final Minefarm minefarm = Manager.getInstance().getCurrentMinefarm(player.getUniqueId());

        ItemStack grass = new ItemStack(Material.GRASS);
        ItemMeta itemMeta = grass.getItemMeta();
        itemMeta.setDisplayName("마인팜으로 이동");
        grass.setItemMeta(itemMeta);

        ItemStack cart = new ItemStack(Material.MINECART);
        itemMeta = cart.getItemMeta();
        itemMeta.setDisplayName("다른 마인팜에 " + ChatColor.GOLD + "방문" + ChatColor.RESET + "하기");
        cart.setItemMeta(itemMeta);

        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        itemMeta = pickaxe.getItemMeta();
        itemMeta.setDisplayName("다른 마인팜에 " + ChatColor.GREEN + "가입" + ChatColor.RESET + "하기");
        pickaxe.setItemMeta(itemMeta);

        ItemStack skull = new ItemStack(Material.SKULL_ITEM);
        skull.setDurability((short)3);
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        skullMeta.setDisplayName("섬원 목록 관리");
        skull.setItemMeta(skullMeta);

        ItemStack nameTag = new ItemStack(Material.NAME_TAG);
        itemMeta = nameTag.getItemMeta();
        itemMeta.setDisplayName("마인팜 이름 변경");
        nameTag.setItemMeta(itemMeta);

        contents.set(0, 2, ClickableItem.of(grass, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                Manager.getInstance().goToCurrentMinefarm(player);
            }
        }));

        contents.set(0, 3, ClickableItem.of(cart, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
            }
        }));

        contents.set(0, 4, ClickableItem.of(pickaxe, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
            }
        }));

        contents.set(0, 5, ClickableItem.of(skull, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
            }
        }));

        contents.set(0, 6, ClickableItem.of(nameTag, e -> {
            if(e.isLeftClick()) {
                new AnvilGUI.Builder()
                        .onClose(p -> {
                            InvManager.open(p, MinefarmMainInv.create(p));
                        })
                        .onComplete((p, text) -> {
                            minefarm.setName(text);
                            return AnvilGUI.Response.close();
                        })
                        .item(new ItemStack(Material.EMERALD_BLOCK))
                        .text(minefarm.getName())
                        .plugin(Manager.getInstance().getPlugin())
                        .open(player);
            }
        }));
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
