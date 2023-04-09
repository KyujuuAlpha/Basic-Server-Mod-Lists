package com.troiryan.modverify.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;

@Environment(EnvType.CLIENT)
public class BasicServerModListsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BasicServerModLists.initializeModList();

		// mod list request packet handler
		ClientLoginNetworking.registerGlobalReceiver(BasicServerModLists.MOD_REQ_CHANNEL, (client, handler, buf, listenerAdder) -> {
			return client.submit(() -> {
				if (!BasicServerModLists.decodeModRequestPacket(buf)) {
					BasicServerModLists.LOGGER.error("Malformed mod request packet");
					return null;
				}
				return BasicServerModLists.createModListPacket();
			});
		});
	}
}