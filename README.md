# ![](frontend/public/images/logo_32.png) Jlectron

Jlectron is a Java desktop application framework that combines **Spring Boot**, **Angular**, and **JCEF (Java Chromium
Embedded Framework)** to create cross-platform, Electron-like applications using Java and web technologies. This repository
contains a multi-module Maven project with frontend, backend, and packaging modules.

---

## Table of Contents

- [Project Structure](#project-structure)
- [Modules](#modules)
    - [Frontend](#frontend)
    - [App](#app)
    - [Bundle](#bundle)
- [Build and Packaging](#build-and-packaging)
- [Running the Application](#running-the-application)
- [Configuration Notes](#configuration-notes)
- [Developer Notes](#developer-notes)

---

## Project Structure

```
jlectron/
├── frontend/      # Angular frontend
├── app/           # Spring Boot + JCEF backend
├── bundle/        # Packaging module with jpackage and JRE
├── pom.xml        # Parent POM
````

- The project uses **Maven multi-module** layout.
- Java 21 is used for compilation and runtime.
- The `bundle` module creates a native installer using `jpackage` and bundles a JRE and JCEF.

---

## Modules

### Frontend

- Path: `frontend/`
- Purpose: Builds the Angular frontend application.
- Maven Plugin: `frontend-maven-plugin`
- Steps:
    1. Installs Node.js and npm locally.
    2. Runs `npm install` to install dependencies.
    3. Builds Angular app with `ng build`. Output is placed in `dist/frontend/browser`.

### App

- Path: `app/`
- Purpose: The Java backend that embeds the frontend and JCEF for rendering web content.
- Dependencies:
    - Spring Boot (Web, Thymeleaf, DevTools, Configuration Processor)
    - JCEF Maven wrapper (`jcefmaven`)
- Build:
    - Uses `maven-resources-plugin` to copy Angular `dist` folder to `target/classes/static`.
    - Spring Boot Maven Plugin creates a **fat JAR** (`app.jar`) including backend code and Angular frontend static
      files.
- Notes:
    - Angular build must exist before packaging `app.jar`.
    - JCEF native libraries are loaded at runtime via `-Djava.library.path`.

### Bundle

- Path: `bundle/`
- Purpose: Creates a native application package with JRE and JCEF.
- Steps:
    1. Copies `app.jar` from `app/target`.
    2. Copies JCEF folder into the bundle (`app/jcef`).
    3. Downloads and unpacks a JRE (Temurin 21) to bundle runtime.
    4. Uses `jpackage-maven-plugin` to create a **native launcher** (Windows `.exe`) with proper JVM options:
        - `--enable-preview`
        - `-Dfile.encoding=UTF-8`
        - `-Djava.library.path=app/jcef/bin/lib/win64`

---

## Build and Packaging

### From Command Line

```bash
# Clean and build everything
mvn clean package
````

This will execute modules in the following order:

1. `frontend` - builds Angular app
2. `app` - packages backend and copies frontend static files
3. `bundle` - packages native app with JRE and JCEF

### Output

* `app/target/app.jar` – Spring Boot fat JAR with embedded Angular frontend.
* `bundle/target/dist/` – Native application image with bundled JRE and JCEF.

---

## Running the Application

### From Fat JAR

```bash
java -Djava.library.path=path/to/jcef/bin/lib/win64 -jar app/target/app.jar
```

### From Native Installer

* Navigate to `bundle/target/dist/` and run `Jlectron.exe`.
* JCEF native libraries and JRE are bundled, no external dependencies required.

---

## Configuration Notes

* **Angular build output folder:** Configured in `app/pom.xml` as `frontend.build.dir`.
* **JCEF path:** Configured in `bundle/pom.xml` as `jcef.dir`.
* **JRE download URL:** Configured in `bundle/pom.xml` as `jre.download.link.windows`.
* **Static files in JAR:** `maven-resources-plugin` copies frontend output into `target/classes/static` before Spring
  Boot packaging.
* **JCEF library path:** Must match your system architecture (Windows x64 in this configuration).

---

## Developer Notes

* Angular changes require rebuilding the frontend (`mvn -pl frontend clean package` or `ng build` manually).
* App module depends on frontend module; ensure frontend is built before packaging.
* Native packaging bundles its own JRE; Java installation on the target machine is not required.
* JVM options for enabling JCEF and encoding are set in `bundle/pom.xml` via `jpackage-maven-plugin`.
* For debugging JCEF issues, run the fat JAR with `-Djava.library.path` pointing to the JCEF folder.

---

## References

* [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)
* [JCEF Maven Wrapper](https://github.com/friwi/jcefmaven)
* [Frontend Maven Plugin](https://github.com/eirslett/frontend-maven-plugin)
* [JPackage Maven Plugin](https://github.com/panteleyev/jpackage-maven-plugin)

---

## Summary

This project combines:

* **Frontend:** Angular application for UI.
* **Backend:** Spring Boot + JCEF to render frontend inside a desktop app.
* **Packaging:** Native Windows application with embedded JRE and Chromium engine.
* Multi-module Maven structure ensures build order and proper packaging of static resources and native libraries.
