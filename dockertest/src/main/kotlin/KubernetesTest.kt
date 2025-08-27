package mf.vil.dockertest

import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.collections.isEmpty

/**
 * Abstract class to be extended by tests that need to deploy Kubernetes resources.
 */
abstract class KubernetesTest {
    private val logger = LogManager.getLogger(this::class)

    /**
     * Apply all YAML files in the specified directory using kubectl apply
     */
    fun executeKubectlApply(k8sYmlfilesPath: File) {
        logger.trace("Applying Kubernetes resources from path: {}", k8sYmlfilesPath.absolutePath)
        validateK8sDirectory(k8sYmlfilesPath)
        executeKubectlCmd(k8sYmlfilesPath.parentFile ?: File("."), "apply", "-f", k8sYmlfilesPath.absolutePath)
        logger.trace("Kubernetes resources applied successfully.")
    }

    /**
     * Apply all YAML files in the specified directory using kubectl apply with wait option
     */
    fun executeKubectlApplyAndWait(k8sYmlfilesPath: File, timeout: String = "300s") {
        logger.trace("Applying Kubernetes resources from path: {} with wait timeout: {}", k8sYmlfilesPath.absolutePath, timeout)
        validateK8sDirectory(k8sYmlfilesPath)
        executeKubectlCmd(k8sYmlfilesPath.parentFile ?: File("."), "apply", "-f", k8sYmlfilesPath.absolutePath, "--wait", "--timeout=$timeout")
        logger.trace("Kubernetes resources applied and ready successfully.")
    }

    /**
     * Delete all YAML files in the specified directory using kubectl delete
     */
    fun executeKubectlDelete(k8sYmlfilesPath: File) {
        logger.trace("Deleting Kubernetes resources from path: {}", k8sYmlfilesPath.absolutePath)
        validateK8sDirectory(k8sYmlfilesPath)
        executeKubectlCmd(k8sYmlfilesPath.parentFile ?: File("."), "delete", "-f", k8sYmlfilesPath.absolutePath, "--ignore-not-found=true")
        logger.trace("Kubernetes resources deleted successfully.")
    }

    /**
     * Validate that the directory exists and contains YAML files
     */
    private fun validateK8sDirectory(k8sDirectory: File) {
        if (!k8sDirectory.exists()) {
            throw IllegalStateException("K8s directory not found: ${k8sDirectory.absolutePath}")
        }

        if (!k8sDirectory.isDirectory) {
            throw IllegalStateException("K8s path is not a directory: ${k8sDirectory.absolutePath}")
        }

        val yamlFiles = k8sDirectory.listFiles { _, name ->
            name.endsWith(".yml", ignoreCase = true) ||
                    name.endsWith(".yaml", ignoreCase = true) ||
                    name.endsWith(".json", ignoreCase = true)
        }

        if (yamlFiles == null || yamlFiles.isEmpty()) {
            throw IllegalStateException("No YAML/JSON files found in directory: ${k8sDirectory.absolutePath}")
        }

        logger.trace("Found {} valid K8s files in directory: {}", yamlFiles.size, k8sDirectory.absolutePath)
    }

    /**
     * Apply a specific YAML file
     */
    fun executeKubectlApplyFile(yamlFile: File) {
        logger.trace("Applying Kubernetes resource file: {}", yamlFile.absolutePath)
        executeKubectlCmd(yamlFile.parentFile, "apply", "-f", yamlFile.absolutePath)
        logger.trace("Kubernetes resource file applied successfully.")
    }

    /**
     * Delete a specific YAML file
     */
    fun executeKubectlDeleteFile(yamlFile: File) {
        logger.trace("Deleting Kubernetes resource file: {}", yamlFile.absolutePath)
        executeKubectlCmd(yamlFile.parentFile, "delete", "-f", yamlFile.absolutePath, "--ignore-not-found=true")
        logger.trace("Kubernetes resource file deleted successfully.")
    }

    /**
     * Wait for deployments to be ready in a specific namespace
     */
    fun waitForDeployments(namespace: String = "default", timeout: String = "300s") {
        logger.trace("Waiting for deployments to be ready in namespace: {} with timeout: {}", namespace, timeout)
        executeKubectlCmd(File("."), "wait", "--for=condition=available", "--timeout=$timeout", "deployment", "--all", "-n", namespace)
        logger.trace("All deployments are ready in namespace: {}", namespace)
    }

    /**
     * Wait for pods to be ready in a specific namespace
     */
    fun waitForPods(namespace: String = "default", timeout: String = "300s") {
        logger.trace("Waiting for pods to be ready in namespace: {} with timeout: {}", namespace, timeout)
        executeKubectlCmd(File("."), "wait", "--for=condition=ready", "--timeout=$timeout", "pod", "--all", "-n", namespace)
        logger.trace("All pods are ready in namespace: {}", namespace)
    }

    /**
     * Execute kubectl command with the specified arguments
     */
    fun executeKubectlCmd(workingDirectory: File, vararg arguments: String) {
        logger.trace("Checking working directory: '{}'", workingDirectory.absolutePath)

        if (!workingDirectory.exists()) {
            logger.error("Working directory not found: {}", workingDirectory.absolutePath)
            throw IllegalStateException("Working directory not found: ${workingDirectory.absolutePath}")
        }

        logger.trace("Working directory found, executing kubectl command")
        val command = mutableListOf("kubectl")
        command.addAll(arguments)
        logger.trace("Executing commands: {}", command.joinToString(" "))

        val processBuilder = ProcessBuilder()
            .command(command)
            .redirectErrorStream(true)
            .directory(workingDirectory)

        logger.trace("Starting process with command: {}", command.joinToString(" "))
        val process = processBuilder.start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        logger.trace("Process output: {}", output)

        logger.trace("Waiting for process to finish...")
        val exitCode = process.waitFor()
        logger.trace("Process exited with code: {}", exitCode)

        if (exitCode != 0) {
            logger.error("Error when executing kubectl command: $output")
            throw RuntimeException("Failed to execute kubectl command: Exit code $exitCode, output: $output")
        }
    }

    /**
     * Check if kubectl is available and cluster is accessible
     */
    fun checkKubectlAvailability() {
        logger.trace("Checking kubectl availability and cluster connection")
        try {
            executeKubectlCmd(File("."), "version", "--client")
            executeKubectlCmd(File("."), "cluster-info")
            logger.trace("kubectl is available and cluster is accessible")
        } catch (e: Exception) {
            logger.error("kubectl is not available or cluster is not accessible: {}", e.message)
            throw RuntimeException("kubectl is not available or cluster is not accessible", e)
        }
    }

    /**
     * Get the status of resources in a namespace
     */
    fun getResourceStatus(namespace: String = "default"): String {
        logger.trace("Getting resource status for namespace: {}", namespace)
        val command = mutableListOf("kubectl", "get", "all", "-n", namespace)

        val processBuilder = ProcessBuilder()
            .command(command)
            .redirectErrorStream(true)
            .directory(File("."))

        val process = processBuilder.start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            logger.warn("Could not get resource status: {}", output)
            return "Failed to get resource status: $output"
        }

        logger.trace("Resource status retrieved successfully")
        return output
    }
}