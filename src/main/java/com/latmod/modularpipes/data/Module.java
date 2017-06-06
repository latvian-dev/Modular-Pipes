package com.latmod.modularpipes.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public class Module
{
    public static final Module EMPTY = new Module()
    {
        @Override
        public boolean isEmpty()
        {
            return true;
        }
    };

    public Module()
    {
    }

    public boolean isEmpty()
    {
        return false;
    }

    public ModuleData createData(ModuleContainer container)
    {
        return NoData.INSTANCE;
    }

    public FilterConfig createFilterConfig(ModuleContainer container)
    {
        return new FilterConfig();
    }

    public boolean insertInPipe(ModuleContainer container, EntityPlayer player)
    {
        return true;
    }

    public void removeFromPipe(ModuleContainer container, EntityPlayer player)
    {
    }

    public void pipeBroken(ModuleContainer container)
    {
    }

    public void update(ModuleContainer container)
    {
    }

    public boolean onRightClick(ModuleContainer container, EntityPlayer player, EnumHand hand)
    {
        return false;
    }
}