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

import java.util.Arrays;
import java.util.List;

public class MinefarmInvitationInv implements InventoryProvider {

    Player player;
    MinefarmInvitation invitation;

    public static SmartInventory create(Player player, MinefarmInvitation invitation){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmInvitationInv(player, invitation))
                .size(3, 9)
                .title(ChatColor.GREEN + Manager.getInstance().getPlugin().getServer().getOfflinePlayer(invitation.from).getName() + ChatColor.RESET + "님의 초대장")
                .build();
    }

    private MinefarmInvitationInv(Player player, MinefarmInvitation invitation){
        this.player = player;
        this.invitation = invitation;
    }

    private ItemStack getItemStackWithName(ItemStack itemStack, String name){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Minefarm minefarm = Manager.getInstance().getMinefarmByUuid(invitation.minefarmUuid);
        if(minefarm != null){
            ItemStack grass = getItemStackWithName(new ItemStack(minefarm.getIcon()), minefarm.getName());
            ItemMeta meta = grass.getItemMeta();
            meta.setLore(Arrays.asList(ChatColor.WHITE + "인원: " + minefarm.getPlayers().size(), ChatColor.WHITE + "마인팜 ID: " + ChatColor.GOLD + MinefarmID.uuidToString(minefarm.getUniqueId()), ChatColor.WHITE + "명성 : " + minefarm.getReputation()));
            grass.setItemMeta(meta);

            ItemStack no = getItemStackWithName(new ItemStack(Material.REDSTONE_BLOCK), ChatColor.RED + "거부");

            ItemStack yes = getItemStackWithName(new ItemStack(Material.EMERALD_BLOCK), ChatColor.GREEN + "수락");

            contents.set(0, 4, ClickableItem.empty(grass));

            contents.set(2, 3, ClickableItem.of(no, e -> {
                if(e.isLeftClick()) {
                    InvManager.close(player);
                    try{
                        Manager.getInstance().processMinefarmInvitation(player, invitation, false);
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
                        Manager.getInstance().processMinefarmInvitation(player, invitation, true);
                    }
                    catch (RuntimeException e1){
                        player.sendMessage(ChatColor.RED + e1.getMessage());
                    }
                }
            }));
        }
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
