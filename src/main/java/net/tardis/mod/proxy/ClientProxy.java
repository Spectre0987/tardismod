package net.tardis.mod.proxy;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.tardis.mod.common.entities.controls.ControlDimChange;
import net.tardis.mod.common.entities.controls.ControlDoor;
import net.tardis.mod.common.entities.controls.ControlFlight;
import net.tardis.mod.common.entities.controls.ControlFuel;
import net.tardis.mod.common.entities.controls.ControlLaunch;
import net.tardis.mod.common.entities.controls.ControlRandom;
import net.tardis.mod.common.entities.controls.ControlSTCButton;
import net.tardis.mod.common.entities.controls.ControlSTCLoad;
import net.tardis.mod.common.entities.controls.ControlScreen;
import net.tardis.mod.common.entities.controls.ControlX;
import net.tardis.mod.common.entities.controls.ControlY;
import net.tardis.mod.common.entities.controls.ControlZ;
import net.tardis.mod.common.entities.EntityAngel;
import net.tardis.mod.common.entities.EntityDalekRay;
import net.tardis.mod.common.entities.EntityForceField;
import net.tardis.mod.common.entities.EntityTardis;
import net.tardis.mod.client.renderers.RenderAngel;
import net.tardis.mod.client.renderers.RenderConsole;
import net.tardis.mod.client.renderers.RenderDoor;
import net.tardis.mod.client.renderers.RenderFField;
import net.tardis.mod.client.renderers.RenderInvis;
import net.tardis.mod.client.renderers.RenderRay;
import net.tardis.mod.client.renderers.RenderScreen;
import net.tardis.mod.client.renderers.RenderTardis;
import net.tardis.mod.client.renderers.RenderUmbrellaStand;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.common.tileentity.TileEntityUmbrellaStand;

public class ClientProxy extends ServerProxy {
	
	@Override
	public void renderEntities() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTardis.class, new RenderConsole());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUmbrellaStand.class, new RenderUmbrellaStand());
		
		// Controls
		RenderingRegistry.registerEntityRenderingHandler(ControlScreen.class, new RenderScreen());
		RenderingRegistry.registerEntityRenderingHandler(ControlDoor.class, new RenderDoor());
		RenderingRegistry.registerEntityRenderingHandler(ControlX.class, new RenderInvis());
		RenderingRegistry.registerEntityRenderingHandler(ControlY.class, new RenderInvis());
		RenderingRegistry.registerEntityRenderingHandler(ControlZ.class, new RenderInvis());
		RenderingRegistry.registerEntityRenderingHandler(ControlLaunch.class, new RenderInvis());
		RenderingRegistry.registerEntityRenderingHandler(ControlDimChange.class, new RenderInvis());
		RenderingRegistry.registerEntityRenderingHandler(ControlFlight.class, new RenderInvis());
		RenderingRegistry.registerEntityRenderingHandler(ControlRandom.class, new RenderInvis());
		RenderingRegistry.registerEntityRenderingHandler(ControlSTCLoad.class, new RenderInvis());
		RenderingRegistry.registerEntityRenderingHandler(ControlSTCButton.class, new RenderInvis());
		RenderingRegistry.registerEntityRenderingHandler(ControlFuel.class, new RenderInvis());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityTardis.class, new RenderTardis());
		RenderingRegistry.registerEntityRenderingHandler(EntityAngel.class, new RenderAngel());
		RenderingRegistry.registerEntityRenderingHandler(EntityDalekRay.class, new RenderRay());
		RenderingRegistry.registerEntityRenderingHandler(EntityForceField.class, new RenderFField());
		
		// Not needed currently.
		// OBJLoader.INSTANCE.addDomain(Tardis.MODID);
	}
	
}
