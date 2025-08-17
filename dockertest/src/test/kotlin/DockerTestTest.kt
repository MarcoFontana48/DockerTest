import mf.vil.dockertest.DockerTest
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.File

class DockerTestTest : DockerTest() {   // extend the test class with 'DockerTest' class to make it use the docker-compose methods
    private val logger = LogManager.getLogger(this::class)
    private val dockerComposePath = "/docker-compose.yml"
    private lateinit var dockerComposeFile: File

    @BeforeEach
    fun setUp() {
        // Load the docker-compose file from resources
        logger.trace("Loading docker-compose file from resources at path: {}", dockerComposePath)
        val dockerComposeResource = this::class.java.getResource(dockerComposePath) ?: throw Exception("Resource not found")
        dockerComposeFile = File(dockerComposeResource.toURI())
        logger.trace("Docker Compose file loaded successfully at path: {}", dockerComposeFile.absolutePath)
    }

    @AfterEach
    fun tearDown() {
        // Clean up by stopping the docker-compose services after each test
        logger.trace("Tearing down after test, stopping docker-compose services.")
        executeDockerComposeDown(dockerComposeFile)
    }

    @Test
    @DisplayName("Ensure Docker Compose File Exists")
    @Timeout(5 * 60)
    fun ensureDockerComposeFileExists() {
        // Check if the docker-compose file exists
        logger.trace("Checking if Docker Compose file exists at path: {}", dockerComposeFile.absolutePath)
        if (!dockerComposeFile.exists()) {
            logger.error("Docker Compose file does not exist at path: {}", dockerComposeFile.absolutePath)
            throw IllegalStateException("Docker Compose file does not exist at path: ${dockerComposeFile.absolutePath}")
        }

        // If the file exists, the test passes. It would throw an exception if it doesn't.
        assertTrue(true, "Docker Compose file exists at path: ${dockerComposeFile.absolutePath}")
    }

    @Test
    @DisplayName("Test Docker Compose Up with existing file")
    @Timeout(5 * 60)
    fun testDockerComposeUpMethodWithExistingFile() {
        // Execute the docker-compose up command
        logger.trace("Executing docker-compose up with file: {}", dockerComposeFile.absolutePath)
        executeDockerComposeUp(dockerComposeFile)
    }

    @Test
    @DisplayName("Test Docker Compose Up with Non-Existent File")
    @Timeout(5 * 60)
    fun testDockerComposeUpMethodWithNonExistentFile() {
        // Create a non-existent file
        val nonExistentFile = File("non-existent-compose.yml")
        logger.trace("Testing docker-compose up with non-existent file: {}", nonExistentFile.absolutePath)
        try {
            executeDockerComposeUp(nonExistentFile)
            // If the command executes, the test should fail
            assertTrue(false, "Expected an exception for non-existent file, but command executed successfully.")
        } catch (e: IllegalStateException) {
            // Check if the exception message is as expected
            logger.trace("Caught expected exception: {}", e.message)
            assertTrue(
                e.message?.contains("File not found") == true,
                "Expected 'File not found' exception, but got: ${e.message}"
            )
        } catch (e: Exception) {
            // If any other exception is thrown, the test should fail
            logger.error("Unexpected exception: {}", e.message)
            assertTrue(false, "Unexpected exception thrown: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test Docker Compose Down with existing file")
    @Timeout(5 * 60)
    fun testDockerComposeDownMethodWithExistingFile() {
        // Execute the docker-compose down command
        logger.trace("Executing docker-compose down with file: {}", dockerComposeFile.absolutePath)
        executeDockerComposeDown(dockerComposeFile)
    }

    @Test
    @DisplayName("Test Docker Compose Down with Non-Existent File")
    @Timeout(5 * 60)
    fun testDockerComposeDownMethodWithNonExistentFile() {
        // Create a non-existent file
        val nonExistentFile = File("non-existent-compose.yml")
        logger.trace("Testing docker-compose down with non-existent file: {}", nonExistentFile.absolutePath)
        try {
            executeDockerComposeDown(nonExistentFile)
            // If the command executes, the test should fail
            assertTrue(false, "Expected an exception for non-existent file, but command executed successfully.")
        } catch (e: IllegalStateException) {
            // Check if the exception message is as expected
            logger.trace("Caught expected exception: {}", e.message)
            assertTrue(
                e.message?.contains("File not found") == true,
                "Expected 'File not found' exception, but got: ${e.message}"
            )
        } catch (e: Exception) {
            // If any other exception is thrown, the test should fail
            logger.error("Unexpected exception: {}", e.message)
            assertTrue(false, "Unexpected exception thrown: ${e.message}")
        }
    }

    @Test
    @DisplayName("Test Docker Compose Command Execution")
    @Timeout(5 * 60)
    fun executeDockerComposeCmd() {
        // Execute a docker-compose command with the file
        logger.trace("Executing docker-compose custom command 'ps' with file: {}", dockerComposeFile.absolutePath)
        executeDockerComposeCmd(dockerComposeFile, "ps")  // Example command to list services

        // The command should execute without throwing an exception
        assertTrue(true, "Docker Compose command executed successfully.")
    }

    @Test
    @DisplayName("Test Docker Compose Command 'up' Execution")
    @Timeout(5 * 60)
    fun executeDockerComposeCmdUp() {
        // Execute a docker-compose command with the file
        logger.trace("Executing docker-compose custom command 'up' with file: {}", dockerComposeFile.absolutePath)
        executeDockerComposeCmd(dockerComposeFile, "up", "--wait")
        // The command should execute without throwing an exception
        assertTrue(true, "Docker Compose command 'up' executed successfully.")
    }

    @Test
    @DisplayName("Test Docker Compose Command 'down' Execution")
    @Timeout(5 * 60)
    fun executeDockerComposeCmdDown() {
        // Execute a docker-compose command with the file
        logger.trace("Executing docker-compose custom command 'down' with file: {}", dockerComposeFile.absolutePath)
        executeDockerComposeCmd(dockerComposeFile, "down", "-v")
        // The command should execute without throwing an exception
        assertTrue(true, "Docker Compose command 'down' executed successfully.")
    }

    @Test
    @DisplayName("Test Docker Compose Command Execution with Non-Existent File")
    @Timeout(5 * 60)
    fun executeDockerComposeCmdWithNonExistentFile() {
        // Create a non-existent file
        val nonExistentFile = File("non-existent-compose.yml")
        logger.trace("Testing docker-compose command with non-existent file: {}", nonExistentFile.absolutePath)
        try {
            executeDockerComposeCmd(nonExistentFile, "ps")
            // If the command executes, the test should fail
            assertTrue(false, "Expected an exception for non-existent file, but command executed successfully.")
        } catch (e: IllegalStateException) {
            // Check if the exception message is as expected
            logger.trace("Caught expected exception: {}", e.message)
            assertTrue(
                e.message?.contains("File not found") == true,
                "Expected 'File not found' exception, but got: ${e.message}"
            )
        } catch (e: Exception) {
            // If any other exception is thrown, the test should fail
            logger.error("Unexpected exception: {}", e.message)
            assertTrue(false, "Unexpected exception thrown: ${e.message}")
        }
    }
}