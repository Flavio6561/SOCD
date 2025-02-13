package com.socd;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOCDClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SOCDClient.class);
    private static double forwardReleaseTime;
    private static double backwardReleaseTime;
    private static double rightReleaseTime;
    private static double leftReleaseTime;
    private static boolean socdForward;
    private static boolean socdBackward;
    private static boolean socdRight;
    private static boolean socdLeft;
    private static KeyBinding forwardKey;
    private static KeyBinding backKey;
    private static KeyBinding rightKey;
    private static KeyBinding leftKey;
    private static int forwardKeyInt;
    private static int backKeyInt;
    private static int rightKeyInt;
    private static int leftKeyInt;
    private static boolean keysAssigned = false;

    protected static boolean toggleMod = true;
    protected static boolean toggleMovement = true;
    protected static boolean toggleStrafe = true;

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        CommandRegister.registerCommands();
        LOGGER.info("SOCD initialized with success");
        WorldRenderEvents.END.register(context -> manageSOCD());
    }

    private static void manageSOCD() {
        if (MinecraftClient.getInstance().options == null || !toggleMod)
            return;
        updateKeys();
        updateReleaseTimes(System.currentTimeMillis());
        if (toggleMovement) {
            if (forwardKey.isPressed() && backKey.isPressed()) {
                if (forwardReleaseTime < backwardReleaseTime) {
                    forwardKey.setPressed(false);
                    socdForward = true;
                }
                if (forwardReleaseTime > backwardReleaseTime) {
                    backKey.setPressed(false);
                    socdBackward = true;
                }
            }
            if (socdForward && isRawKeyPressed(forwardKeyInt) && !isRawKeyPressed(backKeyInt)) {
                forwardKey.setPressed(true);
                socdForward = false;
            }
            if (socdBackward && isRawKeyPressed(backKeyInt) && !isRawKeyPressed(forwardKeyInt)) {
                backKey.setPressed(true);
                socdBackward = false;
            }
        }

        if (toggleStrafe) {
            if (rightKey.isPressed() && leftKey.isPressed()) {
                if (rightReleaseTime < leftReleaseTime) {
                    rightKey.setPressed(false);
                    socdRight = true;
                }
                if (rightReleaseTime > leftReleaseTime) {
                    leftKey.setPressed(false);
                    socdLeft = true;
                }
            }
            if (socdRight && isRawKeyPressed(rightKeyInt) && !isRawKeyPressed(leftKeyInt)) {
                rightKey.setPressed(true);
                socdRight = false;
            }
            if (socdLeft && isRawKeyPressed(leftKeyInt) && !isRawKeyPressed(rightKeyInt)) {
                leftKey.setPressed(true);
                socdLeft = false;
            }
        }
    }

    private static void updateReleaseTimes(double time) {
        if (!forwardKey.isPressed()) forwardReleaseTime = time;
        if (!backKey.isPressed()) backwardReleaseTime = time;
        if (!rightKey.isPressed()) rightReleaseTime = time;
        if (!leftKey.isPressed()) leftReleaseTime = time;
    }

    private static void updateKeys() {
        if (keysAssigned)
            return;
        keysAssigned = true;
        forwardKey = MinecraftClient.getInstance().options.forwardKey;
        backKey = MinecraftClient.getInstance().options.backKey;
        rightKey = MinecraftClient.getInstance().options.rightKey;
        leftKey = MinecraftClient.getInstance().options.leftKey;
        try {
            forwardKeyInt = GLFW.class.getField("GLFW_KEY_" + forwardKey.getBoundKeyTranslationKey().replaceAll("key.keyboard.", "").toUpperCase().replaceAll("\\.", "_")).getInt(null);
            backKeyInt = GLFW.class.getField("GLFW_KEY_" + backKey.getBoundKeyTranslationKey().replaceAll("key.keyboard.", "").toUpperCase().replaceAll("\\.", "_")).getInt(null);
            rightKeyInt = GLFW.class.getField("GLFW_KEY_" + rightKey.getBoundKeyTranslationKey().replaceAll("key.keyboard.", "").toUpperCase().replaceAll("\\.", "_")).getInt(null);
            leftKeyInt = GLFW.class.getField("GLFW_KEY_" + leftKey.getBoundKeyTranslationKey().replaceAll("key.keyboard.", "").toUpperCase().replaceAll("\\.", "_")).getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            sendMessageToPlayer("One or multiple keys are not supported SOCD keys. Replace them with GLFW supported keys");
            LOGGER.warn("One or multiple keys are not supported SOCD keys. Replace them with GLFW supported keys");
            forwardKeyInt = GLFW.GLFW_KEY_W;
            backKeyInt = GLFW.GLFW_KEY_S;
            rightKeyInt = GLFW.GLFW_KEY_D;
            leftKeyInt = GLFW.GLFW_KEY_A;
        }
    }

    private static boolean isRawKeyPressed(int key) {
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }

    public static void sendMessageToPlayer(String chat_message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null)
            client.player.sendMessage(Text.literal(chat_message), false);
    }

    public static int toggleMod(CommandContext<FabricClientCommandSource> ignoredFabricClientCommandSourceCommandContext) {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
        sendMessageToPlayer("Mod is now " + (toggleMod ? "enabled" : "disabled"));
        return 1;
    }

    public static int toggleMovement(CommandContext<FabricClientCommandSource> ignoredFabricClientCommandSourceCommandContext) {
        toggleMovement = !toggleMovement;
        ConfigManager.saveConfig();
        sendMessageToPlayer("Movement SOCD is now " + (toggleMovement ? "enabled" : "disabled"));
        return 1;
    }

    public static int toggleStrafe(CommandContext<FabricClientCommandSource> ignoredFabricClientCommandSourceCommandContext) {
        toggleStrafe = !toggleStrafe;
        ConfigManager.saveConfig();
        sendMessageToPlayer("Strafe SOCD is now " + (toggleStrafe ? "enabled" : "disabled"));
        return 1;
    }

    public static int updateKeys(CommandContext<FabricClientCommandSource> ignoredFabricClientCommandSourceCommandContext) {
        keysAssigned = false;
        updateKeys();
        return 1;
    }
}