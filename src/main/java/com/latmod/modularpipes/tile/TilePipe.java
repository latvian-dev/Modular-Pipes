package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.api.IPipeBlock;
import com.latmod.modularpipes.api.ModuleContainer;
import com.latmod.modularpipes.api.TransportedItem;
import com.latmod.modularpipes.util.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class TilePipe extends TilePipeNetBase
{
    private int tier;
    private int connections = -1;
    public final ModuleContainerImpl[] modules;

    public TilePipe()
    {
        this(0, 0);
    }

    public TilePipe(int dim, int t)
    {
        super(dim);
        tier = t;
        modules = new ModuleContainerImpl[6];

        for(int i = 0; i < 6; i++)
        {
            modules[i] = new ModuleContainerImpl(this, EnumFacing.VALUES[i], ItemStack.EMPTY);
        }
    }

    public int getTier()
    {
        return tier;
    }

    public void clearModules()
    {
        for(int i = 0; i < 6; i++)
        {
            modules[i].setStack(ItemStack.EMPTY);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("Tier", (byte) tier);
        nbt.setByte("Connections", (byte) connections);
        NBTTagList moduleList = new NBTTagList();

        for(ModuleContainer c : modules)
        {
            moduleList.appendTag(ModuleContainerImpl.writeToNBT(c));
        }

        nbt.setTag("Modules", moduleList);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        tier = nbt.getByte("Tier");
        connections = nbt.getByte("Connections") & 0xFF;

        clearModules();

        NBTTagList moduleList = nbt.getTagList("Modules", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < moduleList.tagCount(); i++)
        {
            ModuleContainerImpl c = new ModuleContainerImpl(this, moduleList.getCompoundTagAt(i));
            modules[c.getFacing().getIndex()] = c;
        }
    }

    @Override
    public void updateContainingBlockInfo()
    {
        super.updateContainingBlockInfo();
        connections = -1;
    }

    @Override
    public void update()
    {
        for(ModuleContainerImpl c : modules)
        {
            c.update();
        }

        if(!world.isRemote)
        {
            getConnections();
        }

        super.update();
    }

    public void onRightClick(EntityPlayer playerIn, EnumHand hand)
    {
        if(world.isRemote)
        {
            return;
        }

        RayTraceResult ray = MathUtils.rayTrace(playerIn, false);

        if(ray == null)
        {
            return;
        }

        int facing = ray.subHit;

        if(facing >= 6 || facing < 0)
        {
            facing = ray.sideHit.getIndex();
        }

        ItemStack stack = playerIn.getHeldItem(hand);
        ModuleContainerImpl c = modules[facing];

        if(stack.getCount() == 0 && playerIn.isSneaking())
        {
            if(c.getItemStack().getCount() > 0)
            {
                c.getModule().removeFromPipe(c, playerIn);

                if(c.getData().shouldSave())
                {
                    c.getItemStack().setTagInfo("ModuleData", c.getData().serializeNBT());
                }

                if(!playerIn.inventory.addItemStackToInventory(c.getItemStack()) && c.getItemStack().getCount() > 0)
                {
                    world.spawnEntity(new EntityItem(world, playerIn.posX, playerIn.posY, playerIn.posZ, c.getItemStack()));
                }

                c.setStack(ItemStack.EMPTY);
                markDirty();
            }
        }
        else if(c.getItemStack().getCount() == 0)
        {
            int modulesSize = 0;

            for(int i = 0; i < modules.length; i++)
            {
                if(modules[i].hasModule())
                {
                    modulesSize++;
                }
            }

            if(tier <= modulesSize)
            {
                playerIn.sendMessage(new TextComponentString("Can't insert any more modules!"));//TODO: Lang
                return;
            }

            c.setStack(ItemHandlerHelper.copyStackWithSize(stack, 1));

            if(c.hasModule() && c.getModule().insertInPipe(c, playerIn))
            {
                stack.shrink(1);
                markDirty();
            }
            else
            {
                c.setStack(ItemStack.EMPTY);
            }
        }
        else if(!c.getModule().onRightClick(c, playerIn, hand))
        {
            //TODO: Open GUI
            playerIn.sendMessage(new TextComponentString("GUI Not Implemented!"));
        }
            

        /*
        List<TilePipe> list = PipeNetwork.findPipes(this, false);
        List<String> list1 = new ArrayList<>();

        for(TilePipe t : list)
        {
            list1.add("[" + t.getPos().getX() + ", " + t.getPos().getY() + ", " + t.getPos().getZ() + "]");
        }

        playerIn.sendMessage(new TextComponentString("Found " + list.size() + " pipes on network: " + list1));
        */
    }

    public void onBroken()
    {
        for(ModuleContainer c : modules)
        {
            c.getModule().pipeBroken(c);

            if(c.getData().shouldSave())
            {
                c.getItemStack().setTagInfo("ModuleData", c.getData().serializeNBT());
            }

            if(c.getItemStack().getCount() > 0)
            {
                EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, c.getItemStack());
                entityItem.setPickupDelay(10);
                world.spawnEntity(entityItem);
            }
        }

        clearModules();
    }

    public EnumFacing getItemDirection(TransportedItem item, EnumFacing source)
    {
        return source;
    }

    public int getConnections()
    {
        if(connections == -1)
        {
            connections = 0;

            for(EnumFacing facing : EnumFacing.VALUES)
            {
                if(canConnectTo0(facing))
                {
                    connections |= MathUtils.FACING_BIT[facing.getIndex()];
                }
            }

            markDirty();
        }

        return connections;
    }

    public boolean canConnectTo(EnumFacing facing)
    {
        return (getConnections() & MathUtils.FACING_BIT[facing.getIndex()]) != 0;
    }

    private boolean canConnectTo0(EnumFacing facing)
    {
        BlockPos pos1 = pos.offset(facing);
        IBlockState state1 = world.getBlockState(pos1);
        Block block1 = state1.getBlock();

        if(block1 instanceof IPipeBlock)
        {
            return ((IPipeBlock) block1).canPipeConnect(world, pos1, state1, facing.getOpposite());
        }
        else if(block1.hasTileEntity(state1))
        {
            TileEntity tileEntity = world.getTileEntity(pos1);

            if(tileEntity != null)
            {
                if(tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()))
                {
                    return true;
                }
                else if(tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()))
                {
                    return true;
                }
            }
        }

        return false;
    }
}