package com.latmod.modularpipes.item;

import com.latmod.modularpipes.block.BlockBasicPipe;
import com.latmod.modularpipes.block.BlockController;
import com.latmod.modularpipes.block.BlockPipe;
import com.latmod.modularpipes.item.module.ModuleBlockItems;
import com.latmod.modularpipes.item.module.ModuleExtract;
import com.latmod.modularpipes.item.module.ModuleRightClickExtract;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipesItems
{
    public static final BlockBasicPipe PIPE_BASIC = new BlockBasicPipe("pipe_basic");
    public static final BlockPipe PIPE = new BlockPipe("pipe");
    public static final BlockController CONTROLLER = new BlockController("controller");

    public static final ItemBase MODULE = new ItemBase("module");
    public static final List<ItemModule> MODULE_LIST = new ArrayList<>();

    static
    {
        MODULE_LIST.add(new ItemModule("extract", new ModuleExtract()));
        MODULE_LIST.add(new ItemModule("rightclick_extract", new ModuleRightClickExtract()));
        MODULE_LIST.add(new ItemModule("block_items", new ModuleBlockItems()));
    }
}