package flaxbeard.cyberware.client.render;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberlimb;
import flaxbeard.cyberware.common.item.ItemHandUpgrade;

public class RenderPlayerCyberware extends RenderPlayer
{
	
	public boolean doMuscles = false;
	public boolean doRobo = false;
	public boolean doRusty = false;
	
	public RenderPlayerCyberware(RenderManager renderManager, boolean arms)
	{
		super(renderManager, arms);
	}
	
	private static final ResourceLocation muscles = new ResourceLocation(Cyberware.MODID, "textures/models/player_muscles.png");
	private static final ResourceLocation robo = new ResourceLocation(Cyberware.MODID, "textures/models/player_robot.png");
	private static final ResourceLocation roboRust = new ResourceLocation(Cyberware.MODID, "textures/models/player_rusty_robot.png");
	
	@Override
	public ResourceLocation getEntityTexture(AbstractClientPlayer entity)
	{
		return doRusty ? roboRust : doRobo ? robo :
			doMuscles ? muscles : super.getEntityTexture(entity);
	}
	
	public void setMainModel(ModelPlayer modelPlayer)
	{
		mainModel = modelPlayer;
	}
	
	private static ModelClaws claws = new ModelClaws(0.0F);
	
	@Override
	public void renderRightArm(AbstractClientPlayer clientPlayer)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(robo);
		super.renderRightArm(clientPlayer);
		
		if ( Minecraft.getMinecraft().gameSettings.mainHand != EnumHandSide.RIGHT
		  || !clientPlayer.getHeldItemMainhand().isEmpty() )
		{
			return;
		}
		
		ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(clientPlayer);
		if (cyberwareUserData == null) return;
		
