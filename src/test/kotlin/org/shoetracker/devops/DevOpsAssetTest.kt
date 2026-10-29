package org.shoetracker.devops

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertEquals
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

    @Test
    fun testDependabotConfiguration() {
        val dependabotFile = File(".github/dependabot.yml")
        assertTrue(dependabotFile.exists(), "dependabot.yml must exist")
        val lines = dependabotFile.readLines()
        val content = dependabotFile.readText()

        // Structural and indentation validation (YAML compliance)
        assertTrue(content.contains("version: 2"), "Must be Dependabot version 2")
        assertTrue(lines.none { it.contains("\t") }, "YAML must not contain tab characters")
        assertTrue(lines.all { line ->
            val trimmed = line.trimStart()
            trimmed.isEmpty() || trimmed.startsWith("#") || (line.length - trimmed.length) % 2 == 0
        }, "YAML must follow 2-space indentation")

        // Split into ecosystem update blocks
        val blocks = content.split("- package-ecosystem:").drop(1)
        assertEquals(3, blocks.size, "Must configure exactly 3 package ecosystems")

        // Validate gradle ecosystem block
        val gradleBlock = blocks.find { it.trimStart().startsWith("\"gradle\"") }
        assertTrue(gradleBlock != null, "Gradle ecosystem block must exist")
        assertTrue(gradleBlock!!.contains("interval: \"weekly\""), "Gradle must have weekly schedule")
        assertTrue(gradleBlock.contains("prefix: \"chore(deps)\""), "Gradle must have chore(deps) prefix")
        assertTrue(gradleBlock.contains("quarkus:"), "Gradle must define quarkus group")
        assertTrue(gradleBlock.contains("io.quarkus*"), "Quarkus group must match io.quarkus*")
        assertTrue(gradleBlock.contains("minor-and-patch:"), "Gradle must define minor-and-patch group")
        assertTrue(gradleBlock.contains("version-update:semver-minor"), "Must use semver-minor update-type")
        assertTrue(gradleBlock.contains("version-update:semver-patch"), "Must use semver-patch update-type")

        // Validate docker ecosystem block
        val dockerBlock = blocks.find { it.trimStart().startsWith("\"docker\"") }
        assertTrue(dockerBlock != null, "Docker ecosystem block must exist")
        assertTrue(dockerBlock!!.contains("interval: \"weekly\""), "Docker must have weekly schedule")
        assertTrue(dockerBlock.contains("prefix: \"chore(docker)\""), "Docker must have chore(docker) prefix")

        // Validate github-actions ecosystem block
        val actionsBlock = blocks.find { it.trimStart().startsWith("\"github-actions\"") }
        assertTrue(actionsBlock != null, "GitHub Actions ecosystem block must exist")
        assertTrue(actionsBlock!!.contains("interval: \"weekly\""), "Actions must have weekly schedule")
        assertTrue(actionsBlock.contains("prefix: \"chore(ci)\""), "Actions must have chore(ci) prefix")
        assertTrue(actionsBlock.contains("actions:"), "Actions must define grouping")
    }
}
