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

import java.util.Arrays;

public class MinefarmMainInv implements InventoryProvider{

    Player player;

    public static SmartInventory create(Player player){
        player.closeInventory();
        return SmartInventory.builder()
                .provider(new MinefarmMainInv(player))
                .size(3, 9)
                .title("마인팜 메뉴")
                .build();
    }

    private MinefarmMainInv(Player player){
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
        //ItemStack sapling = getItemStackWithName(new ItemStack(Material.SAPLING), "마인팜 " + ChatColor.GREEN + "생성" + ChatColor.RESET + "하기");

        ItemStack ender = getItemStackWithName(new ItemStack(Material.ENDER_PEARL), "마인팜 " + ChatColor.LIGHT_PURPLE + "리프" + ChatColor.RESET + "하기");

        ItemStack grass = getItemStackWithName(new ItemStack(Material.GRASS), "마인팜으로 이동하기");

        ItemStack chest = getItemStackWithName(new ItemStack(Material.CHEST), "내가 가입한 " + ChatColor.GREEN + "마인팜 목록");

        ItemStack cart = getItemStackWithName(new ItemStack(Material.MINECART), "다른 마인팜에 " + ChatColor.GOLD + "방문" + ChatColor.RESET + "하기");

        /*ItemStack skull = new ItemStack(Material.SKULL_ITEM);
        skull.setDurability((short)3);
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        skullMeta.setDisplayName("섬원 목록 관리");
        skull.setItemMeta(skullMeta);
        if(minefarm == null) skull = getItemStackWithName(new ItemStack(Material.BARRIER), ChatColor.RED + "가입된 마인팜이 없으므로\n이용할 수 없습니다.");

        ItemStack nameTag = minefarm != null ? getItemStackWithName(new ItemStack(Material.NAME_TAG), "마인팜 이름 변경")
                : getItemStackWithName(new ItemStack(Material.BARRIER), ChatColor.RED + "가입된 마인팜이 없으므로\n이용할 수 없습니다.");*/

        ItemStack command = getItemStackWithName(new ItemStack(Material.BARRIER), ChatColor.RED + "자신의 마인팜에서만 사용 가능합니다.");
        Minefarm inMinefarm = Manager.getInstance().getInMinefarm(player);
        if(inMinefarm != null && inMinefarm.isMember(player.getUniqueId())){
            command = getItemStackWithName(new ItemStack(Material.COMMAND), "마인팜 설정/관리");
        }

        ItemStack bookQuill = getItemStackWithName(new ItemStack(Material.BOOK_AND_QUILL), "마인팜 초대장 : " + ChatColor.GREEN + Manager.getInstance().getMinefarmInvitations(player.getUniqueId()).size() + ChatColor.RESET + "장");

        ItemStack book = getItemStackWithName(new ItemStack(Material.BARRIER), ChatColor.RED + "거주하는 마인팜이 없습니다.");
        if(minefarm != null){
            book = getItemStackWithName(new ItemStack(Material.BOOK), "마인팜 ID : " + ChatColor.GOLD + MinefarmID.uuidToString(minefarm.getUniqueId()));
            ItemMeta itemMeta = book.getItemMeta();
            itemMeta.setLore(Arrays.asList(ChatColor.WHITE + "명성 : " + minefarm.getReputation()));
            book.setItemMeta(itemMeta);
        }

        /*contents.set(0, 3, ClickableItem.of(sapling, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
            }
        }));*/

        contents.set(0, 4, ClickableItem.of(ender, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                if(Manager.getInstance().getLeapCooldown(player.getUniqueId()) > 0){
                    InvManager.open(player, FailInv.create(player, "실패", "마인팜 리프는 " + ChatColor.RED + Time2String.getString(Manager.getInstance().getLeapCooldown(player.getUniqueId())) + ChatColor.RESET + "\n후 사용 가능합니다."));
                }
                else{
                    InvManager.open(player, MinefarmLeapInv.create(player));
                }
            }
        }));

        contents.set(1, 2, ClickableItem.of(grass, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                try{
                    Manager.getInstance().goToCurrentMinefarm(player);
                }
                catch (RuntimeException exception){
                    player.sendMessage(ChatColor.RED + exception.getMessage());
                }
            }
        }));

        contents.set(1, 3, ClickableItem.of(chest, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                InvManager.open(player, MinefarmRegisteredInv.create(player));
            }
        }));

        contents.set(1, 4, ClickableItem.of(cart, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                InvManager.open(player, SuccessInv.create(player, "사용 방법", ChatColor.WHITE + "아이템 이름 입력 창에\n" + ChatColor.GREEN + "마인팜 ID" + ChatColor.RESET + "를 입력한 뒤,\n" + ChatColor.RED + "오른쪽 아이템" + ChatColor.RESET + "을 꺼내주세요!", () -> {
                    new AnvilGUI.Builder()
                            .onClose(p -> {
                            })
                            .onComplete((p, text) -> {
                                try{
                                    Manager.getInstance().visitMinefarm(player, text);
                                }
                                catch (RuntimeException e1){
                                    player.sendMessage(ChatColor.RED + e1.getMessage());
                                }
                                return AnvilGUI.Response.close();
                            })
                            .item(new ItemStack(Material.EMERALD_BLOCK))
                            .text("ID")
                            .plugin(Manager.getInstance().getPlugin())
                            .open(player);
                }));
            }
        }));

        contents.set(1, 5, ClickableItem.of(bookQuill, e -> {
            if(e.isLeftClick()) {
                if(Manager.getInstance().getMinefarmInvitations(player.getUniqueId()).size() > 0){
                    InvManager.close(player);
                    InvManager.open(player, MinefarmInvitationListInv.create(player));
                }
            }
        }));

        contents.set(1, 6, ClickableItem.of(command, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                InvManager.open(player, MinefarmConfInv.create(player));
            }
            /*if(e.isLeftClick()) {
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
            }*/
        }));

        contents.set(2, 4, ClickableItem.of(book, e -> {
            if(e.isLeftClick()) {
                //InvManager.close(player);
            }
        }));
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
