package com.troiryan.modverify.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashSet;

import com.troiryan.modverify.common.Constants;
import com.troiryan.modverify.common.Mod;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.text.Text;

@Environment(EnvType.SERVER)
public class BasicServerModListsServer implements DedicatedServerModInitializer  {

	@Override
	public void onInitializeServer() {
		BasicServerModLists.initializeModList();

		// player server login event
		ServerLoginConnectionEvents.QUERY_START.register((netHandler, server, packetSender, sync) -> packetSender.sendPacket(BasicServerModLists.MOD_REQ_CHANNEL, BasicServerModLists.createModRequestPacket()));

		// mod list packet handler
		ServerLoginNetworking.registerGlobalReceiver(BasicServerModLists.MOD_REQ_CHANNEL, (server, handler, understood, buf, sync, responseSender) -> {
			if (!understood) {
				handler.disconnect(Text.literal("This server requires " + Constants.PROJECT_NAME));
				return;
			}

			sync.waitFor(server.submit(() -> {
				try {
					HashSet<Mod> modSet = BasicServerModLists.decodeModListPacket(buf);
					HashSet<Mod> missingMods = BasicServerModLists.getMissingMods(modSet);
					if (!missingMods.isEmpty()) {
						String kickMessage = "Missing mods:";
						for (Mod mod : missingMods)
							kickMessage += "\n - " + mod.getModID() + " version " + mod.getVersion();

						handler.disconnect(Text.literal(kickMessage));
					}
				} catch (Exception e) {
					String disconnectMessage = "Malformed mod list packet: " + e.getMessage();
					BasicServerModLists.LOGGER.error(disconnectMessage);
					handler.disconnect(Text.literal(disconnectMessage));
				}
			}));
		});
	}
}