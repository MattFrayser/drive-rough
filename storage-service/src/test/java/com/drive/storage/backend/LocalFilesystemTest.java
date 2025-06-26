// ============================================================================
// Unit Tests for LocalFilesystem Backend
// ============================================================================

package com.drive.storage.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LocalFilesystemTest {

    private LocalFilesystem localStorage;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        localStorage = new LocalFilesystem();
        // Use reflection to set the basePath since it's a private field
        ReflectionTestUtils.setField(localStorage, "basePath", tempDir.toString());
    }

    @Test
    void testStoreFile() throws Exception {
        String content = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        String filename = "test.txt";

        String storagePath = localStorage.store(inputStream, filename);

        assertNotNull(storagePath);
        assertTrue(storagePath.contains("test.txt"));
        assertTrue(localStorage.exists(storagePath));
    }

    @Test
    void testRetrieveFile() throws Exception {
        String content = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        String filename = "test.txt";

        // Store file
        String storagePath = localStorage.store(inputStream, filename);

        // Retrieve file
        InputStream retrievedStream = localStorage.retrieve(storagePath);
        String retrievedContent = new String(retrievedStream.readAllBytes());

        assertEquals(content, retrievedContent);
    }

    @Test
    void testRetrieveNonExistentFile() {
        assertThrows(Exception.class, () -> {
            localStorage.retrieve("non-existent-file.txt");
        });
    }

    @Test
    void testDeleteFile() throws Exception {
        String content = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        String filename = "test.txt";

        // Store file
        String storagePath = localStorage.store(inputStream, filename);
        assertTrue(localStorage.exists(storagePath));

        // Delete file
        localStorage.delete(storagePath);
        assertFalse(localStorage.exists(storagePath));
    }

    @Test
    void testDeleteNonExistentFile() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            localStorage.delete("non-existent-file.txt");
        });
    }

    @Test
    void testExistsFile() throws Exception {
        String content = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        String filename = "test.txt";

        String storagePath = localStorage.store(inputStream, filename);

        assertTrue(localStorage.exists(storagePath));
        assertFalse(localStorage.exists("non-existent-file.txt"));
    }

    @Test
    void testUniqueFilenames() throws Exception {
        String content = "Hello, World!";
        String filename = "test.txt";

        InputStream inputStream1 = new ByteArrayInputStream(content.getBytes());
        InputStream inputStream2 = new ByteArrayInputStream(content.getBytes());

        String storagePath1 = localStorage.store(inputStream1, filename);
        String storagePath2 = localStorage.store(inputStream2, filename);

        assertNotEquals(storagePath1, storagePath2);
        assertTrue(localStorage.exists(storagePath1));
        assertTrue(localStorage.exists(storagePath2));
    }
}
