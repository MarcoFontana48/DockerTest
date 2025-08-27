package ausl.cce.endtoend

import mf.vil.dockertest.KubernetesTest
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class KubernetesTestTest : KubernetesTest() {
    private val logger = LogManager.getLogger(this::class)
    private val k8sYmlfilesPath = "src/test/resources/k8s/"
    private val k8sNamespace = "monitoring-app"
    private lateinit var k8sDirectory: File

    @BeforeEach
    fun setUp() {
        k8sDirectory = File(k8sYmlfilesPath)

        logger.info("Starting Kubernetes resources setup...")
        logger.info("K8s directory path: {}", k8sDirectory.absolutePath)

        // check kubectl is available and cluster is accessible
        checkKubectlAvailability()

        // apply all YAML files and wait for resources to be ready
        executeKubectlApplyAndWait(k8sDirectory)

        // wait for deployments and pods to be ready (adjust namespace if needed)
        waitForDeployments(k8sNamespace, "600s")
        waitForPods(k8sNamespace, "600s")

        logger.info("Kubernetes resources are ready for testing")
    }

    @AfterEach
    fun tearDown() {
        logger.info("Cleaning up Kubernetes resources...")
        executeKubectlDelete(k8sDirectory)
        logger.info("Kubernetes resources cleaned up")
    }

    @Test
    fun testServiceIsRunning() {
        logger.info("Service is running. Implement tests here.")

        // optional: Log current resource status for debugging
        val status = getResourceStatus(k8sNamespace)
        logger.debug("Current resource status:\n{}", status)

        // Your implementation goes here
    }
}