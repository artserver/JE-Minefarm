package aren227.minefarm;

import aren227.minefarm.command.MinefarmCommand;
import aren227.minefarm.generator.MinefarmGenerator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {

    private Manager manager;

    @Override
    public void onEnable() {
        manager = new Manager(this);

        this.getCommand("mf").setExecutor(new MinefarmCommand(manager));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if(worldName.equals("minefarm")) return new MinefarmGenerator();
        return null;
    }
}
