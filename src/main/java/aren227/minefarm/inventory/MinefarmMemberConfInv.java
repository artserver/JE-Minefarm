package aren227.minefarm.inventory;

import aren227.minefarm.Manager;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.MinefarmID;
import aren227.minefarm.util.MinefarmInvitation;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class MinefarmMemberConfInv implements InventoryProvider {

    Player player;
    OfflinePlayer who;

    public static SmartInventory create(Player player, OfflinePlayer who){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmMemberConfInv(player, who))
                .size(3, 9)
                .title("유저 관리 - " + who.getName())
                .build();
    }

    private MinefarmMemberConfInv(Player player, OfflinePlayer who){
        this.player = player;
        this.who = who;
    }

    private ItemStack getItemStackWithName(ItemStack itemStack, String name){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack getSkull(OfflinePlayer player){
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getName());
        ItemStack stack = new ItemStack(Material.SKULL_ITEM,1 , (byte)3);
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Minefarm minefarm = Manager.getInstance().getCurrentMinefarm(player.getUniqueId());
        if(minefarm != null){
            ItemStack skull = getSkull(who);

            ItemStack toggle = getItemStackWithName(new ItemStack(Material.REDSTONE_BLOCK), "관리자 권한 : " + ChatColor.RED + "없음");
            if(minefarm.isOp(who.getUniqueId())){
                toggle = getItemStackWithName(new ItemStack(Material.EMERALD_BLOCK), "관리자 권한 : " + ChatColor.GREEN + "있음");
            }

            ItemStack kick = getItemStackWithName(new ItemStack(Material.BARRIER), ChatColor.RED + "강퇴하기");

            contents.set(0, 4, ClickableItem.empty(skull));

            contents.set(2, 3, ClickableItem.of(toggle, e -> {
                if(e.isLeftClick()) {
                    try{
                        InvManager.close(player);
                        if(minefarm.isOp(who.getUniqueId())){
                            InvManager.open(player, YesNoInv.create(player, "관리자 권한 설정", ChatColor.GREEN + who.getName() + ChatColor.WHITE + "님의\n" + ChatColor.WHITE + "관리자 권한을 삭제할까요?", () -> {
                                minefarm.removeOp(who.getUniqueId());
                            }, null));
                        }
                        else{
                            InvManager.open(player, YesNoInv.create(player, "관리자 권한 설정", ChatColor.GREEN + who.getName() + ChatColor.WHITE + "님에게\n" + ChatColor.WHITE + "관리자 권한을 부여할까요?", () -> {
                                minefarm.addOp(who.getUniqueId());
                            }, null));
                        }

                    }
                    catch (RuntimeException e1){
                        player.sendMessage(ChatColor.RED + e1.getMessage());
                    }
                    InvManager.close(player);
                }
            }));

            contents.set(2, 5, ClickableItem.of(kick, e -> {
                if(e.isLeftClick()) {
                    InvManager.close(player);
                    InvManager.open(player, YesNoInv.create(player, "추방하기", ChatColor.GREEN + who.getName() + ChatColor.WHITE + "님을\n" + ChatColor.WHITE + "추방할까요?", () -> {
                        minefarm.removePlayer(who.getUniqueId());
                        Player online = Manager.getInstance().getPlugin().getServer().getPlayer(who.getName());
                        if(online != null){
                            if(Manager.getInstance().getInMinefarm(online).getUniqueId().equals(minefarm.getUniqueId())) online.teleport(Manager.getInstance().getDefaultWorld().getSpawnLocation());
                            online.sendMessage("마인팜 " + ChatColor.GREEN + minefarm.getName() + ChatColor.RESET + "에서 " + ChatColor.RED + "추방" + ChatColor.RESET + "되었습니다.");
                        }
                        player.sendMessage(ChatColor.GREEN + who.getName() + ChatColor.WHITE + "님을 " + ChatColor.RED + "추방" + ChatColor.WHITE + "했습니다.");
                    }, null));
                }
            }));
        }
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
