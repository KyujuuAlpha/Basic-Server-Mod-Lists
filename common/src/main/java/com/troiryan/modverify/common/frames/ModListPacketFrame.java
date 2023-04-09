package com.troiryan.modverify.common.frames;

import java.util.HashSet;

import com.troiryan.modverify.common.Constants;
import com.troiryan.modverify.common.Mod;

import io.netty.buffer.ByteBuf;

public class ModListPacketFrame extends PacketFrame {

    public ModListPacketFrame() {
        super();
    }

    public ModListPacketFrame(ByteBuf buf) {
        super(buf);
    }

    public ModListPacketFrame(byte[] byteArray) {
        super(byteArray);
    }

    @Override
    protected byte getDiscriminator() {
        return Constants.PACKET_MOD_LIST_DISC;
    }

    public boolean writePacket(HashSet<Mod> modSet) {

        if (!this.writeBasePacket())
            return false;
    
        this.buf.writeInt(modSet.size());
        for (Mod mod : modSet) {
            this.writeStringToBuffer(mod.getModID(), Constants.MAXIMUM_MOD_ID_LEN);
            this.writeStringToBuffer(mod.getVersion(), Constants.MAXIMUM_MOD_VERSION_LEN);
        }
        return true;
    }

    public HashSet<Mod> readPacket() throws Exception {

        if (!this.isPacketReadable())
            throw new Exception("Unreadable mod list packet");

        int savedWriterIndex = this.buf.writerIndex();
        HashSet<Mod> modSet = new HashSet<>();

        try {
            int numMods = this.buf.readInt();
            for (int i = 0; i < numMods; i++) {
                modSet.add(new Mod(this.readStringFromBuffer(Constants.MAXIMUM_MOD_ID_LEN), 
                                   this.readStringFromBuffer(Constants.MAXIMUM_MOD_VERSION_LEN)));
            }
        // catch and reset writerIndex before throwing to caller
        } catch (Exception e) {
            this.buf.writerIndex(savedWriterIndex);
            throw e;
        }

        this.buf.writerIndex(savedWriterIndex);
        return modSet;
    }
}
