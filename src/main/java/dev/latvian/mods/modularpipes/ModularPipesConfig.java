package dev.latvian.mods.modularpipes;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;

/**
 * @author LatvianModder
 */
//@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID)
//@Config(modid = ModularPipes.MOD_ID, category = "")
public class ModularPipesConfig {
	//	@Config.LangKey("stat.generalButton")
	public static final General general = new General();

	public static final Pipes pipes = new Pipes();

	public static class General {
		//		@Config.Comment("Allows players to use dev features, like network visualisation")
		public boolean dev_mode = false;
	}

	public static class Pipes {
		//		@Config.RangeDouble(min = 0, max = 1)
		//		@Config.Comment("Base speed")
		public double base_speed = 0.05;

		//		@Config.RangeInt(min = 1)
		public int max_energy_stored = 24000;
	}

	//	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(ModularPipes.MOD_ID)) {
			//			sync();
		}
	}
}