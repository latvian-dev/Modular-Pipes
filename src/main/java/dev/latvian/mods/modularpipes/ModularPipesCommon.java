package dev.latvian.mods.modularpipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModularPipesCommon {
	public static final int EXPLOSION = 0;
	public static final int SPARK = 1;

	public void spawnParticle(BlockPos pos, @Nullable Direction facing, int type) {
	}

	public int getPipeLightValue(BlockAndTintGetter world) {
		return 0;
	}
}