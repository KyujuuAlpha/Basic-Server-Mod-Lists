package com.troiryan.modverify.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;

@Environment(EnvType.CLIENT)
public class HackyModVerifyClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HackyModVerify.initializeModList();
		
		// mod list request packet handler
		ClientLoginNetworking.registerGlobalReceiver(HackyModVerify.MOD_REQ_CHANNEL, (client, handler, buf, listenerAdder) -> {
			HackyModVerify.LOGGER.info("RECEIVED PACKET");
			return client.submit(() -> {
				HackyModVerify.LOGGER.info("ACK PACKET SENT");
				return HackyModVerify.createModListPacket();
			});
		});
	}
}