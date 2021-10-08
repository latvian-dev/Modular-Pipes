package dev.latvian.mods.modularpipes.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * @author LatvianModder
 */
public final class ItemKey {
	public static final ItemKey EMPTY = new ItemKey(ItemStack.EMPTY);
	public final Item item;
	public final CompoundTag nbt;

	private ItemKey(ItemStack stack) {
		item = stack.getItem();
		nbt = item.getShareTag(stack);
	}

	public static ItemKey of(ItemStack stack) {
		return stack.isEmpty() ? EMPTY : new ItemKey(stack);
	}

	public int hashCode() {
		return item.hashCode() * 31 + Objects.hashCode(nbt);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof ItemKey) {
			ItemKey key = (ItemKey) o;
			return item == key.item && Objects.equals(nbt, key.nbt);
		}

		return false;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder(item.getRegistryName().toString());

		if (nbt != null) {
			builder.append('+');
			builder.append(nbt);
		}

		return builder.toString();
	}
}