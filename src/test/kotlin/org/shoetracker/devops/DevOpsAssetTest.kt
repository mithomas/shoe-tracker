package org.shoetracker.devops

import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

@QuarkusTest
class DevOpsAssetTest {
    @Test
    fun testGitHubActionsWorkflowConfiguration() {
        val workflowFile = File(".github/workflows/ci.yml")
        assertThat(workflowFile).exists().isFile()
        val content = workflowFile.readText()

        // Check triggers
        assertThat(content)
            .contains("branches:")
            .contains("tags:")
            .contains("'v*'")

        // Check test job
        assertThat(content)
            .contains("actions/checkout@v4")
            .contains("actions/setup-java@v4")
            .contains("./gradlew spotlessCheck --no-daemon")
            .contains("./gradlew lintJs --no-daemon")
            .contains("./gradlew check test --no-daemon")
            .contains("docker build --build-arg GIT_COMMIT")

        // Check release job
        assertThat(content)
            .contains("docker/setup-qemu-action@v3")
            .contains("docker/setup-buildx-action@v3")
            .contains("docker/login-action@v3")
            .contains("docker/build-push-action@v6")
            .contains("linux/amd64,linux/arm64")
            .contains("GIT_COMMIT")
    }

    @Test
    fun testDockerComposeConfiguration() {
        val composeFile = File("docker-compose.yml")
        assertThat(composeFile).exists().isFile()
        val content = composeFile.readText()

        assertThat(content)
            .contains("services:")
            .contains("shoe-tracker:")
            .contains("./data:/data")
            .contains("\"8080:8080\"")
            .contains("restart: unless-stopped")
    }

    @Test
    fun testDependabotConfiguration() {
        val dependabotFile = File(".github/dependabot.yml")
        assertThat(dependabotFile).exists().isFile()
        val lines = dependabotFile.readLines()
        val content = dependabotFile.readText()

        // Structural and indentation validation (YAML compliance)
        assertThat(content).contains("version: 2")
        assertThat(lines).noneMatch { it.contains("\t") }
        assertThat(lines).allMatch { line ->
            val trimmed = line.trimStart()
            trimmed.isEmpty() || trimmed.startsWith("#") || (line.length - trimmed.length) % 2 == 0
        }

        // Split into ecosystem update blocks
        val blocks = content.split("- package-ecosystem:").drop(1)
        assertThat(blocks).hasSize(3)

        // Validate gradle ecosystem block
        val gradleBlock = blocks.find { it.trimStart().startsWith("\"gradle\"") }
        assertThat(gradleBlock)
            .isNotNull()
            .contains("interval: \"weekly\"")
            .contains("prefix: \"chore(deps)\"")
            .contains("quarkus:")
            .contains("io.quarkus*")
            .contains("minor-and-patch:")
            .contains("version-update:semver-minor")
            .contains("version-update:semver-patch")

        // Validate docker ecosystem block
        val dockerBlock = blocks.find { it.trimStart().startsWith("\"docker\"") }
        assertThat(dockerBlock)
            .isNotNull()
            .contains("interval: \"weekly\"")
            .contains("prefix: \"chore(docker)\"")

        // Validate github-actions ecosystem block
        val actionsBlock = blocks.find { it.trimStart().startsWith("\"github-actions\"") }
        assertThat(actionsBlock)
            .isNotNull()
            .contains("interval: \"weekly\"")
            .contains("prefix: \"chore(ci)\"")
            .contains("actions:")
    }

    @Test
    fun testSpotlessAndLintConfiguration() {
        val editorConfig = File(".editorconfig")
        assertThat(editorConfig).exists().isFile()
        assertThat(editorConfig.readText()).contains("max_line_length = 120")

        val gitAttributes = File(".gitattributes")
        assertThat(gitAttributes).exists().isFile()
        val gitAttributesText = gitAttributes.readText()
        assertThat(gitAttributesText)
            .contains("eol=lf")
            .contains("*.bat text eol=crlf")
            .contains("*.png binary")

        val dockerfile = File("Dockerfile")
        assertThat(dockerfile).exists().isFile()
        assertThat(dockerfile.readText()).contains("COPY .editorconfig gradlew")

        val buildFile = File("build.gradle.kts")
        assertThat(buildFile).exists().isFile()
        val content = buildFile.readText()

        assertThat(content)
            .contains("id(\"com.diffplug.spotless\")")
            .contains("spotless {")
            .contains("ktlint()")
            .contains("\"max_line_length\" to \"120\"")
            .contains("format(\"web\")")
            .contains("leadingTabsToSpaces(2)")
            .contains("endWithNewline()")
            .contains("val lintJs by tasks.registering")
            .contains("com.google.javascript:closure-compiler")
            .contains("tasks.named(\"check\")")
    }
}
