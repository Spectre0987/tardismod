package net.tardis.mod.common.entities;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tardis.mod.common.items.TItems;
import net.tardis.mod.common.sounds.TSounds;

public class EntityDalek extends EntityMob implements IRangedAttackMob, EntityFlying {


	private ItemStack[] deathItems = new ItemStack[]{new ItemStack(TItems.power_cell, 20 + rand.nextInt(11)), new ItemStack(TItems.gunstick, 1), new ItemStack(TItems.circuts, 7 + rand.nextInt(3))};


	public EntityDalek(World world) {
		super(world);
	}
	
	@Override
	 protected void initEntityAI(){
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackRanged(this, 0.75D, 40, 30F));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI(){
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, false, false));
    }

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
	}
	
	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		Vec3d look = target.getPositionVector().subtract(this.getPositionVector());
		
		EntityDalekRay ball = new EntityDalekRay(world, this);
		ball.setPosition(posX + this.getLookVec().x, posY + this.getEyeHeight(), posZ + this.getLookVec().z);
		world.spawnEntity(ball);
		world.playSound(null, this.getPosition(), TSounds.dalek_ray, SoundCategory.HOSTILE, 1F, 1F);
		if(this.rand.nextInt(3) == 0)world.playSound(null, this.getPosition(), TSounds.dalek, SoundCategory.HOSTILE, 1F, 1F);
	}
	
	@Override
	public void setSwingingArms(boolean swingingArms) {}


	@Override
	public void onUpdate() {
		super.onUpdate();
		if(this.getAttackTarget() != null) {
			if(this.getAttackTarget().posY > this.posY + 2) {
				this.setNoGravity(true);
				this.motionY = 0.02;
			}
			else if(this.hasNoGravity())this.motionY = -0.02;
			this.fallDistance = 0;
			if(this.hasNoGravity()) {
				Vec3d dir = this.getAttackTarget().getPositionVector().subtract(this.getPositionVector()).normalize().scale(0.3D);
				this.motionX = dir.x;
				this.motionZ = dir.z;
			}
		}
		if(this.onGround || !this.isAirBorne) this.setNoGravity(false);
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		int index = rand.nextInt(deathItems.length);
		if (!world.isRemote) {
			InventoryHelper.spawnItemStack(world, posX, posY, posZ, deathItems[index]);
		}
	}
	
}
