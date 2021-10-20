package dev.latvian.mods.modularpipes.item;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.block.ModularPipesBlocks;
import dev.latvian.mods.modularpipes.item.module.CraftingModule;
import dev.latvian.mods.modularpipes.item.module.EnergyInputModule;
import dev.latvian.mods.modularpipes.item.module.EnergyOutputModule;
import dev.latvian.mods.modularpipes.item.module.FluidExtractModule;
import dev.latvian.mods.modularpipes.item.module.ItemExtractModule;
import dev.latvian.mods.modularpipes.item.module.RoundRobinModule;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public interface ModularPipesItems {
	DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ModularPipes.MOD_ID);

	static Supplier<Item> basicItem(String id) {
		return REGISTRY.register(id, () -> new Item(new Item.Properties().tab(ModularPipes.TAB)));
	}

	static Supplier<BlockItem> blockItem(String id, Supplier<Block> sup) {
		return REGISTRY.register(id, () -> new BlockItem(sup.get(), new Item.Properties().tab(ModularPipes.TAB)));
	}

	Supplier<BlockItem> TRANSPORT_PIPE = blockItem("transport_pipe", ModularPipesBlocks.TRANSPORT_PIPE);
	Supplier<BlockItem> MODULAR_PIPE_MK1 = blockItem("modular_pipe_mk1", ModularPipesBlocks.MODULAR_PIPE_MK1);
	Supplier<BlockItem> MODULAR_PIPE_MK2 = blockItem("modular_pipe_mk2", ModularPipesBlocks.MODULAR_PIPE_MK2);
	Supplier<BlockItem> MODULAR_PIPE_MK3 = blockItem("modular_pipe_mk3", ModularPipesBlocks.MODULAR_PIPE_MK3);
	Supplier<BlockItem> PIPE_NETWORK_MANAGER = blockItem("pipe_network_manager", ModularPipesBlocks.PIPE_NETWORK_MANAGER);

	Supplier<Item> WRENCH = REGISTRY.register("wrench", WrenchItem::new);
	Supplier<Item> VISUALIZER = REGISTRY.register("visualizer", VisualizerItem::new);
	Supplier<Item> MODULE = basicItem("module");
	Supplier<Item> LIGHT = basicItem("light");

	Supplier<Item> ITEM_EXTRACT_MODULE = REGISTRY.register("item_extract_module", () -> new ModuleItem(ItemExtractModule::new));
	Supplier<Item> FLUID_EXTRACT_MODULE = REGISTRY.register("fluid_extract_module", () -> new ModuleItem(FluidExtractModule::new));
	Supplier<Item> CRAFTING_MODULE = REGISTRY.register("crafting_module", () -> new ModuleItem(CraftingModule::new));
	Supplier<Item> ENERGY_INPUT_MODULE = REGISTRY.register("energy_input_module", () -> new ModuleItem(EnergyInputModule::new));
	Supplier<Item> ENERGY_OUTPUT_MODULE = REGISTRY.register("energy_output_module", () -> new ModuleItem(EnergyOutputModule::new));
	Supplier<Item> ROUND_ROBIN_MODULE = REGISTRY.register("round_robin_module", () -> new ModuleItem(RoundRobinModule::new));
}