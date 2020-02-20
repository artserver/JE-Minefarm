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
        if(minefarm == null) return;

        ItemStack chest = getItemStackWithName(new ItemStack(Material.CHEST), "마인팜 유저 목록");

        ItemStack nametag = getItemStackWithName(new ItemStack(Material.NAME_TAG), "마인팜 이름 변경");

        ItemStack bed = getItemStackWithName(new ItemStack(Material.BED), "마인팜 스폰지점 설정");

        ItemStack door = getItemStackWithName(new ItemStack(Material.BARRIER), "마인팜 방문 가능 여부 : " + ChatColor.RED + "거부");
        if(minefarm.getCanVisit()){
            door = getItemStackWithName(new ItemStack(Material.IRON_DOOR), "마인팜 방문 가능 여부 : " + ChatColor.GREEN + "허용");
        }

        ItemStack random = getItemStackWithName(new ItemStack(minefarm.getIcon()), "마인팜 아이콘 변경");

        ItemStack bookQuill = getItemStackWithName(new ItemStack(Material.BOOK_AND_QUILL), "초대장 보내기");

        ItemStack quit = getItemStackWithName(new ItemStack(Material.BARRIER), "마인팜 나가기");

        contents.set(1, 0, ClickableItem.of(chest, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                InvManager.open(player, MinefarmMembersInv.create(player));
            }
        }));

        contents.set(1, 1, ClickableItem.of(nametag, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                if(minefarm.isOp(player.getUniqueId())){
                    InvManager.open(player, SuccessInv.create(player, "사용 방법", "아이템 이름 입력 창에\n" + ChatColor.GREEN + "마인팜 이름" + ChatColor.RESET + "을 입력한 뒤,\n" + ChatColor.RED + "오른쪽 아이템" + ChatColor.RESET + "을 꺼내주세요!", () -> {
                        new AnvilGUI.Builder()
                                .onClose(p -> {
                                })
                                .onComplete((p, text) -> {
                                    try{
                                        minefarm.setName(text);
                                        player.sendMessage("마인팜 이름을 " + ChatColor.GREEN + text + ChatColor.RESET + "로 변경했습니다!");
                                    }
                                    catch (RuntimeException e1){
                                        player.sendMessage(ChatColor.RED + e1.getMessage());
                                    }
                                    return AnvilGUI.Response.close();
                                })
                                .item(new ItemStack(Material.EMERALD_BLOCK))
                                .text(minefarm.getName())
                                .plugin(Manager.getInstance().getPlugin())
                                .open(player);
                    }));
                }
                else{
                    InvManager.open(player, FailInv.create(player, "사용 불가", ChatColor.WHITE + "권한이 없습니다."));
                }
            }
        }));

        contents.set(1, 2, ClickableItem.of(bed, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                if(minefarm.isOp(player.getUniqueId())){
                    InvManager.open(player, YesNoInv.create(player, "스폰 지점 설정", "지금 서 있는 이곳을\n스폰지점으로 설정할까요?", () -> {
                        minefarm.setSpawnLocation(player.getLocation());
                        player.sendMessage("스폰지점을 " + ChatColor.GREEN + "변경" + ChatColor.RESET + "했습니다.");
                    }, null));
                }
                else{
                    InvManager.open(player, FailInv.create(player, "사용 불가", ChatColor.WHITE + "권한이 없습니다."));
                }
            }
        }));

        contents.set(1, 3, ClickableItem.of(door, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                if(minefarm.isOp(player.getUniqueId())){
                    minefarm.setCanVisit(!minefarm.getCanVisit());
                    InvManager.open(player, MinefarmConfInv.create(player));
                }
                else{
                    InvManager.open(player, FailInv.create(player, "사용 불가", ChatColor.WHITE + "권한이 없습니다."));
                }
            }
        }));

        contents.set(1, 4, ClickableItem.of(random, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                if(minefarm.isOp(player.getUniqueId())){
                    InvManager.open(player, MinefarmSelectIconInv.create(player, 0));
                }
                else{
                    InvManager.open(player, FailInv.create(player, "사용 불가", ChatColor.WHITE + "권한이 없습니다."));
                }

            }
        }));

        contents.set(1, 5, ClickableItem.of(bookQuill, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                InvManager.open(player, SuccessInv.create(player, "사용 방법", "아이템 이름 입력 창에\n" + ChatColor.GREEN + "초대할 플레이어의 이름" + ChatColor.RESET + "를 입력한 뒤,\n" + ChatColor.RED + "오른쪽 아이템" + ChatColor.RESET + "을 꺼내주세요!", () -> {
                    new AnvilGUI.Builder()
                            .onClose(p -> {
                            })
                            .onComplete((p, text) -> {
                                try{
                                    Manager.getInstance().createMinefarmInvitation(player, text);
                                    player.sendMessage(ChatColor.GREEN + text + ChatColor.RESET + "님에게 초대장을 발송했습니다!");
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

        contents.set(1, 6, ClickableItem.of(quit, e -> {
            if(e.isLeftClick()) {
                InvManager.close(player);
                if(Manager.getInstance().getLeapCooldown(player.getUniqueId()) > 0){
                    InvManager.open(player, FailInv.create(player, "경고!", ChatColor.WHITE + "아직 마인팜 리프 쿨타임\n" + ChatColor.WHITE + "(" + Time2String.getString(Manager.getInstance().getLeapCooldown(player.getUniqueId())) + ")\n" + ChatColor.WHITE + "이 끝나지 않았습니다!\n" + ChatColor.WHITE + "탈퇴는 가능하지만 리프는 불가능하므로\n" + ChatColor.RED + "쿨타임이 끝날 때까지 다른 마인팜에 입장할 수 없습니다!\n" + ChatColor.WHITE + "그래도 계속하시겠습니까?", () -> {
                        InvManager.close(player);
                        InvManager.open(player, YesNoInv.create(player, "마인팜 나가기", "정말 마인팜을 나가시겠습니까?", () -> {
                            minefarm.removePlayer(player.getUniqueId());
                            if(Manager.getInstance().getInMinefarm(player).getUniqueId().equals(minefarm.getUniqueId())) player.teleport(Manager.getInstance().getDefaultWorld().getSpawnLocation());
                            player.sendMessage("마인팜 " + ChatColor.GREEN + minefarm.getName() + ChatColor.RESET + "을 " + ChatColor.RED + "떠났습니다.");
                        }, null));
                    }));
                }
                else if(Manager.getInstance().getMinefarms(player.getUniqueId()).size() <= 1){
                    InvManager.open(player, FailInv.create(player, "경고!", ChatColor.WHITE + "이 마인팜을 떠나면\n" + ChatColor.WHITE + "거주 할 마인팜이 없습니다!\n" + ChatColor.WHITE + "그래도 계속하시겠습니까?", () -> {
                        InvManager.close(player);
                        InvManager.open(player, YesNoInv.create(player, "마인팜 나가기", "정말 마인팜을 나가시겠습니까?", () -> {
                            minefarm.removePlayer(player.getUniqueId());
                            if(Manager.getInstance().getInMinefarm(player).getUniqueId().equals(minefarm.getUniqueId())) player.teleport(Manager.getInstance().getDefaultWorld().getSpawnLocation());
                            player.sendMessage("마인팜 " + ChatColor.GREEN + minefarm.getName() + ChatColor.RESET + "을 " + ChatColor.RED + "떠났습니다.");
                        }, null));
                    }));
                }
                else{
                    InvManager.open(player, YesNoInv.create(player, "마인팜 나가기", "정말 마인팜을 나가시겠습니까?", () -> {
                        minefarm.removePlayer(player.getUniqueId());
                        if(Manager.getInstance().getInMinefarm(player).getUniqueId().equals(minefarm.getUniqueId())) player.teleport(Manager.getInstance().getDefaultWorld().getSpawnLocation());
                        player.sendMessage("마인팜 " + ChatColor.GREEN + minefarm.getName() + ChatColor.RESET + "을 " + ChatColor.RED + "떠났습니다.");
                    }, null));
                }
            }
        }));
    }


    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
