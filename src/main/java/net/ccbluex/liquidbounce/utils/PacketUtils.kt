package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.server.*

object PacketUtils : MinecraftInstance() {
    @JvmField
    val packets = ArrayList<Packet<INetHandlerPlayServer>>()


    fun sendPacketNoEvent(packet: Packet<INetHandlerPlayServer>) {
        packets.add(packet)
        mc.netHandler.addToSendQueue(packet)
    }

}