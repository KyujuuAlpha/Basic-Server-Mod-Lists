package com.troiryan.modverify.fabric;

import com.troiryan.modverify.common.Constants;
import com.troiryan.modverify.common.ModIdFilter;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;

public class HackyModVerify {

    protected static final String                  MOD_ID = Constants.PROJECT_ID;
	protected static final String    REQUIRED_MODS_CONFIG = "required-mods.txt";
	protected static final Identifier MOD_REQ_CHANNEL     = new Identifier(Constants.REQUEST_CHANNEL[0], Constants.REQUEST_CHANNEL[1]);
	protected static final PacketByteBuf MOD_REQ_PACKET   = new PacketByteBuf(Unpooled.wrappedBuffer(new byte[]{(byte) 2}).asReadOnly());

	protected static final Logger LOGGER = LoggerFactory.getLogger("hacky-mod-verify");

    protected static final List<ModIdFilter> ignoredModsFilters = Arrays.asList(new ModIdFilter[] {
		new ModIdFilter("org_jetbrains_kotlin_*"),
		new ModIdFilter("fabric*"),
		new ModIdFilter("com_google*"),
		new ModIdFilter("java*"),
		new ModIdFilter("minecraft*"),
		new ModIdFilter("org_jetbrains*"),
		new ModIdFilter(MOD_ID + "*")
	});

    protected static List<String> verifyModList = new ArrayList<>();

	protected static PacketByteBuf createModRequestPacket() {
		PacketByteBuf packetBuffer = PacketByteBufs.create();
		packetBuffer.writeByte(2);
		return packetBuffer;
	}

    protected static PacketByteBuf createModListPacket() {
		PacketByteBuf packetBuffer = PacketByteBufs.create();
		packetBuffer.writeByte(2);
		// packetBuffer.
		return packetBuffer;
	}

    protected static void initializeModList() {
        LOGGER.info("Running Hacky Mod Verification by Troi");

		List<ModIdFilter> modFilters = new ArrayList<>();

		// what to run only on servers, find required mods
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).resolve(REQUIRED_MODS_CONFIG);
			try (Stream<String>  lines = Files.lines(configPath)) {
				lines.forEachOrdered(line -> modFilters.add(new ModIdFilter(line)));
			} catch (IOException e) {
				LOGGER.error("Config does not exist, creating " + REQUIRED_MODS_CONFIG);
				try {
					Files.createDirectories(configPath.getParent());
					Files.createFile(configPath);
				} catch (IOException e2) {
					LOGGER.error("Failed to create " + REQUIRED_MODS_CONFIG + "!");
					return;
				}
			}
		}

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			ModMetadata modInfo = mod.getMetadata();
			String modString = modInfo.getId() + "_" + modInfo.getVersion();
			String filteredModString = modString.replaceAll("[^a-z0-9/._-]", "");

			if (ignoredModsFilters.stream().anyMatch(s -> s.match(filteredModString)))
				continue;

			if (modFilters.isEmpty() || modFilters.stream().anyMatch(s -> s.match(filteredModString)))
			{
				LOGGER.info("Found mod for verification: " + filteredModString);
				verifyModList.add(filteredModString);
			}
		}
	}
}