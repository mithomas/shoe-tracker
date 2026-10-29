package org.shoetracker.devops

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

@QuarkusTest
class DevOpsAssetTest {

    @Test
    fun testGitHubActionsWorkflowConfiguration() {
        val workflowFile = File(".github/workflows/ci.yml")
        assertTrue(workflowFile.exists(), "ci.yml must exist")
        val content = workflowFile.readText()

        // Check triggers
        assertTrue(content.contains("branches:"), "Workflow must trigger on branches")
        assertTrue(content.contains("tags:"), "Workflow must trigger on release tags")
        assertTrue(content.contains("'v*'"), "Workflow must match v* tags")

        // Check test job
        assertTrue(content.contains("actions/checkout@v4"), "Must use actions/checkout@v4")
        assertTrue(content.contains("actions/setup-java@v4"), "Must use actions/setup-java@v4")
        assertTrue(content.contains("./gradlew check test --no-daemon"), "Must run check and test")

        // Check release job
        assertTrue(content.contains("docker/setup-qemu-action@v3"), "Must set up QEMU")
        assertTrue(content.contains("docker/setup-buildx-action@v3"), "Must set up Buildx")
        assertTrue(content.contains("docker/login-action@v3"), "Must log in to Docker Hub")
        assertTrue(content.contains("docker/build-push-action@v6"), "Must use build-push-action@v6")
        assertTrue(content.contains("linux/amd64,linux/arm64"), "Must build for amd64 and arm64")
        assertTrue(content.contains("GIT_COMMIT"), "Must pass GIT_COMMIT build arg")
    }

    @Test
    fun testDockerComposeConfiguration() {
        val composeFile = File("docker-compose.yml")
        assertTrue(composeFile.exists(), "docker-compose.yml must exist")
        val content = composeFile.readText()

        assertTrue(content.contains("services:"), "Must declare services")
        assertTrue(content.contains("shoe-tracker:"), "Must declare shoe-tracker service")
        assertTrue(content.contains("./data:/data"), "Must mount persistent data volume")
        assertTrue(content.contains("\"8080:8080\""), "Must expose port 8080")
        assertTrue(content.contains("restart: unless-stopped"), "Must use unless-stopped restart policy")
    }
}
