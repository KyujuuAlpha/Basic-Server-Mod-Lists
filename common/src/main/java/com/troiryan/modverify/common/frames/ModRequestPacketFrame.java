package com.troiryan.modverify.common.frames;

import com.troiryan.modverify.common.Constants;

import io.netty.buffer.ByteBuf;

public class ModRequestPacketFrame extends PacketFrame {

    public ModRequestPacketFrame() {
        super();
    }

    public ModRequestPacketFrame(ByteBuf buf) {
        super(buf);
    }

    public ModRequestPacketFrame(byte[] byteArray) {
        super(byteArray);
    }

    @Override
    protected byte getDiscriminator() {
        return Constants.PACKET_REQUEST_MODS_DISC;
    }
}
