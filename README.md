# Photo Feed

[![Multiplatform CI](https://github.com/greenpeaksstudio/photo-feed/actions/workflows/multiplatform_ci.yml/badge.svg)](https://github.com/greenpeaksstudio/photo-feed/actions/workflows/multiplatform_ci.yml)

## App Architecture

<p align="center">
    <img src="https://raw.githubusercontent.com/greenpeaksstudio/photo-feed/main/docs/images/architecture.png" />
</p>

## Coding Style

### Kotlin Coding conventions
Our code style guidelines are based on the [Kotlin Coding conventions](https://kotlinlang.org/docs/coding-conventions.html).

#### Spotless and detekt

All code needs to comply with the [Spotless](https://github.com/diffplug/spotless) checks before being merged. You can check this locally using `./gradlew spotlessCheck`, or auto-format your code with `./gradlew spotlessApply`.

Also, the code needs to comply with static code analyzer enforced by [detekt](https://detekt.dev). You can pass this analysis locally running `./gradlew detekt`

The project has a Git hook you can install to run the spotless check on pre-commit and both spotless and detekt on pre-push: `./gradlew installGitHooks`.

## License
```
Copyright 2023 Green Peaks Studio

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```