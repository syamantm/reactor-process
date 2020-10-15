import org.gradle.api.Plugin
import org.gradle.api.Project

class BintrayPublishing implements Plugin<Project> {

    @Override
    void apply(Project project) {
        applyPlugins(project)
    }

    private static void applyPlugins(Project project) {
        project.getPluginManager().apply("nebula.maven-publish")
        project.getPluginManager().apply("nebula.javadoc-jar")
        project.getPluginManager().apply("nebula.source-jar")
        project.getPluginManager().apply("nebula.nebula-bintray-publishing")
    }
}
