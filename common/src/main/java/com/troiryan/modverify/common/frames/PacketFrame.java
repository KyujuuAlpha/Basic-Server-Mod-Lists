package com.troiryan.modverify.common.frames;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public abstract class PacketFrame {
    
    protected ByteBuf buf;
	private boolean readable, writable;

	public PacketFrame() {
		this.buf = Unpooled.buffer();
		this.readable = false;
		this.writable = true;
	}

    public PacketFrame(ByteBuf buf) {
        this.buf = buf.copy();
		this.readable = this.buf.readByte() == this.getDiscriminator();
		this.writable = false;
    }

    public PacketFrame(byte[] byteArray) {
        this.buf = Unpooled.wrappedBuffer(Arrays.copyOf(byteArray, byteArray.length));
		this.readable = this.buf.readByte() == this.getDiscriminator();
		this.writable = false;
    }

	public boolean isPacketReadable() {
		return this.readable;
	}

	public ByteBuf getByteBuf() {
		return this.buf;
	}

	public byte[] getByteArray() {
		if (!this.buf.hasArray()) {
			int savedReaderIndex = this.buf.readerIndex();
			this.buf.readerIndex(0);
			byte[] byteArray = ByteBufUtil.getBytes(this.buf);
			this.buf.readerIndex(savedReaderIndex);
			return byteArray;
		}
			
		return this.buf.array();
	}

	public boolean writeBasePacket() {
		if (!this.writable)
			return false;
		this.buf.writeByte(this.getDiscriminator());
		this.writable = false;
		return true;
	}

	/*
	 * Utility function for derived classes to use to write strings to the
	 * buffer, trimming the string to its maxLength
	 */
	protected void writeStringToBuffer(String string, int maxLength) {
		String trimmedString = string.substring(0, Math.min(string.length(), maxLength));
		byte[] stringByteArray = trimmedString.getBytes(StandardCharsets.UTF_8);
		this.buf.writeInt(stringByteArray.length);
		this.buf.writeBytes(stringByteArray);
	}

	/*
	 * Utility function for derived classes to use to read strings from the
	 * buffer
	 */
	protected String readStringFromBuffer(int maxLength) throws Exception {
		int charArrayLength = this.buf.readInt();
		if (charArrayLength > maxLength)
			throw new Exception("Buffer string exceeded max length of " + maxLength);
		String string = this.buf.toString(this.buf.readerIndex(), charArrayLength, StandardCharsets.UTF_8);
		this.buf.readerIndex(this.buf.readerIndex() + charArrayLength);
		return string;
	}

	protected abstract byte getDiscriminator();
}
