package net.tardis.mod.common.entities.controls;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.tardis.mod.client.worldshell.BlockStorage;
import net.tardis.mod.client.worldshell.IContainsWorldShell;
import net.tardis.mod.client.worldshell.MessageSyncWorldShell;
import net.tardis.mod.client.worldshell.MessageSyncWorldShell.EnumType;
import net.tardis.mod.client.worldshell.PlayerStorage;
import net.tardis.mod.client.worldshell.WorldShell;
import net.tardis.mod.common.IDoor;
import net.tardis.mod.common.blocks.BlockTardisTop;
import net.tardis.mod.common.items.TItems;
import net.tardis.mod.common.sounds.TSounds;
import net.tardis.mod.common.tileentity.TileEntityDoor;
import net.tardis.mod.common.tileentity.TileEntityTardis;
import net.tardis.mod.network.NetworkHandler;
import net.tardis.mod.network.packets.MessageRequestBOTI;
import net.tardis.mod.util.common.helpers.Helper;
import net.tardis.mod.util.common.helpers.TardisHelper;

public class ControlDoor extends Entity implements IContainsWorldShell, IDoor {

	public static final DataParameter<Boolean> IS_OPEN = EntityDataManager.createKey(ControlDoor.class, DataSerializers.BOOLEAN);
	public static final DataParameter<EnumFacing> FACING = EntityDataManager.createKey(ControlDoor.class, DataSerializers.FACING);
	public int antiSpamTicks = 0;
	private WorldShell shell = new WorldShell(BlockPos.ORIGIN);

	public ControlDoor(World world) {
		super(world);
		this.setSize(1F, 2F);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(IS_OPEN, false);
		this.dataManager.register(FACING, EnumFacing.NORTH);
	}

