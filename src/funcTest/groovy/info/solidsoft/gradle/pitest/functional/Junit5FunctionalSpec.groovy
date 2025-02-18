package info.solidsoft.gradle.pitest.functional

import groovy.transform.CompileDynamic
import nebula.test.functional.ExecutionResult
import spock.lang.Issue

@CompileDynamic
class Junit5FunctionalSpec extends AbstractPitestFunctionalSpec {

    void "should work with kotlin and junit5"() {
        given:
            copyResources("testProjects/junit5kotlin", "")
        when:
            ExecutionResult result = runTasks('pitest')
        then:
            !result.standardError.contains("Build failed with an exception")
            !result.failure
            result.wasExecuted('pitest')
            result.standardOutput.contains('Generated 2 mutations Killed 2 (100%)')
    }

    void "should work with kotlin and junit5 in build.gradle.kts"() {
        given:
            copyResources("testProjects/junit5kotlin", "")
        when:
            ExecutionResult result = runTasks('pitest', '-b', 'build.gradle.kts')
        then:
            !result.standardError.contains("Build failed with an exception")
            !result.failure
            result.wasExecuted('pitest')
            result.standardOutput.contains('Generated 2 mutations Killed 2 (100%)')
    }

    @Issue(["https://github.com/szpak/gradle-pitest-plugin/issues/177", "https://github.com/szpak/gradle-pitest-plugin/issues/300"])
    void "should work with junit5 without explicitly adding dependency (#description)"() {
        given:
            copyResources("testProjects/junit5simple", "")
        when:
            ExecutionResult result = runTasks('pitest', '-b', buildFileName)
        then:
            !result.standardError.contains("Build failed with an exception")
            !result.failure
            result.wasExecuted('pitest')
        and:
            result.standardOutput.contains('Generated 1 mutations Killed 1 (100%)')
        and:
            result.standardOutput.contains("pitest-junit5-plugin-${expectedJunitPluginVersion}.jar")
            result.standardOutput.contains("junit-jupiter-api-${expectedJUnitJupiterVersion}.jar")
            result.standardOutput.contains("junit-platform-commons-${expectedJUnitPlatformVersion}.jar")
        where:
            buildFileName                             || expectedJunitPluginVersion | expectedJUnitJupiterVersion | expectedJUnitPlatformVersion
            'build.gradle'                            || "1.0.0"                    | "5.8.0"                     | "1.8.0"
            'build-pit-1.8-junit-platform-5.7.gradle' || "0.14"                     | "5.7.0"                     | "1.7.0"

            description = "plugin $expectedJunitPluginVersion, junit $expectedJUnitJupiterVersion, platform $expectedJUnitPlatformVersion"
    }

    void "should work with Spock 2 using JUnit 5 internally"() {
        given:
            copyResources("testProjects/junit5spock2", "")
        when:
            ExecutionResult result = runTasks('pitest')
        then:
            !result.standardError.contains("Build failed with an exception")
            !result.failure
            result.wasExecuted('pitest')
        and:
            result.standardOutput.contains('Generated 1 mutations Killed 1 (100%)')
    }

    @Issue("https://github.com/szpak/gradle-pitest-plugin/issues/249")
    void "should store and reuse Gradle configuration cache"() {
        given:
            copyResources("testProjects/junit5simple", "")
        when:
            ExecutionResult result = runTasks('pitest', '--configuration-cache')
            ExecutionResult result2 = runTasks('pitest', '--configuration-cache')
        then:
            !result.failure
            result.wasExecuted('pitest')
            !result2.failure
            result2.wasExecuted('pitest')
        and:
            result.standardOutput.contains('Configuration cache entry stored')
            result2.standardOutput.contains('Reusing configuration cache.')
    }

}
