package fr.nicopico.petitboutiste.utils.file

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PathExtTest {

    @Test
    fun `asString should return the string representation of the path`() {
        // GIVEN
        val path = Path("a/b/c")

        // WHEN
        val result = path.asString()

        // THEN
        assertEquals("a/b/c", result)
    }

    @Test
    fun `nameWithoutExtension should return the name without extension`() {
        assertEquals("file", Path("file.txt").nameWithoutExtension)
        assertEquals("file.tar", Path("file.tar.gz").nameWithoutExtension)
        assertEquals("file", Path("file").nameWithoutExtension)
        assertEquals("", Path(".hidden").nameWithoutExtension)
    }

    @Test
    fun `parentOrCurrent should return parent path or current directory`() {
        assertEquals("a/b", Path("a/b/c").parentOrCurrent.asString())
        assertEquals(".", Path("a").parentOrCurrent.asString())
        assertEquals(".", Path(".").parentOrCurrent.asString())
    }

    @Test
    fun `normalizePath should resolve dot and dot-dot segments`() {
        assertEquals("a/b/c", Path("a/./b/c").normalize().asString())
        assertEquals("a/c", Path("a/b/../c").normalize().asString())
        assertEquals("c", Path("a/b/../../c").normalize().asString())
        assertEquals("a/b", Path("a/b/c/..").normalize().asString())
        assertEquals("../a", Path("../a").normalize().asString())
        assertEquals("a/b/c", Path("a\\b\\c").normalize().asString())
    }

    @Test
    fun `normalizePath should preserve leading slash for absolute paths`() {
        assertEquals("/a/b/c", Path("/a/b/c").normalize().asString())
        assertEquals("/a/c", Path("/a/b/../c").normalize().asString())
        assertEquals("/", Path("/a/b/../..").normalize().asString())
    }

    @Test
    fun `relativeTo should compute relative path between two paths`() {
        assertEquals("b/c", Path("a/b/c").relativeTo(Path("a")))
        assertEquals("../c", Path("a/c").relativeTo(Path("a/b")))
        assertEquals("../../c/d", Path("c/d").relativeTo(Path("a/b")))
        assertEquals(".", Path("a/b").relativeTo(Path("a/b")))
    }

    @Test
    fun `relativeTo should handle absolute paths`() {
        assertEquals("b/c", Path("/a/b/c").relativeTo(Path("/a")))
        assertEquals("../c", Path("/a/c").relativeTo(Path("/a/b")))
        assertEquals("/a/b/c", Path("/a/b/c").relativeTo(Path("a")))
        assertEquals("a/b/c", Path("a/b/c").relativeTo(Path("/a")))
    }

    @Test
    fun `relativeTo should handle different Windows drives`() {
        val path = Path("C:/a/b/c")
        val base = Path("D:/a/b/c")

        // Should return the absolute path because they are on different drives
        assertEquals("C:/a/b/c", path.relativeTo(base))
    }

    @Test
    fun `normalizePath should preserve drive letter`() {
        assertEquals("C:/a/b/c", Path("C:\\a\\b\\c").normalize().asString())
        assertEquals("C:/a/b", Path("C:/a/b/c/..").normalize().asString())
    }

    @Test
    fun `relativeTo should return absolute path when roots are different`() {
        assertEquals("/a/b/c", Path("/a/b/c").relativeTo(Path("C:/a/b/c")))
        assertEquals("C:/a/b/c", Path("C:/a/b/c").relativeTo(Path("/a/b/c")))
    }

    @Test
    fun `exists should return true for existing file`() {
        // GIVEN
        val tempFile = createTempFile()

        try {
            // WHEN & THEN
            assertTrue(tempFile.exists())
        } finally {
            SystemFileSystem.delete(tempFile)
        }
    }

    @Test
    fun `exists should return false for non-existing file`() {
        // GIVEN
        val nonExistingFile = Path(SystemTemporaryDirectory, "non-existing-file-${kotlin.uuid.Uuid.random()}")

        // WHEN & THEN
        assertFalse(nonExistingFile.exists())
    }

    @Test
    fun `asSource and asSink should allow reading and writing to a file`() {
        // GIVEN
        val tempFile = createTempFile()
        val content = "Hello, World!"

        try {
            // WHEN
            tempFile.asSink().use { it.writeString(content) }
            val readContent = tempFile.asSource().use { it.readString() }

            // THEN
            assertEquals(content, readContent)
        } finally {
            SystemFileSystem.delete(tempFile)
        }
    }

    @Test
    fun `createTempFile should create a unique file in the specified directory`() {
        // GIVEN
        val directory = SystemTemporaryDirectory

        // WHEN
        val tempFile = createTempFile(directory = directory)

        try {
            // THEN
            assertTrue(tempFile.exists())
            assertEquals(directory, tempFile.parent)
            assertTrue(tempFile.name.startsWith("tmp-"))
        } finally {
            SystemFileSystem.delete(tempFile)
        }
    }

    @Test
    fun `absolutePath should return the absolute path of a file`() {
        // GIVEN
        val tempFile = createTempFile()

        try {
            // WHEN
            val absolutePath = tempFile.absolutePath

            // THEN
            assertTrue(absolutePath.isNotEmpty())
            // On JVM it should be an absolute path
            // We can't easily check for exact format in common code, but it shouldn't be empty
        } finally {
            SystemFileSystem.delete(tempFile)
        }
    }

    @Test
    fun `lastModified should return the last modified time of a file`() {
        // GIVEN
        val tempFile = createTempFile()

        try {
            // WHEN
            val lastModified = tempFile.lastModified()

            // THEN
            assertTrue(lastModified > 0)
        } finally {
            SystemFileSystem.delete(tempFile)
        }
    }
}
