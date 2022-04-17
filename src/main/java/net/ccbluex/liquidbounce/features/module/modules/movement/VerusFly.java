package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

@ModuleInfo(name = "VerusFly", description = "Allows you to fly in Verus.", category = ModuleCategory.MOVEMENT)
public class VerusFly extends Module {

    private int ticks;
    private int offGroundTicks;


    @EventTarget
    private void onPre(MotionEvent event) {
        if (event.getEventState() == EventState.PRE) {
            ++ticks;

            if (Minecraft.getMinecraft().thePlayer.onGround) {
                offGroundTicks = 0;
            } else {
                ++offGroundTicks;
            }

            if (ticks == 1) {
                if (!Minecraft.getMinecraft().thePlayer.onGround) {
                    setState(false);
                    return;
                }
            }

            if (offGroundTicks >= 2 && !Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {
                if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
                    if (Minecraft.getMinecraft().thePlayer.ticksExisted % 2 == 0)
                        Minecraft.getMinecraft().thePlayer.motionY = 0.42F;
                } else {
                    if (Minecraft.getMinecraft().thePlayer.onGround) {
                        Minecraft.getMinecraft().thePlayer.jump();
                    }

                    if (Minecraft.getMinecraft().thePlayer.fallDistance > 1) {
                        Minecraft.getMinecraft().thePlayer.motionY = -((Minecraft.getMinecraft().thePlayer.posY) - Math.floor(Minecraft.getMinecraft().thePlayer.posY));
                    }

                    if (Minecraft.getMinecraft().thePlayer.motionY == 0) {
                        Minecraft.getMinecraft().thePlayer.jump();

                        Minecraft.getMinecraft().thePlayer.onGround = true;
                        Minecraft.getMinecraft().thePlayer.fallDistance = 0;
                    }
                }
            }

            strafe(1);

            if (ticks == 1) {
                mc.timer.timerSpeed = 0.15F;
                damagePlayer(3.42F, 1, true, false);
                Minecraft.getMinecraft().thePlayer.jump();
                mc.thePlayer.onGround = true;
            } else
                mc.timer.timerSpeed = 1;
        }
    }

    public static void damagePlayer(final double value, final int packets, final boolean groundCheck, final boolean hurtTimeCheck) {
        if ((!groundCheck || Minecraft.getMinecraft().thePlayer.onGround) && (!hurtTimeCheck || Minecraft.getMinecraft().thePlayer.hurtTime == 0)) {
            final double x = Minecraft.getMinecraft().thePlayer.posX;
            final double y = Minecraft.getMinecraft().thePlayer.posY;
            final double z = Minecraft.getMinecraft().thePlayer.posZ;

            for (int i = 0; i < packets; i++) {
                Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + value, z, false), null);
                Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false), null);
            }
            Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true), null);
        }
    }

    public void strafe(final float speed) {
        if (!isMoving()) return;

        final double yaw = getDirection();

        Minecraft.getMinecraft().thePlayer.motionX = -MathHelper.sin((float) yaw) * speed;
        Minecraft.getMinecraft().thePlayer.motionZ = MathHelper.cos((float) yaw) * speed;
    }

    public double getDirection() {
        float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;

        if (Minecraft.getMinecraft().thePlayer.moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (Minecraft.getMinecraft().thePlayer.moveForward < 0F) forward = -0.5F;
        else if (Minecraft.getMinecraft().thePlayer.moveForward > 0F) forward = 0.5F;

        if (Minecraft.getMinecraft().thePlayer.moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (Minecraft.getMinecraft().thePlayer.moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public boolean isMoving() {
        return Minecraft.getMinecraft().thePlayer != null && (Minecraft.getMinecraft().thePlayer.movementInput.moveForward != 0F || Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe != 0F);
    }


}