		ItemStack itemStackClaws = cyberwareUserData.getCyberware(CyberwareContent.handUpgrades.getCachedStack(ItemHandUpgrade.META_CLAWS));
		if ( !itemStackClaws.isEmpty()
		  && cyberwareUserData.isCyberwareInstalled(CyberwareContent.cyberlimbs.getCachedStack(ItemCyberlimb.META_RIGHT_CYBER_ARM))
		  && EnableDisableHelper.isEnabled(itemStackClaws) )
		{
			GlStateManager.pushMatrix();
			
			float percent = ((Minecraft.getMinecraft().player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks() - ItemHandUpgrade.clawsTime) / 4F);
			percent = Math.min(1.0F, percent);
			percent = Math.max(0F, percent);
			percent = (float) Math.sin(percent * Math.PI / 2F);
			claws.claw1.rotateAngleY = 0.00F;
			claws.claw1.rotateAngleZ = 0.07F;
			claws.claw1.rotateAngleX = 0.00F;
			claws.claw1.setRotationPoint(-5.0F, -5.0F + (7F * percent), 0.0F);
			claws.claw1.render(0.0625F);
			GlStateManager.popMatrix();
		}
	}
	
	@Override
	public void renderLeftArm(AbstractClientPlayer clientPlayer)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(robo);
		super.renderLeftArm(clientPlayer);
		
		if ( Minecraft.getMinecraft().gameSettings.mainHand != EnumHandSide.LEFT
		  || !clientPlayer.getHeldItemMainhand().isEmpty() )
		{
			return;
		}
		
		ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(clientPlayer);
		if (cyberwareUserData == null) return;
		
		ItemStack itemStackClaws = cyberwareUserData.getCyberware(CyberwareContent.handUpgrades.getCachedStack(ItemHandUpgrade.META_CLAWS));
		if ( !itemStackClaws.isEmpty()
		  && cyberwareUserData.isCyberwareInstalled(CyberwareContent.cyberlimbs.getCachedStack(ItemCyberlimb.META_LEFT_CYBER_ARM))
		  && EnableDisableHelper.isEnabled(itemStackClaws) )
		{
			GlStateManager.pushMatrix();
			
			float percent = ((Minecraft.getMinecraft().player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks() - ItemHandUpgrade.clawsTime) / 4F);
			percent = Math.min(1.0F, percent);
			percent = Math.max(0F, percent);
			percent = (float) Math.sin(percent * Math.PI / 2F);
			claws.claw1.rotateAngleY = 0.00F;
			claws.claw1.rotateAngleZ = 0.07F;
			claws.claw1.rotateAngleX = 0.00F;
			claws.claw1.setRotationPoint(-5.0F, -5.0F + (7F * percent), 0.0F);
			claws.claw1.render(0.0625F);
			GlStateManager.popMatrix();
		}
	}
	
	public void doRender(@Nonnull AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Pre(entity, this, partialTicks, x, y, z))) return;
		if ( !entity.isUser()
		  || renderManager.renderViewEntity == entity )
		{
			double d0 = y;
			
			if (entity.isSneaking())
			{
				d0 = y - 0.125D;
			}
			
			setModelVisibilities(entity);
			GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
			doRenderELB(entity, x, d0, z, entityYaw, partialTicks);
			GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
		}
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Post(entity, this, partialTicks, x, y, z));
	}
	
	public void doRenderELB(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		// if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<>(entity, this, partialTicks, x, y, z))) return;
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		mainModel.swingProgress = getSwingProgress(entity, partialTicks);
		boolean shouldSit = entity.isRiding() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		mainModel.isRiding = shouldSit;
		mainModel.isChild = entity.isChild();
		
		ItemStack head  = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		ItemStack body  = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		ItemStack legs  = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		ItemStack shoes = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		ItemStack heldItem = entity.getHeldItemMainhand();
		ItemStack offHand = entity.getHeldItemOffhand();
		
		if (doRobo)
		{
			entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemStack.EMPTY);
			entity.setItemStackToSlot(EntityEquipmentSlot.FEET, ItemStack.EMPTY);
			entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
		}
		
		try
		{
			float f = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
			float f1 = interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
			float netHeadYaw = f1 - f;
			
			if ( shouldSit
			  && entity.getRidingEntity() instanceof EntityLivingBase )
			{
				EntityLivingBase entitylivingbase = (EntityLivingBase) entity.getRidingEntity();
				f = interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
				netHeadYaw = f1 - f;
				float f3 = MathHelper.wrapDegrees(netHeadYaw);
				
				if (f3 < -85.0F)
				{
					f3 = -85.0F;
				}
				
				if (f3 >= 85.0F)
				{
					f3 = 85.0F;
				}
				
				f = f1 - f3;
				
				if (f3 * f3 > 2500.0F)
				{
					f += f3 * 0.2F;
				}
				
				netHeadYaw = f1 - f;
			}
			
			float headPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
			renderLivingAt(entity, x, y, z);
			float ageInTicks = handleRotationFloat(entity, partialTicks);
			applyRotations(entity, ageInTicks, f, partialTicks);
			float scaleFactor = prepareScale(entity, partialTicks);
			float limbSwingAmount = 0.0F;
			float limbSwing = 0.0F;
			
			if (!entity.isRiding())
			{
				limbSwingAmount = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
				limbSwing = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
				
				if (entity.isChild())
				{
					limbSwing *= 3.0F;
				}
				
				if (limbSwingAmount > 1.0F)
				{
					limbSwingAmount = 1.0F;
				}
				netHeadYaw = f1 - f;
			}
			
			GlStateManager.enableAlpha();
			mainModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
			mainModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
			
			if (renderOutlines)
			{
				boolean wasTeamColorChanged = setScoreTeamColor(entity);
				GlStateManager.enableColorMaterial();
				GlStateManager.enableOutlineMode(getTeamColor(entity));
				
				if (!renderMarker)
				{
					renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
				}
				
				if (!entity.isSpectator())
				{
					renderLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleFactor);
				}
				
				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();
				
				if (wasTeamColorChanged)
				{
					unsetScoreTeamColor();
				}
			}
			else
			{
				boolean wasBrightnessChanged = setDoRenderBrightness(entity, partialTicks);
				renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
				if (wasBrightnessChanged)
				{
					unsetBrightness();
				}
				
				GlStateManager.depthMask(true);
				
				if (!entity.isSpectator())
				{
					renderLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleFactor);
				}
			}
			
			GlStateManager.disableRescaleNormal();
		}
		catch (Exception exception)
		{
			Cyberware.logger.error("Disabling custom render until next restart");
			CyberwareConfig.RENDER = false;
			exception.printStackTrace();
			Cyberware.logger.error(String.format("Exception while rendering %s with %s",
			                                     entity, this));
		}
		
		entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
		entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, body);
		entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, legs);
		entity.setItemStackToSlot(EntityEquipmentSlot.FEET, shoes);
		entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, heldItem);
		entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, offHand);
		
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		
		// From Render.class
		if (!renderOutlines)
		{
			renderName(entity, x, y, z);
		}
		// net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<>(entity, this, partialTicks, x, y, z));
	}
	
	private void setModelVisibilities(@Nonnull AbstractClientPlayer clientPlayer)
	{
		ModelPlayer modelplayer = getMainModel();
		
		if (clientPlayer.isSpectator())
		{
			modelplayer.setVisible(false);
			modelplayer.bipedHead.showModel = true;
			modelplayer.bipedHeadwear.showModel = true;
		}
		else
		{
			ItemStack itemstack = clientPlayer.getHeldItemMainhand();
			ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
			modelplayer.setVisible(true);
			modelplayer.bipedHeadwear.showModel = !modelplayer.bipedHead.isHidden
			                                   && clientPlayer.isWearing(EnumPlayerModelParts.HAT);
			modelplayer.bipedBodyWear.showModel = !modelplayer.bipedBody.isHidden
			                                   && clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
			modelplayer.bipedLeftLegwear.showModel = !modelplayer.bipedLeftLeg.isHidden
			                                      && clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
			modelplayer.bipedRightLegwear.showModel = !modelplayer.bipedRightLeg.isHidden
			                                       && clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
			modelplayer.bipedLeftArmwear.showModel = !modelplayer.bipedLeftArm.isHidden
			                                      && clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
			modelplayer.bipedRightArmwear.showModel = !modelplayer.bipedRightArm.isHidden
			                                       && clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
			modelplayer.isSneak = clientPlayer.isSneaking();
			ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
			ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;
			
			if (!itemstack.isEmpty())
			{
				modelbiped$armpose = ModelBiped.ArmPose.ITEM;
				
				if (clientPlayer.getItemInUseCount() > 0)
				{
					EnumAction enumaction = itemstack.getItemUseAction();
					
					if (enumaction == EnumAction.BLOCK)
					{
						modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
					}
					else if (enumaction == EnumAction.BOW)
					{
						modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
					}
				}
			}
			
			if (!itemstack1.isEmpty())
			{
				modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;
				
				if (clientPlayer.getItemInUseCount() > 0)
				{
					EnumAction enumaction1 = itemstack1.getItemUseAction();
					
					if (enumaction1 == EnumAction.BLOCK)
					{
						modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
					}
				}
			}
			
			if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT)
			{
				modelplayer.rightArmPose = modelbiped$armpose;
				modelplayer.leftArmPose = modelbiped$armpose1;
			}
			else
			{
				modelplayer.rightArmPose = modelbiped$armpose1;
				modelplayer.leftArmPose = modelbiped$armpose;
			}
		}
	}
	
}
