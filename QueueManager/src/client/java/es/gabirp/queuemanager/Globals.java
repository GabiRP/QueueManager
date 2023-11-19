package es.gabirp.queuemanager;

import net.minecraft.client.MinecraftClient;

public class Globals {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static Boolean isQueueManagerEnabled = false;
    public static String endpointUrl = "ws://127.0.0.1:8080/queue";
}
