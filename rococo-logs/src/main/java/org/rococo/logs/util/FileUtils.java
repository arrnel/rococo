package org.rococo.logs.util;

import lombok.extern.slf4j.Slf4j;
import org.rococo.logs.data.log.LogEntity;
import org.rococo.logs.model.ServiceName;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileUtils {

    private static final String LOG_SUFFIX = ".log";
    private static final String ARCHIVE_SUFFIX = ".zip";
    private static final String ROCOCO_LOGS = "rococo-logs";

    public static Resource createLogFile(ServiceName serviceName, List<LogEntity> logs) throws IOException {

        var tempDir = Files.createTempDirectory(serviceName.getServiceName());
        var tempFile = Files.createFile(tempDir.resolve("%s%s".formatted(serviceName.getServiceName(), LOG_SUFFIX)));

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            for (LogEntity log : logs)
                writer.write("%s%n".formatted(log.getMessage()));
        }

        return new FileSystemResource(tempFile.toFile());

    }

    public static Resource packFilesToArchive(Map<ServiceName, List<LogEntity>> servicesLogs) throws IOException {

        var tempDir = Files.createTempDirectory(ROCOCO_LOGS);
        var tempZipFile = Files.createFile(tempDir.resolve("%s%s".formatted(ROCOCO_LOGS, ARCHIVE_SUFFIX)));

        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(tempZipFile))) {

            for (Map.Entry<ServiceName, List<LogEntity>> entry : servicesLogs.entrySet()) {
                String serviceName = entry.getKey().getServiceName();
                Path logFile = tempDir.resolve("%s%s".formatted(serviceName, LOG_SUFFIX));

                try (BufferedWriter writer = Files.newBufferedWriter(logFile)) {
                    for (LogEntity log : entry.getValue())
                        writer.write(String.format("%s%n", log.getMessage()));
                }

                zipOut.putNextEntry(new ZipEntry(logFile.getFileName().toString()));
                Files.copy(logFile, zipOut);
                zipOut.closeEntry();

            }
        }

        return new FileSystemResource(tempZipFile.toFile());

    }

    private static void deleteDirectory(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            log.warn("Unsuccessful operation of removing directory: {}.%nMessage: {};%nStackTrace: {}",
                    directory, e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
    }

}
