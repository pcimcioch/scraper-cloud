package scraper.services.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import scraper.exception.UnexpectedException;

import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/**
 * Rest controller for managing files.
 */
@RestController
@MultipartConfig(fileSizeThreshold = 52_428_800)
public class FilesController {

    private final StorageService service;

    @Autowired
    public FilesController(StorageService service) {
        this.service = service;
    }

    /**
     * Uploads file.
     *
     * @param uploadedFileRef file to upload
     * @return file id
     */
    @RequestMapping(path = "/", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("uploadedFile") MultipartFile uploadedFileRef) {
        try (InputStream inStream = uploadedFileRef.getInputStream()) {
            return service.saveFile(inStream);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new UnexpectedException("Unable to process file stream", e);
        }
    }

    /**
     * Downloads file.
     *
     * @param id id of the file
     * @return file
     */
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("id") String id) {
        try {
            Path file = service.getFile(id);
            long contentLength = Files.size(file);
            InputStream inStream = Files.newInputStream(file);

            // TODO investigate UrlResource
            return ResponseEntity.ok().contentLength(contentLength).contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(inStream));
        } catch (IOException e) {
            throw new UnexpectedException("Unable to process file stream", e);
        }
    }
}
