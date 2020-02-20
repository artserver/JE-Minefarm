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

public class YesNoInv implements InventoryProvider{

    Player player;
    String title;
    String question;
    Runnable yRunnable;
    Runnable nRunnable;

    public static SmartInventory create(Player player, String title, String question, Runnable yRunnable, Runnable nRunnable){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new YesNoInv(player, title, question, yRunnable, nRunnable))
                .size(3, 9)
                .title(title)
                .build();
    }

    private YesNoInv(Player player, String title, String question, Runnable yRunnable, Runnable nRunnable){
        this.player = player;
        this.title = title;
        this.question = question;
        this.yRunnable = yRunnable;
        this.nRunnable = nRunnable;
    }

    private ItemStack getItemStackWithName(ItemStack itemStack, String name){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta itemMeta = book.getItemMeta();
        itemMeta.setDisplayName(title);
        itemMeta.setLore(Arrays.asList(question.split("\n")));
        book.setItemMeta(itemMeta);

        ItemStack no = getItemStackWithName(new ItemStack(Material.REDSTONE_BLOCK), ChatColor.RED + "아니오");

        ItemStack yes = getItemStackWithName(new ItemStack(Material.EMERALD_BLOCK), ChatColor.GREEN + "예");

        contents.set(0, 4, ClickableItem.empty(book));

        contents.set(2, 3, ClickableItem.of(no, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                try{
                    if(nRunnable != null) nRunnable.run();
                }
                catch (RuntimeException e1){
                    player.sendMessage(ChatColor.RED + e1.getMessage());
                }
            }
        }));

        contents.set(2, 5, ClickableItem.of(yes, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                try{
                    if(yRunnable != null) yRunnable.run();
                }
                catch (RuntimeException e1){
                    player.sendMessage(ChatColor.RED + e1.getMessage());
                }
            }
        }));
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
