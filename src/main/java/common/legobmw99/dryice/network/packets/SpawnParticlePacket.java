package common.legobmw99.dryice.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SpawnParticlePacket implements IMessage {

	public static class Handler implements IMessageHandler<SpawnParticlePacket, IMessage> {

		@Override
		public IMessage onMessage(final SpawnParticlePacket message, final MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					double d0 = (double) message.x;
					double d1 = (double) message.y;
					double d2 = (double) message.z;
					Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.CLOUD, d0 + Math.random(),
							d1 + 2 * Math.random(), d2 + Math.random(), 0.0D, 0.0D, 0.0D);
				}
			});
			return null;
		}
	}

	public SpawnParticlePacket() {
	}

	private int x, y, z;

	public SpawnParticlePacket(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = ByteBufUtils.readVarInt(buf, 5);
		y = ByteBufUtils.readVarInt(buf, 5);
		z = ByteBufUtils.readVarInt(buf, 5);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, x, 5);
		ByteBufUtils.writeVarInt(buf, y, 5);
		ByteBufUtils.writeVarInt(buf, z, 5);

	}

}
