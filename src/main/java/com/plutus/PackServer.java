package com.plutus;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackServer {

    private final PlutusMacePlugin plugin;
    private HttpServer server;
    private byte[] packBytes;
    private String packHash;
    private int port;

    public PackServer(PlutusMacePlugin plugin) {
        this.plugin = plugin;
        this.port = plugin.getConfig().getInt("resource-pack.port", 19133);
    }

    public void start() {
        try {
            createPack();
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/pack.zip", exchange -> {
                exchange.sendResponseHeaders(200, packBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(packBytes);
                os.close();
            });
            server.setExecutor(null);
            server.start();
            plugin.getLogger().info("Internal resource pack server started on port " + port);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to start resource pack server: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void createPack() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            // pack.mcmeta
            addFileToZip(zos, "pack.mcmeta", "{\"pack\":{\"description\":\"Plutus Mace Assets\",\"pack_format\":37}}");

            // Texture
            addResourceToZip(zos, "assets/minecraft/textures/item/plutus_mace.png");

            // Models
            addResourceToZip(zos, "assets/minecraft/models/item/plutus_mace.json");
            addResourceToZip(zos, "assets/minecraft/models/item/mace.json");
        }

        this.packBytes = baos.toByteArray();

        // Calculate SHA-1
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(packBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        this.packHash = sb.toString();
    }

    private void addFileToZip(ZipOutputStream zos, String path, String content) throws IOException {
        zos.putNextEntry(new ZipEntry(path));
        zos.write(content.getBytes());
        zos.closeEntry();
    }

    private void addResourceToZip(ZipOutputStream zos, String path) throws IOException {
        InputStream is = plugin.getResource(path);
        if (is == null) {
            plugin.getLogger().warning("Resource not found for pack: " + path);
            return;
        }
        zos.putNextEntry(new ZipEntry(path));
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > 0) zos.write(buffer, 0, len);
        zos.closeEntry();
        is.close();
    }

    public void sendPack(Player player) {
        String url = "http://" + plugin.getConfig().getString("resource-pack.host", "your-ip-here") + ":" + port + "/pack.zip";
        if (url.contains("your-ip-here")) {
            plugin.getLogger().warning("Resource pack host is not set in config.yml! Pack cannot be sent.");
            return;
        }
        player.setResourcePack(url, packHash, true, net.kyori.adventure.text.Component.text("Please accept the Plutus Mace textures!"));
    }
}
