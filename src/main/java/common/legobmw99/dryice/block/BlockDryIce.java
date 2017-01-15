package common.legobmw99.dryice.block;

import java.util.Random;

import common.legobmw99.dryice.DryIce;
import common.legobmw99.dryice.network.packets.SpawnParticlePacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockDryIce extends Block {

	public BlockDryIce() {

		super(Material.ice);
		this.slipperiness = 0.98F;
		this.setTickRandomly(true);
		this.setUnlocalizedName("dryice");
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setLightOpacity(3);
		this.setHardness(0.5F);
		this.setStepSound(soundTypeGlass);

	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        float f = 0.0625F;
        return new AxisAlignedBB((double)((float)pos.getX() + f), (double)pos.getY(), (double)((float)pos.getZ() + f), (double)((float)(pos.getX() + 1) - f), (double)((float)(pos.getY() + 1) - f), (double)((float)(pos.getZ() + 1) - f));
    }
	
	@Override
    public boolean isFullCube()
    {
        return false;
    }
	
	@Override
    protected boolean canSilkHarvest(){
		return true;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
		player.addExhaustion(0.025F);

		if (this.canSilkHarvest(worldIn, pos, worldIn.getBlockState(pos), player)
				&& EnchantmentHelper.getSilkTouchModifier(player)) {
			java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
			ItemStack itemstack = this.createStackedBlock(state);

			if (itemstack != null)
				items.add(itemstack);

			net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos,
					worldIn.getBlockState(pos), 0, 1.0f, true, player);

			for (ItemStack is : items)
				spawnAsEntity(worldIn, pos, is);
		} else {
			triggerEffects(worldIn,pos);
		}

	}
	

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.provider.getDimensionId() == 1
				|| worldIn.getBiomeGenForCoords(pos).biomeName.toLowerCase().contains("ice")
				|| worldIn.getBiomeGenForCoords(pos).biomeName.toLowerCase().contains("cold")) {
			
			if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - this.getLightOpacity()) {
				worldIn.setBlockToAir(pos);
				this.triggerEffects(worldIn, pos);
			}
		} else if (worldIn.provider.getDimensionId() == -1) {
			worldIn.setBlockToAir(pos);
			worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3F, true);

		} else {
			if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 7 - this.getLightOpacity()) {
				worldIn.setBlockToAir(pos);
				this.triggerEffects(worldIn, pos);

			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (entityIn instanceof EntityEnderman || entityIn instanceof EntitySkeleton
				|| entityIn instanceof EntityGuardian) {
			return;
		}
		if (entityIn instanceof EntityGhast || entityIn instanceof EntityBlaze || entityIn instanceof EntityMagmaCube) {
			entityIn.attackEntityFrom(DamageSource.generic, 3.0F);
		} else {
			entityIn.attackEntityFrom(DamageSource.generic, 1.0F);
		}
	}
	
	@Override
    public int quantityDropped(Random random)
    {
        return 0;
    }

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		this.checkForMixing(worldIn, pos, state);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		this.checkForMixing(worldIn, pos, state);
	}

	public boolean checkForMixing(World worldIn, BlockPos pos, IBlockState state) {
		IBlockState[] states = { Blocks.ice.getDefaultState(), Blocks.obsidian.getDefaultState(),
				Blocks.cobblestone.getDefaultState() };
		int type = 0;
		EnumFacing face = null;
		for (EnumFacing enumfacing : EnumFacing.values()) {
			if (enumfacing != EnumFacing.DOWN
					&& worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getMaterial() == Material.water) {
				type = 1;
				face = enumfacing;
				break;
			} else if (enumfacing != EnumFacing.DOWN
					&& worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getMaterial() == Material.lava) {

				type = 2;

				System.out.println(worldIn.getBlockState(pos.offset(enumfacing)).getValue(BlockLiquid.LEVEL));
				if (worldIn.getBlockState(pos.offset(enumfacing)).getValue(BlockLiquid.LEVEL) >= 4) {
					type = 3;
				}
				face = enumfacing;
				break;
			}
		}

		if (type > 0) {
			worldIn.setBlockState(pos.offset(face), states[type - 1]);
			if (type > 1) {
				worldIn.setBlockToAir(pos);
				worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3F, true);
			}
			this.triggerEffects(worldIn, pos);
			return true;
		}

		return false;
	}

	protected void triggerEffects(World worldIn, BlockPos pos) {
		double d0 = (double) pos.getX();
		double d1 = (double) pos.getY();
		double d2 = (double) pos.getZ();
		worldIn.playSoundEffect(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "random.fizz", 0.5F,
				2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

		for (int i = 0; i < 120; i++) {
			DryIce.network.sendToAll(new SpawnParticlePacket(pos.getX(),pos.getY(),pos.getZ()));
		}
	}
}
