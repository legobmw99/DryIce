package common.legobmw99.dryice.world;

import java.util.Random;

import common.legobmw99.dryice.DryIce;
import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class Generator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		int x, y, z, num;

		// generate in overworld
		if (world.provider.getDimensionId() == 0) {

			BlockPos center = new BlockPos((16 * chunkX) + 8, 64, (16 * chunkZ) + 8);

			String biome = world.getBiomeGenForCoords(center).biomeName;

			// only generate in ice spike biomes
			if (biome.toLowerCase().contains("spike")) {

				x = random.nextInt(16);
				z = random.nextInt(16);
				x = x + (16 * chunkX);
				z = z + (16 * chunkZ);

				BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));

				(new WorldGenIcePath2(5)).generate(world, random, pos);

			}
		}

		// generate in end
		if (world.provider.getDimensionId() == 1) {
			if (random.nextInt(88) == 4) {
				x = random.nextInt(16);
				z = random.nextInt(16);
				y = random.nextInt(80);
				x = x + (16 * chunkX);
				z = z + (16 * chunkZ);
				BlockPos pos = new BlockPos(x, y, z);
				num = random.nextInt(3);
				(new WorldGenBlockBlob2(DryIce.dryice, num)).generate(world, random, pos);

			}
		}
	}
	
	//Modified from WorldGenIcePath in vanilla
	private class WorldGenIcePath2 extends WorldGenerator {
		private Block block = DryIce.dryice;
		private int basePathWidth;

		public WorldGenIcePath2(int size) {
			this.basePathWidth = size;
		}

		public boolean generate(World worldIn, Random rand, BlockPos position) {

			while (worldIn.isAirBlock(position) && position.getY() > 2) {
				position = position.down();
			}


			int i = rand.nextInt(this.basePathWidth - 2) + 2;
			int j = 1;

			for (int k = position.getX() - i; k <= position.getX() + i; ++k) {
				for (int l = position.getZ() - i; l <= position.getZ() + i; ++l) {
					int i1 = k - position.getX();
					int j1 = l - position.getZ();

					if (i1 * i1 + j1 * j1 <= i * i) {

						for (int k1 = position.getY() - j; k1 <= position.getY() + j; ++k1) {
							BlockPos blockpos = new BlockPos(k, k1, l);
							Block block = worldIn.getBlockState(blockpos).getBlock();

							if (block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
								worldIn.setBlockState(blockpos, this.block.getDefaultState(), 2);
							}
						}
					}
				}
			}

			return true;
		}
	}
	
	//Modified from WorldGenBlockBlob in vanilla
	public class WorldGenBlockBlob2 extends WorldGenerator {
		private final Block block;
		private final int rad;

		public WorldGenBlockBlob2(Block b, int r) {
			super(false);
			this.block = b;
			this.rad = r;
		}

		public boolean generate(World worldIn, Random rand, BlockPos position) {
			while (true) {

				int i1 = this.rad;

				for (int i = 0; i1 >= 0 && i < 3; ++i) {
					int j = i1 + rand.nextInt(2);
					int k = i1 + rand.nextInt(2);
					int l = i1 + rand.nextInt(2);
					float f = (float) (j + k + l) * 0.333F + 0.5F;

					for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
						if (blockpos.distanceSq(position) <= (double) (f * f)) {
							worldIn.setBlockState(blockpos, this.block.getDefaultState(), 4);
						}
					}

					position = position.add(-(i1 + 1) + rand.nextInt(2 + i1 * 2), 0 - rand.nextInt(2),
							-(i1 + 1) + rand.nextInt(2 + i1 * 2));
				}

				return true;

			}
		}
	}
}