import org.gradle.api.Plugin
import org.gradle.api.Project

class BintrayPublishingExtension {
    String user = System.getenv("BINTRAY_USER") ?: "UNKNOWN"
    String key = System.getenv("BINTRAY_KEY") ?: "UNKNOWN"
    String userOrg = "syamantm"
    String releaseRepo = "maven"
    String snapshotRepo = "snapshots"
    String websiteUrl = "https://github.com/${userOrg}/reactor-process"
    String issueTrackerUrl = "https://github.com/${userOrg}/reactor-process/issues"
    String vcsUrl = "https://github.com/${userOrg}/reactor-process.git"
    String[] licenses = ["Apache-2.0"]
    String[] labels = ["reactor", "process", "gradle", "bintray", "nebula"]
}

class BintrayPublishing implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def extension = project.extensions.create('bintray', BintrayPublishingExtension)
        applyPlugins(project)
        setupBintray(project, extension)
    }

    private void setupBintray(Project project, BintrayPublishingExtension extension) {
        project.bintray {
            user = project.findProperty("bintray.user") ?: extension.user
            key = project.findProperty("bintray.key") ?: extension.key
            pkg {
                userOrg = extension.userOrg
                repo = repoToPublishTo(project, extension)
                websiteUrl = extension.websiteUrl
                issueTrackerUrl = extension.issueTrackerUrl
                vcsUrl = extension.vcsUrl
                licenses = extension.licenses
                labels = extension.labels
            }
        }
    }

    private static String repoToPublishTo(Project project, BintrayPublishingExtension extension) {
        if (project.findProject("release.useLastTag")) {
            return extension.releaseRepo
        } else {
            return extension.snapshotRepo
        }
    }

    private static void applyPlugins(Project project) {
        project.getPluginManager().apply("nebula.maven-publish")
        project.getPluginManager().apply("nebula.javadoc-jar")
        project.getPluginManager().apply("nebula.source-jar")
        project.getPluginManager().apply("nebula.nebula-bintray-publishing")
    }
}
