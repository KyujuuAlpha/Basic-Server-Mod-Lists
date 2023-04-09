package com.troiryan.modverify.fabric;

import com.troiryan.modverify.common.Constants;
import com.troiryan.modverify.common.Mod;
import com.troiryan.modverify.common.ModIdFilter;
import com.troiryan.modverify.common.frames.ModListPacketFrame;
import com.troiryan.modverify.common.frames.ModRequestPacketFrame;

import net.fabricmc.api.EnvType;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicServerModLists {

    protected static final String                  MOD_ID = Constants.PROJECT_ID;
	protected static final String    REQUIRED_MODS_CONFIG = "required-mods.txt";
	protected static final Identifier MOD_REQ_CHANNEL     = new Identifier(Constants.REQUEST_CHANNEL[0], Constants.REQUEST_CHANNEL[1]);

	protected static final Logger LOGGER = LoggerFactory.getLogger(Constants.PROJECT_ID);

    protected static final List<ModIdFilter> ignoredModsFilters = Arrays.asList(new ModIdFilter[] {
		new ModIdFilter("org_jetbrains_kotlin_*"),
		new ModIdFilter("fabric*"),
		new ModIdFilter("com_google*"),
		new ModIdFilter("java*"),
		new ModIdFilter("minecraft*"),
		new ModIdFilter("org_jetbrains*"),
		new ModIdFilter("porting_lib*"),
		new ModIdFilter(MOD_ID + "*")
	});

    protected static HashSet<Mod> verifyModSet = new HashSet<>();

	protected static PacketByteBuf createModRequestPacket() {
		ModRequestPacketFrame modRequest = new ModRequestPacketFrame();
		modRequest.writeBasePacket();
		return new PacketByteBuf(modRequest.getByteBuf());
	}

	protected static boolean decodeModRequestPacket(PacketByteBuf packetBuffer) {
		ModRequestPacketFrame modRequest = new ModRequestPacketFrame(packetBuffer.asReadOnly());
		return modRequest.isPacketReadable();
	}

    protected static PacketByteBuf createModListPacket() {
		ModListPacketFrame modsPacket = new ModListPacketFrame();
		modsPacket.writePacket(verifyModSet);
		return new PacketByteBuf(modsPacket.getByteBuf());
	}

	protected static HashSet<Mod> decodeModListPacket(PacketByteBuf packetBuffer) throws Exception {
		ModListPacketFrame modsPacket = new ModListPacketFrame(packetBuffer.asReadOnly());
		return modsPacket.readPacket();
	}

	protected static HashSet<Mod> getMissingMods(HashSet<Mod> modSet) {
		HashSet<Mod> missingMods = new HashSet<>();
		for (Mod requiredMod : verifyModSet) {
			if (!modSet.contains(requiredMod))
				missingMods.add(requiredMod);
		}
		return missingMods;
	}

    protected static void initializeModList() {
        LOGGER.info(Constants.PROJECT_NAME + " by " + Constants.AUTHOR);

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

		for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
			ModMetadata modInfo = modContainer.getMetadata();
			Mod mod = new Mod(modInfo.getId(), modInfo.getVersion().getFriendlyString());
			String combinedModString = mod.getCombinedString();

			if (ignoredModsFilters.stream().anyMatch(s -> s.match(combinedModString)))
				continue;

			if (modFilters.isEmpty() || modFilters.stream().anyMatch(s -> s.match(combinedModString))) {
				LOGGER.info("Found mod for verification: " + combinedModString);
				verifyModSet.add(mod);
			}
		}
	}
}