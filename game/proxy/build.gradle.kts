tasks.register("clean") {
    description = "Cleans only the game subprojects (excluding common)"
    group = "build"

    // Filter subprojects: only include if under :game and not named "common"
    val gameSubs = subprojects.filter { it.path.startsWith(":game:") && !it.name.contains("common") }

    dependsOn(gameSubs.map { it.tasks.named("clean") })
}
