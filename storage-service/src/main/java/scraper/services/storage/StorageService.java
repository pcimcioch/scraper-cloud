package scraper.services.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import scraper.common.FileUtils;
import scraper.exception.ResourceNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;

/**
 * Service used for storing and accessing files.
 */
@Service
@RefreshScope
public class StorageService {

    private final Path dataPath;

    private final Path tempPath;

    @Autowired
    public StorageService(@Value("${scraper.service.storage.dir:data}") String dataDir, @Value("${scraper.service.storage.temp:temp}") String tempDir, FileSystem fs)
            throws IOException {
        this.dataPath = fs.getPath(dataDir);
        this.tempPath = fs.getPath(tempDir);

        Files.createDirectories(dataPath);
        Files.createDirectories(tempPath);
    }

    /**
     * Saves file.
     *
     * @param inputStream stream of the data to save
     * @return id of the file - its sha2 checksum
     * @throws IOException              id io failed
     * @throws NoSuchAlgorithmException if sha algorithm is not supported
     */
    public String saveFile(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        Path tempFile = createTemp();
        try {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            String sha = FileUtils.computeSHA2(tempFile);
            saveIfMissing(tempFile, sha);

            return sha;
        } finally {
            Files.delete(tempFile);
        }
    }

    private Path createTemp() throws IOException {
        return Files.createTempFile(tempPath, "upload", "tmp");
    }

    private void saveIfMissing(Path tempFile, String sha) throws IOException {
        Path path = getPathForSha(sha);
        Files.createDirectories(path.getParent());

        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException ex) {
            return;
        }

        Files.copy(tempFile, path, StandardCopyOption.REPLACE_EXISTING);
    }

    private Path getPathForSha(String sha) {
        String firstTwoChars = sha.substring(0, 2);
        String secondTwoChars = sha.substring(2, 4);

        return dataPath.resolve(firstTwoChars).resolve(secondTwoChars).resolve(sha);
    }

    /**
     * Gets file.
     *
     * @param id id of the file. Its sha2 checksum.
     * @return path to the file. File under this path will exist
     * @throws IOException               if io failed
     * @throws IllegalArgumentException  if id has incorrect form
     * @throws ResourceNotFoundException if file with given id does not exist
     */
    public Path getFile(String id) throws IOException {
        if (id.length() < 5) {
            throw new IllegalArgumentException("Resource id " + id + " too short");
        }

        Path path = getPathForSha(id);
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("Resource [id=%s] not found");
        }

        return path;
    }
}
