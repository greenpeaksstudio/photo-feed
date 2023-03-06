# Photo Feed

[![Multiplatform CI](https://github.com/asturiancoder/photo-feed/actions/workflows/multiplatform_ci.yml/badge.svg)](https://github.com/asturiancoder/photo-feed/actions/workflows/multiplatform_ci.yml)

## App Architecture

<p align="center">
    <img src="https://raw.githubusercontent.com/asturiancoder/photo-feed/main/assets/architecture.png" />
</p>

## Coding Style

Our code style guidelines are based on the [Android Kotlin style guide](https://developer.android.com/kotlin/style-guide).


### Spotless

All code needs to comply with the Spotless checks before being merged. You can check this locally using `./gradlew spotlessCheck`, or auto-format your code with `./gradlew spotlessApply`.

The project has a Git hook you can install to run this check on pre-commit `./gradlew installGitHooks`.

## License
```
Copyright 2023 Asturian Coder

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