package dev.latvian.mods.modularpipes.net;

import dev.latvian.mods.modularpipes.ModularPipes;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class ParticleMessage extends BaseS2CMessage {
	public final double x, y, z;
	public final int type;

	public ParticleMessage(double _x, double _y, double _z, int t) {
		x = _x;
		y = _y;
		z = _z;
		type = t;
	}

	public ParticleMessage(FriendlyByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		type = buf.readVarInt();
	}

	@Override
	public MessageType getType() {
		return ModularPipesNet.PARTICLE;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeVarInt(type);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		ModularPipes.PROXY.spawnParticle(x, y, z, type);
	}
}