	public TileEntityTardis getConsole() {
		return (TileEntityTardis) world.getTileEntity(TardisHelper.getTardisForPosition(getPosition()));
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (!world.isRemote) {
			this.setDead();
			InventoryHelper.spawnItemStack(world, posX, posY, posZ, new ItemStack(TItems.interior_door));
		}
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	public EnumFacing getFacing() {
		return this.dataManager.get(FACING);
	}

	public void setFacing(EnumFacing facing) {
		this.dataManager.set(FACING, facing);
	}

	public boolean isOpen() {
		return this.dataManager.get(IS_OPEN);
	}

	public void setOpen(boolean b) {
		this.dataManager.set(IS_OPEN, b);
	}

	public long getTime() {
		return 1l;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		TileEntityTardis tardis = (TileEntityTardis) world.getTileEntity(TardisHelper.getTardisForPosition(this.getPosition()));
		if (tardis == null || tardis.isLocked()) return false;
		if (!player.isSneaking()) {
			boolean state = !this.isOpen();
			this.setOpen(state);
			this.setOtherDoors(state);
			if (!world.isRemote) {
				if (this.isOpen())
					world.playSound(null, this.getPosition(), TSounds.door_open, SoundCategory.BLOCKS, 0.5F, 0.5F);
				else
					world.playSound(null, this.getPosition(), TSounds.door_closed, SoundCategory.BLOCKS, 0.5F, 0.5F);
			}
		}
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (antiSpamTicks > 0) --antiSpamTicks;
		TileEntity te = world.getTileEntity(TardisHelper.getTardisForPosition(this.getPosition()));
		if (te == null || !(te instanceof TileEntityTardis)) return;
		TileEntityTardis tardis = (TileEntityTardis) world.getTileEntity(TardisHelper.getTardisForPosition(this.getPosition()));
		if (!world.isRemote && this.isOpen()) {
			if(world.getMinecraftServer() == null) return;
			WorldServer ws = world.getMinecraftServer().getWorld(tardis.dimension);
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox());
			for (Entity e : entities) {
				if (e == this || e instanceof IControl || e instanceof IDoor) continue;
				if (!e.isSneaking()) tardis.transferPlayer(e, true);
			}
			if (this.ticksExisted % 10 == 0) {
				this.shell = new WorldShell(tardis.getLocation().up().offset(this.getFacing(), 11));
				Vec3i r = new Vec3i(10, 10, 10);
				IBlockState doorState = ws.getBlockState(tardis.getLocation().up());
				EnumFacing facing = EnumFacing.NORTH;
				if (doorState != null && doorState.getBlock() instanceof BlockTardisTop) {
					facing = doorState.getValue(BlockTardisTop.FACING);
				}
				for (BlockPos pos : BlockPos.getAllInBox(shell.getOffset().subtract(r), shell.getOffset().add(r))) {
					IBlockState state = ws.getBlockState(pos);
					if (state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof BlockTardisTop)) {
						int light = 15;
						this.shell.blockMap.put(pos, new BlockStorage(state, ws.getTileEntity(pos), light));
					}
				}
				this.setFacing(facing);
				List<NBTTagCompound> list = new ArrayList<>();
				List<PlayerStorage> players = new ArrayList<PlayerStorage>();
				for (Entity e : ws.getEntitiesWithinAABB(Entity.class, Helper.createBB(tardis.getLocation().offset(facing, 10), 10))) {
					if (EntityList.getKey(e) != null) {
						NBTTagCompound tag = new NBTTagCompound();
						e.writeToNBT(tag);
						tag.setString("id", EntityList.getKey(e).toString());
						tag.setFloat("rot", e.rotationYaw);
						list.add(tag);
					} else if (e instanceof EntityPlayer) {
						players.add(new PlayerStorage((EntityPlayer) e));
					}
				}
				shell.setTime(world.getWorldTime());
				shell.setPlayers(players);
				shell.setEntities(list);
			}
			if (world.isRemote) this.shell.setTime(shell.getTime() + 1);
		}
		if (tardis.isInFlight() && this.isOpen()) {
			AxisAlignedBB voidBB = new AxisAlignedBB(-10, -10, -10, 10, 10, 10).offset(this.getPosition());

			for (Entity entity : world.getEntitiesWithinAABB(Entity.class, voidBB)) {
				if (!entity.isDead) {
					Vec3d dir = entity.getPositionVector().subtract(this.getPositionVector()).normalize().scale(-1).scale(0.12);
					entity.motionX += dir.x;
					entity.motionY += dir.y;
					entity.motionZ += dir.z;
					if (entity instanceof EntityPlayer && entity.getPositionVector().distanceTo(this.getPositionVector()) <= 1) {
						if (!world.isRemote) tardis.transferPlayer(entity, false);
					}
				}
			}
		}
		if(world.isRemote && this.shell.getOffset().equals(BlockPos.ORIGIN)) {
			NetworkHandler.NETWORK.sendToServer(new MessageRequestBOTI(this.getEntityId()));
		}
		if(world.isRemote && world.getTotalWorldTime() % 200 == 0)
			NetworkHandler.NETWORK.sendToServer(new MessageRequestBOTI(this.getEntityId()));
	}
	
	public void sendBOTI(EnumType type) {
		NetworkHandler.NETWORK.sendToAllAround(new MessageSyncWorldShell(this.getWorldShell(), this.getEntityId(), type), new TargetPoint(this.dimension, posX, posY, posZ, 40D));
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public WorldShell getWorldShell() {
		return this.shell;
	}

	@Override
	public void setWorldShell(WorldShell worldShell) {
		this.shell = worldShell;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.dataManager.set(IS_OPEN, compound.getBoolean("open"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setBoolean("open", this.dataManager.get(IS_OPEN));
	}

	@Override
	public int getDimension() {
		return this.getConsole() != null ? this.getConsole().dimension : 0;
	}
	
	public void setOtherDoors(boolean open) {
		if(!world.isRemote) {
			TileEntityTardis tardis = this.getConsole();
			if(tardis != null) {
				TileEntityDoor door = (TileEntityDoor)((WorldServer)world).getMinecraftServer().getWorld(tardis.dimension).getTileEntity(tardis.getLocation().up());
				if(door != null) {
					door.setLocked(!open);
				}
			}
		}
	}

	@Override
	public boolean requiresUpdate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRequiresUpdate(boolean bool) {
		// TODO Auto-generated method stub
		
	}

}
