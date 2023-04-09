package com.troiryan.modverify.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.text.Text;

@Environment(EnvType.SERVER)
public class HackyModVerifyServer implements DedicatedServerModInitializer  {

	@Override
	public void onInitializeServer() {
		HackyModVerify.initializeModList();

		// player server login event
		ServerLoginConnectionEvents.QUERY_START.register((netHandler, server, packetSender, sync) -> packetSender.sendPacket(HackyModVerify.MOD_REQ_CHANNEL, HackyModVerify.createModRequestPacket()));

		// mod list packet handler
		ServerLoginNetworking.registerGlobalReceiver(HackyModVerify.MOD_REQ_CHANNEL, (server, handler, understood, buf, sync, responseSender) -> {
			HackyModVerify.LOGGER.error("hello! " + understood);
			if (!understood)
				return;

			sync.waitFor(server.submit(() -> {
				handler.disconnect(Text.literal("Invalid mods!"));
			}));
		});
	}
}