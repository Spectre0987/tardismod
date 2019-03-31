package net.tardis.mod.common.systems;

import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tardis.mod.common.blocks.TBlocks;
import net.tardis.mod.common.items.TItems;
import net.tardis.mod.common.systems.TardisSystems.BaseSystem;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.util.common.helpers.Helper;

public class SystemCCircuit extends BaseSystem {

	private boolean checkActive = false;

	@Override
	public void onUpdate(World world, BlockPos consolePos) {
		if (checkActive) {
			TileEntityTardis tardis = Helper.getTardis(world.getTileEntity(consolePos));
			if (tardis != null && tardis.getTopBlock().getBlock() == TBlocks.tardis_top_cc) {
				this.setHealth(this.getHealth() - 0.01F);
			}
			checkActive = false;
		}
	}

	@Override
	public void damage() {
	}

	@Override
	public Item getRepairItem() {
		return TItems.chameleon_circuit;
	}

	@Override
	public String getNameKey() {
		return "system.tardis.ccircuit";
	}

	@Override
	public String getUsage() {
		return "Without this system, you will not be able to change your TARDIS exterior";
	}

	@Override
	public void wear() {
		this.setHealth(this.getHealth() - 0.005F);
	}

	@Override
	public boolean shouldStopFlight() {
		return false;
	}

}
