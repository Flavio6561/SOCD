package com.socd;

import net.fabricmc.fabric.api.client.command.v2.*;

public class CommandRegister {
    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("SOCD")
                        .executes(SOCDClient::toggleMod)
                        .then(ClientCommandManager.literal("Movement")
                                .executes(SOCDClient::toggleMovement))
                        .then(ClientCommandManager.literal("Strafe")
                                .executes(SOCDClient::toggleStrafe))
                        .then(ClientCommandManager.literal("UpdateKeys")
                                .executes(SOCDClient::updateKeys))
        ));
    }
}