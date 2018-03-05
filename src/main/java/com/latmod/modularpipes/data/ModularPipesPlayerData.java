package com.latmod.modularpipes.data;

import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.latmod.modularpipes.ModularPipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author LatvianModder
 */
public class ModularPipesPlayerData implements INBTSerializable<NBTTagCompound>
{
	public static ModularPipesPlayerData get(ForgePlayer player)
	{
		return player.getData().get(ModularPipes.MOD_ID);
	}

	public final ConfigBoolean devMode = new ConfigBoolean(false)
	{
		@Override
		public void setBoolean(boolean v)
		{
			super.setBoolean(v);

			if (player.isOnline())
			{
				PipeNetwork.get(player.getPlayer().world).markDirty();
			}
		}
	};

	private final ForgePlayer player;

	public ModularPipesPlayerData(ForgePlayer p)
	{
		player = p;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		if (devMode.getBoolean())
		{
			nbt.setBoolean("Dev", true);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		devMode.setBoolean(nbt.getBoolean("Dev"));
	}
}