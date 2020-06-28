import org.gradle.api.Plugin
import org.gradle.api.Project

class GitHubPublishingExtension {
    String owner = "syamantm"
    String repository = "reactor-process"
}

class GitHubPublishing implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def extension = project.extensions.create('github', GitHubPublishingExtension)
        applyPlugins(project)
        setupGitHubPackages(project, extension)
    }

    private void setupGitHubPackages(Project project, GitHubPublishingExtension extension) {
        project.publishing {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = project.uri("https://maven.pkg.github.com/${extension.owner}/${extension.repository}")
                    credentials {
                        username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
                        password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
                    }
                }
            }
        }
    }

    private void applyPlugins(Project project) {
        project.getPluginManager().apply("nebula.maven-publish")
        project.getPluginManager().apply("nebula.javadoc-jar")
        project.getPluginManager().apply("nebula.source-jar")
    }
}
