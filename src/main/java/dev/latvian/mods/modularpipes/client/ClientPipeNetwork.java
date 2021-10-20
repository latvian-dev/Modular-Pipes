package dev.latvian.mods.modularpipes.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.latvian.mods.modularpipes.util.PipeItem;
import dev.latvian.mods.modularpipes.util.PipeNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ClientPipeNetwork extends PipeNetwork {
	public ClientPipeNetwork(Level w) {
		super(w);
	}

	public void render(RenderWorldLastEvent event) {
		if (pipeItems.isEmpty()) {
			return;
		}

		ProfilerFiller profiler = level.getProfiler();
		profiler.push("Modular Pipes");

		Minecraft mc = Minecraft.getInstance();
		ItemRenderer itemRenderer = mc.getItemRenderer();
		PoseStack matrix = event.getMatrixStack();
		float delta = event.getPartialTicks();
		MultiBufferSource multiBufferSource = mc.renderBuffers().bufferSource();

		double renderDistanceSq = ModularPipesClientConfig.ITEM_RENDER_DISTANCE * ModularPipesClientConfig.ITEM_RENDER_DISTANCE;
		double px = BlockEntityRenderDispatcher.instance.camera.getPosition().x;
		double py = BlockEntityRenderDispatcher.instance.camera.getPosition().y;
		double pz = BlockEntityRenderDispatcher.instance.camera.getPosition().z;
		Frustum frustum = new Frustum(matrix.last().pose(), event.getProjectionMatrix());
		frustum.prepare(px, py, pz);
		RenderSystem.disableLighting();
		Lighting.setupFor3DItems();
		RenderSystem.enableDepthTest();
		double[] position = new double[5];

		for (PipeItem item : pipeItems.values()) {
			if (item.ttl <= 0 || item.path == null) {
				continue;
			}

			item.path.translate(item.pos, position);
			double x = position[0];
			double y = position[1];
			double z = position[2];
			double rx = position[3];
			double ry = position[4];
			double s = item.getScale(mc, itemRenderer);

			if ((px - x) * (px - x) + (py - y) * (py - y) + (pz - z) * (pz - z) > renderDistanceSq || !frustum.isVisible(new AABB(x - s, y - s, z - s, x + s, y + s, z + s))) {
				continue;
			}

			item.path.translate(item.pos - 1, position);

			matrix.pushPose();
			matrix.translate(Mth.lerp(delta, position[0], x) - px, Mth.lerp(delta, position[1], y) - py, Mth.lerp(delta, position[2], z) - pz);
			matrix.mulPose(Vector3f.YP.rotationDegrees((float) Mth.lerp(delta, position[4], ry)));
			matrix.mulPose(Vector3f.XP.rotationDegrees((float) Mth.lerp(delta, position[3], rx)));
			matrix.scale((float) s, (float) s, (float) s);
			item.render(matrix, itemRenderer, multiBufferSource, 15728880, OverlayTexture.NO_OVERLAY);
			matrix.popPose();
		}

		// Lighting.setupForFlatItems();
		RenderSystem.enableLighting();

		profiler.pop();
	}
}
