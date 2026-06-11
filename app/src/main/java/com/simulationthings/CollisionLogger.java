package com.simulationthings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CollisionLogger implements AutoCloseable {

    private final BufferedWriter writer;
    private final Path filePath;
    private volatile boolean closed = false;

    public CollisionLogger(String fileName) throws IOException {
        Path file = Paths.get(System.getProperty("user.dir")).resolve(fileName);
        this.filePath = file.toAbsolutePath();
        File parent = file.toFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        this.writer = new BufferedWriter(new FileWriter(file.toFile(), false));
        writer.write("time_seconds,collision_x,collision_y");
        writer.newLine();
        writer.flush();
    }

    public void log(double seconds, int x, int y) {
        if (closed) return;
        try {
            writer.write(String.format("%.3f,%d,%d", seconds, x, y));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write collision log: " + e.getMessage());
        }
    }

    public Path getFilePath() {
        return filePath;
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to close collision log file: " + e.getMessage());
        }
    }
}
