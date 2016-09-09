package scraper.services.storage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import scraper.common.FileUtils;
import scraper.exception.ResourceNotFoundException;
import scraper.test.FileSystemRule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StorageServiceTest {

    @Rule
    public final FileSystemRule rule = new FileSystemRule();

    private StorageService storageService;

    private FileSystem fs;

    @Before
    public void setUp() throws IOException {
        fs = rule.getFileSystem();
        storageService = new StorageService("data", "temp", fs);
    }

    @Test
    public void testSaveFile_oneFile() throws Exception {
        // given
        String fileContent = "Something\nis here";
        InputStream inStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));

        // sanity
        assertCountFiles(0, "temp");
        assertCountFiles(0, "data");

        // when
        String sha2 = storageService.saveFile(inStream);

        // then
        assertEquals("02C85CEA9E45CC036DAE7A44C487EB0BE95E5FF6E3D73C2F7DBB66E58C38F869", sha2);
        assertExists(fileContent, "data", "02", "C8", "02C85CEA9E45CC036DAE7A44C487EB0BE95E5FF6E3D73C2F7DBB66E58C38F869");
        assertCountFiles(0, "temp");
        assertCountFiles(1, "data");
    }

    @Test
    public void testSaveFile_multipleFiles() throws Exception {
        // given
        String file1Content = "Something\nis here";
        String file2Content = "Another file";
        String file3Content = "This\nis\nmadness";
        InputStream inStream1 = new ByteArrayInputStream(file1Content.getBytes(StandardCharsets.UTF_8));
        InputStream inStream2 = new ByteArrayInputStream(file2Content.getBytes(StandardCharsets.UTF_8));
        InputStream inStream3 = new ByteArrayInputStream(file3Content.getBytes(StandardCharsets.UTF_8));

        // sanity
        assertCountFiles(0, "temp");
        assertCountFiles(0, "data");

        // when
        String file1sha2 = storageService.saveFile(inStream1);
        String file2sha2 = storageService.saveFile(inStream2);
        String file3sha2 = storageService.saveFile(inStream3);

        // then
        assertEquals("02C85CEA9E45CC036DAE7A44C487EB0BE95E5FF6E3D73C2F7DBB66E58C38F869", file1sha2);
        assertEquals("2D7B141433885598043EFAB600B0A582FC3921C3D2A0B67D8FCDAE45DAC3F27B", file2sha2);
        assertEquals("AA6F79E6B96E48EF6BB1399889AE8361C943E34197A9C212C8BF7178618D14B6", file3sha2);
        assertExists(file1Content, "data", "02", "C8", "02C85CEA9E45CC036DAE7A44C487EB0BE95E5FF6E3D73C2F7DBB66E58C38F869");
        assertExists(file2Content, "data", "2D", "7B", "2D7B141433885598043EFAB600B0A582FC3921C3D2A0B67D8FCDAE45DAC3F27B");
        assertExists(file3Content, "data", "AA", "6F", "AA6F79E6B96E48EF6BB1399889AE8361C943E34197A9C212C8BF7178618D14B6");
        assertCountFiles(0, "temp");
        assertCountFiles(3, "data");
    }

    @Test
    public void testSaveFile_duplicateFiles() throws Exception {
        // given
        String fileContent = "Something\nis here";
        InputStream inStream1 = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
        InputStream inStream2 = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));

        // sanity
        assertCountFiles(0, "temp");
        assertCountFiles(0, "data");

        // when
        String file1sha2 = storageService.saveFile(inStream1);
        String file2sha2 = storageService.saveFile(inStream2);

        // then
        assertEquals("02C85CEA9E45CC036DAE7A44C487EB0BE95E5FF6E3D73C2F7DBB66E58C38F869", file1sha2);
        assertEquals("02C85CEA9E45CC036DAE7A44C487EB0BE95E5FF6E3D73C2F7DBB66E58C38F869", file2sha2);
        assertExists(fileContent, "data", "02", "C8", "02C85CEA9E45CC036DAE7A44C487EB0BE95E5FF6E3D73C2F7DBB66E58C38F869");
        assertCountFiles(0, "temp");
        assertCountFiles(1, "data");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFile_tooShortId() throws IOException {
        // when
        storageService.getFile("1234");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFile_missingFile_noOtherFiles() throws IOException {
        // when
        storageService.getFile("02C85CEA9E45CC036DAE7A44C487EB0BE95E5FF6E3D73C2F7DBB66E58C38F869");
    }

    @Test
    public void testGetFile_missingFile_otherFiles() throws Exception {
        // given
        Path existingFile = fs.getPath("data", "12", "AB", "12ABCD");
        Files.createDirectories(existingFile.getParent());
        Files.createFile(existingFile);

        // when
        try {
            storageService.getFile("123456");
            fail();
        } catch (ResourceNotFoundException ex) {
            // then
        }
    }

    @Test
    public void testGetFile() throws Exception {
        // given
        Path existingFile = fs.getPath("data", "12", "AB", "12ABCD");
        Files.createDirectories(existingFile.getParent());
        Files.createFile(existingFile);

        // when
        Path obtainedPath = storageService.getFile("12ABCD");

        // then
        assertEquals(existingFile, obtainedPath);
    }

    private void assertExists(String fileContent, String dir, String... path) throws IOException {
        Path p = FileUtils.resolve(fs.getPath(""), dir, path);

        assertTrue(Files.exists(p));
        assertEquals(fileContent, FileUtils.readFile(p, StandardCharsets.UTF_8));
    }

    private void assertCountFiles(long expectedCount, String dir) throws IOException {
        Path path = fs.getPath(dir);
        if (!Files.exists(path)) {
            if (expectedCount == 0) {
                return;
            } else {
                fail();
            }
        }

        long filesCount = Files.walk(path).filter(Files::isRegularFile).count();
        assertEquals(expectedCount, filesCount);
    }
}