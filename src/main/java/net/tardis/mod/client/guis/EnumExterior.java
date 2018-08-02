package net.tardis.mod.client.guis;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tardis.mod.client.models.exteriors.ModelTardis01;
import net.tardis.mod.client.models.exteriors.ModelTardis02;
import net.tardis.mod.client.models.exteriors.ModelTardis03;
import net.tardis.mod.client.renderers.exteriors.RenderTileDoor03;
import net.tardis.mod.client.renderers.exteriors.RendererTileDoor01;
import net.tardis.mod.client.renderers.tiles.RenderTileDoor;
import net.tardis.mod.common.blocks.TBlocks;

@SideOnly(Side.CLIENT)
public enum EnumExterior {

	FIRST(new ModelTardis01(), RenderTileDoor.TEXTURE, TBlocks.tardis_top),
	SECOND(new ModelTardis02(), RendererTileDoor01.TEXTURE, TBlocks.tardis_top_01),
	THIRD(new ModelTardis03(), RenderTileDoor03.TEXTURE, TBlocks.tardis_top_02);
	
	ModelBase model;
	ResourceLocation tex;
	Block block;
	
	EnumExterior(ModelBase base, ResourceLocation tex, Block block){
		this.model = base;
		this.tex = tex;
		this.block = block;
	}
}
