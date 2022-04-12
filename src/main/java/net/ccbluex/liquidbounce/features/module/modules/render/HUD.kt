/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import com.mojang.realmsclient.gui.ChatFormatting
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtil
import net.ccbluex.liquidbounce.utils.render.AnimationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import java.awt.Color

@ModuleInfo(
    name = "HUD",
    description = "Toggles visibility of the HUD.",
    category = ModuleCategory.RENDER,
    array = false
)
class HUD : Module() {
    val blackHotbarValue = BoolValue("BlackHotbar", true)
    val inventoryParticle = BoolValue("InventoryParticle", false)
    private val blurValue = BoolValue("Blur", false)
    val fontChatValue = BoolValue("FontChat", false)

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (mc.currentScreen is GuiHudDesigner)
            return

        var modY = 0f;
        for (module in LiquidBounce.moduleManager.modules.sortedBy { -Fonts.font35.getStringWidth(it.name + (if (it.tag == null) "" else (" " + it.tag))) }) {
            var sc = ScaledResolution(mc);
            var title = module.name + (if (module.tag == null) "" else (" " + ChatFormatting.GRAY + module.tag));
            if (module.animation > 35) {
                Fonts.font35.drawString(
                    title,
                    sc.scaledWidth_double.toFloat() - Fonts.font35.getStringWidth(title) - 6,
                    module.animationY + 5,
                    Color(255, 255, 255, module.animation.toInt()).getRGB(),
                )
                module.animationY = AnimationUtil.smoothAnimation(module.animationY, modY, 20f, 1f)
                modY += 16
            }
            if (module.state) {
                module.animation = AnimationUtil.smoothAnimation(module.animation, 255f, 20f, 1f)
            } else {
                module.animation = AnimationUtil.smoothAnimation(module.animation, 30f, 20f, 1f)
            }
        }

        LiquidBounce.hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        LiquidBounce.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidBounce.hud.handleKey('a', event.key)
    }

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.guiScreen != null &&
            !(event.guiScreen is GuiChat || event.guiScreen is GuiHudDesigner)
        ) mc.entityRenderer.loadShader(
            ResourceLocation(LiquidBounce.CLIENT_NAME.toLowerCase() + "/blur.json")
        ) else if (mc.entityRenderer.shaderGroup != null &&
            mc.entityRenderer.shaderGroup!!.shaderGroupName.contains("liquidultimate/blur.json")
        ) mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }
}