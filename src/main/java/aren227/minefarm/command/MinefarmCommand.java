package aren227.minefarm.command;

import aren227.minefarm.Manager;
import aren227.minefarm.inventory.InvManager;
import aren227.minefarm.inventory.MinefarmMainInv;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MinefarmCommand implements CommandExecutor {

    private Manager manager;

    public MinefarmCommand(Manager manager){
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            //manager.goToMinefarm((Player)sender);
            InvManager.open((Player)sender, MinefarmMainInv.create((Player)sender));
        }
        return true;
    }

}
