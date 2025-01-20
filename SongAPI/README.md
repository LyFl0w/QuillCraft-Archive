# SongAPI

The SongAPI Plugin is a powerful tool that enables the playback of sounds in Minecraft using the .nbs format. It serves
as an essential component for enhancing the auditory experience within the QuillCraft Minecraft servers.

## Key Features

- **Sound Playback:** SongAPI supports the playback of sounds in the .nbs format, allowing for immersive audio
  experiences within your Minecraft server.

## Compilation and Integration

To integrate the SongAPI Plugin into your project, follow these steps:

1. Clone the SongAPI plugin repository using your repository access.

2. Open a terminal/command prompt in the root directory of the cloned repository.

3. Execute the following command to compile the plugin using Maven:

```shell
mvn clean install
```

4. Once the compilation is complete, you will find the compiled JAR file in the "target" directory of the repository.

To add the SongAPI Plugin to your project, include the following dependency in your pom.xml file:

```mvn
<dependencies>
    <dependency>
        <groupId>net.quillcraft</groupId>
        <artifactId>SongAPI</artifactId>
        <version>X.X.X</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
````

Ensure that you use the correct version of the plugin that matches your project's requirements.

Usage
Once integrated into your server, the SongAPI Plugin allows you to create captivating auditory experiences using the
.nbs format. Explore the possibilities of enhancing your server's ambiance and gameplay with immersive sound effects.
