package common.legobmw99.dryice;

import common.legobmw99.dryice.block.BlockDryIce;
import common.legobmw99.dryice.network.packets.SpawnParticlePacket;
import common.legobmw99.dryice.world.Generator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = DryIce.MODID, version = DryIce.VERSION)
public class DryIce {
	public static final String MODID = "dryice";
	public static final String VERSION = "@VERSION@";
	public static BlockDryIce dryice;
	public static SimpleNetworkWrapper network;

	@Instance(value = "dryice")
	public static DryIce instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		dryice = new BlockDryIce();
		GameRegistry.registerBlock(dryice, "dryice");
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel("dryice");
		network.registerMessage(SpawnParticlePacket.Handler.class, SpawnParticlePacket.class, 0, Side.CLIENT);
		
		GameRegistry.registerWorldGenerator(new Generator(), 0);


	}

	@EventHandler
	public void load(FMLInitializationEvent event) {

		if (event.getSide() == Side.CLIENT) {
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

			renderItem.getItemModelMesher().register(Item.getItemFromBlock(dryice), 0,
					new ModelResourceLocation("dryice:dryice", "inventory"));
		}
	}

}
