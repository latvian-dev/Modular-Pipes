package com.latmod.modularpipes.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * @author LatvianModder
 */
public interface IPipeBlock
{
    boolean isNode(IBlockAccess world, BlockPos pos, IBlockState state);

    float getSpeedModifier(IBlockAccess world, BlockPos pos, IBlockState state);

    boolean canPipeConnect(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing);

    EnumFacing getItemDirection(IBlockAccess world, BlockPos pos, IBlockState state, TransportedItem item, EnumFacing source);
}