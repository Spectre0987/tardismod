package net.tardis.mod.client.renderers.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.tardis.mod.Tardis;
import net.tardis.mod.client.models.entity.ModelCybermanInvasion;
import net.tardis.mod.common.entities.EntityCybermanInvasion;

public class RenderCybermanInvasion extends RenderLiving<EntityCybermanInvasion> {

	Minecraft mc;
	public static ModelCybermanInvasion model = new ModelCybermanInvasion();
	public static final ResourceLocation TEXTURE = new ResourceLocation(Tardis.MODID, "textures/entity/mob/cyberman_invasion.png");
	
	public RenderCybermanInvasion(RenderManager manager) {
		super(manager, model, 0.03F);
		mc = Minecraft.getMinecraft();

	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCybermanInvasion entity) {
		return TEXTURE;
	}

	@Override
	protected void renderModel(EntityCybermanInvasion entitylivingbaseIn, float limbSwing, float limbSwingAmount,float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
	}

}
