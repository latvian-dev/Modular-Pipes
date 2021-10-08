package dev.latvian.mods.modularpipes.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.ModularPipesUtils;
import dev.latvian.mods.modularpipes.block.EnumMK;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class ModelPipe implements IUnbakedModel {
	public static final ModelRotation[] FACE_ROTATIONS = {
			ModelRotation.X0_Y0,
			ModelRotation.X180_Y0,
			ModelRotation.X90_Y180,
			ModelRotation.X90_Y0,
			ModelRotation.X90_Y90,
			ModelRotation.X90_Y270
	};

	private static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> TRANSFORM_MAP;

	public interface ModelCallback {
		List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, boolean uvlock, Map.Entry... retextures);

		default List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, Map.Entry... retextures) {
			return get(id, rotation, true, retextures);
		}
	}

	static {
		TRSRTransformation thirdperson = get(2.5F, 75, 45, 0.375F);
		TRSRTransformation flipX = new TRSRTransformation(null, null, new javax.vecmath.Vector3f(-1, 1, 1), null);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(ItemCameraTransforms.TransformType.GUI, get(0, 30, 225, 0.625F));
		builder.put(ItemCameraTransforms.TransformType.GROUND, get(3, 0, 0, 0.25F));
		builder.put(ItemCameraTransforms.TransformType.FIXED, get(0, 0, 0, 0.5F));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(thirdperson)).compose(flipX)));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 45, 0.4F));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 225, 0.4F));
		TRANSFORM_MAP = Maps.immutableEnumMap(builder.build());
	}

	public final Collection<ResourceLocation> models;
	public final ResourceLocation modelBase, modelConnection, modelVertical;
	public final ResourceLocation modelOverlay, modelModule;
	public final ResourceLocation modelGlassBase, modelGlassConnection, modelGlassVertical;
	public final Collection<ResourceLocation> textures;
	public final ResourceLocation textureParticle;
	public final ResourceLocation[] overlayTextures;

	public ModelPipe() {
		Collection<ResourceLocation> models0 = new HashSet<>();
		models0.add(modelBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/base"));
		models0.add(modelConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/connection"));
		models0.add(modelVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/vertical"));
		models0.add(modelOverlay = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/overlay"));
		models0.add(modelModule = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/module"));
		models0.add(modelGlassBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_base"));
		models0.add(modelGlassConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_connection"));
		models0.add(modelGlassVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_vertical"));

		models = Collections.unmodifiableCollection(models0);
		textures = new HashSet<>();

		overlayTextures = new ResourceLocation[EnumMK.VALUES.length];

		for (int i = 0; i < EnumMK.VALUES.length; i++) {
			textures.add(overlayTextures[i] = new ResourceLocation("modularpipes:block/pipe/overlay/" + EnumMK.VALUES[i].getName()));
		}

		textureParticle = new ResourceLocation("minecraft:block/concrete_gray");
		textures.add(new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/module"));
		//		textures.add(textureParticle = new ResourceLocation("minecraft:block/concrete_gray"));
	}

	private static TRSRTransformation get(float ty, float ax, float ay, float s) {
		return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new javax.vecmath.Vector3f(0F, ty / 16F, 0F), TRSRTransformation.quatFromXYZDegrees(new javax.vecmath.Vector3f(ax, ay, 0F)), new javax.vecmath.Vector3f(s, s, s), null));
	}

	public static org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handleModelPerspective(IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType) {
		return PerspectiveMapWrapper.handlePerspective(model, TRANSFORM_MAP, cameraTransformType);
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return models;
	}

	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		return textures;
	}

	public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
		return new ModelPipeBaked(this, spriteGetter.apply(textureParticle), (id, rotation, uvlock, retextures) ->
		{
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

			for (Map.Entry entry : retextures) {
				builder.put(new AbstractMap.SimpleEntry<>(entry.getKey().toString(), new ResourceLocation(entry.getValue().toString()).toString()));
			}

			IModel model = ModelLoaderRegistry.getModelOrMissing(id).retexture(builder.build());//.smoothLighting(false);
			IBakedModel bakedModel = model.bake(bakery, spriteGetter, sprite, format);
			return ModularPipesUtils.optimize(bakedModel.getQuads(null, null, new Random()));
		});
	}
}