package com.troiryan.modverify.velocity;

import com.troiryan.modverify.common.Constants;
import com.troiryan.modverify.common.frames.ModListPacketFrame;
import com.troiryan.modverify.common.frames.ModRequestPacketFrame;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;

import com.velocitypowered.api.event.player.ServerLoginPluginMessageEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.LoginPhaseConnection;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;

@Plugin(
	id = Constants.PROJECT_ID,
	name = Constants.PROJECT_NAME,
	description = Constants.DESCRIPTION,
	version = Constants.VERSION,
	authors = { Constants.AUTHOR }
)
public final class BasicServerModListsVelocity {

	private final Logger logger;

	protected static final ChannelIdentifier MOD_REQ_CHANNEL = MinecraftChannelIdentifier.create(Constants.REQUEST_CHANNEL[0], Constants.REQUEST_CHANNEL[1]);

	@Inject
	public BasicServerModListsVelocity(
			final ProxyServer proxyServer,
			final Logger logger,
			final @DataDirectory Path path,
			final PluginManager pluginManager,
			final EventManager eventManager,
			final CommandManager commandManager
	) {
		this.logger = logger;
	}
	
	@Subscribe
	void onProxyInitialization(final ProxyInitializeEvent event) {
		logger.info(Constants.PROJECT_NAME + " by " + Constants.AUTHOR);
	}

  	private HashMap<String, ModListPacketFrame> playerPacketCache = new HashMap<>();

	@Subscribe
	public void onServerLogin(ServerLoginPluginMessageEvent event) {
		// server join request
		if (event.getIdentifier().equals(MOD_REQ_CHANNEL)) {
			String playerUsername = event.getConnection().getPlayer().getUsername();
	
			// player's cache already exists.. note it
			if (!playerPacketCache.containsKey(playerUsername)) {
				logger.info("No cached mod list found for " + playerUsername + "!");
				return;
			}
			
			logger.info("Sending mod list for " + playerUsername + " to " + event.getConnection().getServer().getServerInfo().getName());
			event.setResult(ServerLoginPluginMessageEvent.ResponseResult.reply(playerPacketCache.get(playerUsername).getByteArray()));
		}
	}

	@Subscribe
	public void onClientLoginInitiated(PreLoginEvent event) {
		LoginPhaseConnection connection = (LoginPhaseConnection) event.getConnection();
		ModRequestPacketFrame modRequest = new ModRequestPacketFrame();
		modRequest.writeBasePacket();
		byte[] arr = modRequest.getByteArray();

		// server join request
		connection.sendLoginPluginMessage(MOD_REQ_CHANNEL, arr, (buf) -> {
			// player must not have the mod..
			if (buf == null)
				return;

			// player's cache already exists.. remove it
			if (playerPacketCache.containsKey(event.getUsername()))
				playerPacketCache.remove(event.getUsername());
	
			logger.info("Cached player " + event.getUsername() + "'s' mod list");
			playerPacketCache.put(event.getUsername(), new ModListPacketFrame(buf));
		});
	}

	@Subscribe
	public void onClientLoginDisconnect(DisconnectEvent event) {
		if (!playerPacketCache.containsKey(event.getPlayer().getUsername()))
			return;

		logger.info("Removing cached mod list for " + event.getPlayer().getUsername());
		playerPacketCache.remove(event.getPlayer().getUsername());
	}
}