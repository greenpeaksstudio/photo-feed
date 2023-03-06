fun isLinuxOrMacOs(): Boolean {
    val osName = System.getProperty("os.name").toLowerCase()
    return osName.contains("linux") || osName.contains("mac os") || osName.contains("macos")
}

tasks.register<Exec>("installGitHooks") {
    description = "Installs the pre-commit git hooks from scripts/git-hooks."
    group = "git hooks"

    workingDir = rootDir
    commandLine("chmod")
    args("-R", "+x", ".git/hooks/")
    dependsOn("copyGitHooks")
    onlyIf { isLinuxOrMacOs() }

    doLast {
        println("Git hooks installed successfully.")
    }
}

tasks.register<Copy>("copyGitHooks") {
    description = "Copies the git hooks from scripts/git-hooks to the .git folder."
    group = "git hooks"

    from("$rootDir/buildscripts/git-hooks/")
    include("*.sh")
    rename("(.*).sh", "$1")

    into("$rootDir/.git/hooks")
    onlyIf { isLinuxOrMacOs() }
}
