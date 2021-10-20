package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.item.module.PipeModule;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PipeSideData implements ICapabilityProvider, IItemHandler {
	public final PipeBlockEntity entity;
	public final Direction direction;
	public boolean connect;
	public PipeModule module;
	public boolean light;
	public boolean disabled;
	public int roundRobin;
	LazyOptional<PipeSideData> thisOptional;

	public PipeSideData(PipeBlockEntity e, Direction d) {
		entity = e;
		direction = d;
		connect = false;
		module = null;
		light = false;
		disabled = false;
		roundRobin = 0;
	}

	public boolean shouldWrite() {
		return connect || module != null || light || disabled;
	}

	public CompoundTag write(CompoundTag tag) {
		if (connect) {
			tag.putBoolean("Connect", true);
		}

		if (module != null) {
			CompoundTag nbt1 = module.moduleItem.serializeNBT();
			CompoundTag nbt2 = new CompoundTag();
			module.writeData(nbt2);

			if (!nbt2.isEmpty()) {
				nbt1.put("ModuleData", nbt2);
			}

			tag.put("Module", nbt1);
		}

		if (light) {
			tag.putBoolean("Light", true);
		}

		if (disabled) {
			tag.putBoolean("Disabled", true);
		}

		if (roundRobin > 0) {
			tag.putInt("RoundRobin", roundRobin);
		}

		return tag;
	}

	public void read(CompoundTag tag) {
		connect = tag.getBoolean("Connect");

		if (tag.contains("Module")) {
			CompoundTag nbt1 = tag.getCompound("Module");
			ItemStack stack = ItemStack.of(nbt1);
			module = stack.getCapability(PipeModule.CAP, null).orElse(null);

			if (module != null) {
				module.sideData = this;
				module.moduleItem = stack;
				module.readData(nbt1.getCompound("ModuleData"));
			}
		} else {
			module = null;
		}

		light = tag.getBoolean("Light");
		disabled = tag.getBoolean("Disabled");
		roundRobin = tag.getInt("RoundRobin");
	}

	private boolean updateConnection0() {
		if (!canConnect()) {
			return false;
		}

		BlockEntity blockEntity = entity.getLevel().getBlockEntity(entity.getBlockPos().relative(direction));

		if (blockEntity instanceof PipeNetworkManagerBlockEntity) {
			return true;
		} else if (blockEntity instanceof PipeBlockEntity) {
			return ((PipeBlockEntity) blockEntity).sideData[direction.getOpposite().get3DDataValue()].canConnect();
		} else if (blockEntity != null) {
			return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent() || blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
		}

		return false;
	}

	public void updateConnection() {
		int b = getModelIndex();
		connect = updateConnection0();

		if (b != getModelIndex()) {
			entity.sync();
		}
	}

	public boolean extendShape() {
		return connect || light || module != null;
	}

	public int getModelIndex() {
		int index = 0;

		if (connect || module != null) {
			index |= 1;
		}

		if (module != null) {
			index |= 2;
		}

		if (light) {
			index |= 4;
		}

		// 8

		// 16

		return index;
	}

	public boolean canConnect() {
		return !disabled;
	}

	public void setDisabled(boolean d) {
		disabled = d;
		connect = updateConnection0();
		entity.sync();
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
		if (arg != direction) {
			return LazyOptional.empty();
		} else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return getThisOptional().cast();
		}

		return LazyOptional.empty();
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
		return LazyOptional.empty();
	}

	public LazyOptional<?> getThisOptional() {
		if (thisOptional == null) {
			thisOptional = LazyOptional.of(() -> this);
		}

		return thisOptional;
	}

	public void invalidateCaps() {
		if (thisOptional != null) {
			thisOptional.invalidate();
			thisOptional = null;
		}

		if (module != null) {
			module.invalidateCaps();
		}
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int i) {
		return ItemStack.EMPTY;
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.getCount() < 1) {
			return stack;
		}

		ItemStack single = ItemHandlerHelper.copyStackWithSize(stack, 1);

		for (int i = 0; i < 6; i++) {
			if (i != direction.get3DDataValue()) {
				// eat the item for now
				System.out.println("Ate " + single);
				return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
			}
		}

		return stack;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int i) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isItemValid(int i, @NotNull ItemStack stack) {
		return true;
	}
}
