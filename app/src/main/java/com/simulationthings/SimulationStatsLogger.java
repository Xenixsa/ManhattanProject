package com.simulationthings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class SimulationStatsLogger implements AutoCloseable {

    private final BufferedWriter writer;
    private final Path filePath;
    private volatile boolean closed = false;

    public SimulationStatsLogger(String fileName) throws IOException {
        Path file = Paths.get(System.getProperty("user.dir")).resolve(fileName);
        this.filePath = file.toAbsolutePath();
        File parent = file.toFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        this.writer = new BufferedWriter(new FileWriter(file.toFile(), false));
        writer.write("time_seconds,active_neutrons,visible_atoms,active_fragments");
        writer.newLine();
        writer.flush();
    }

    public void log(double seconds, int neutrons, int atoms, int fragments) {
        if (closed) return;
        try {
            writer.write(String.format(Locale.US, "%.1f,%d,%d,%d", seconds, neutrons, atoms, fragments));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write simulation stats: " + e.getMessage());
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
            System.err.println("Failed to close simulation stats file: " + e.getMessage());
        }
    }
}
