package aren227.minefarm.generator;

import aren227.minefarm.Manager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class MinefarmGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);

        if(chunkX < 0 || chunkZ < 0) return chunk;

        chunkX %= Manager.MINEFARM_CHUNK_DIST;
        chunkZ %= Manager.MINEFARM_CHUNK_DIST;

        chunkX -= (Manager.MINEFARM_CHUNK_DIST / 2) - 1;
        chunkZ -= (Manager.MINEFARM_CHUNK_DIST / 2) - 1;

        //오직 중앙의 4청크에만 생성
        if(chunkX < 0 || chunkX > 1 || chunkZ < 0 || chunkZ > 1) return chunk;

        createMinefarm(chunk, chunkX, chunkZ);

        return chunk;
    }

    private void createMinefarm(ChunkData chunk, int rX, int rZ){
        //TODO: 템플릿 사용
        for(int i = -4; i <= 4; i++){
            for(int j = -4; j <= 4; j++){
                int x = 16 + i, z = 16 + j;

                if(rX * 16 <= x && x < (rX + 1) * 16 && rZ * 16 <= z && z < (rZ + 1) * 16){
                    chunk.setBlock(x % 16, Manager.MINEFARM_Y, z % 16, Material.GRASS);
                    chunk.setBlock(x % 16, Manager.MINEFARM_Y - 1, z % 16, Material.DIRT);
                    chunk.setBlock(x % 16, Manager.MINEFARM_Y - 2, z % 16, Material.DIRT);
                }
            }
        }

        if(rX * 16 <= 16 && 16 < (rX + 1) * 16 && rZ * 16 <= 16 && 16 < (rZ + 1) * 16){
            chunk.setBlock(0, Manager.MINEFARM_Y, 0, Material.STRUCTURE_BLOCK);
        }
    }

}
