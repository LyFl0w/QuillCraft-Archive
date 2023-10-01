# Lumy Program

The Lumy Program plays a crucial role in the QuillCraft server ecosystem by facilitating the seamless update of text content stored in Redis. It accomplishes this through local or non-local requests combined with a specific port.

## Overview

- **Text Update Management:** Lumy Program acts as the central hub for efficiently updating text content in Redis, ensuring that players receive accurate and up-to-date information.

- **API Integration:** Lumy offers an [API](https://github.com/QuillCraftMC/Lumy-API) designed to simplify communication with the server.
  
## Configuration

To configure and fine-tune the behavior of the Lumy Program, follow these steps:

- **Lumy Server Connection Port:** You can modify the Lumy server's connection port by editing the **config-lumy.yml** configuration file.

- **Redis Server Connection:** Configure the IP address and port for connecting to the Redis server by modifying the **config-redis.yml** configuration file.

- **Text Files:** Text content is stored in YAML format files named according to the ISO assigned to the language of the text within the file. For example, English (USA) content is stored in a file named "en_us," and French content is stored in a file named "fr_fr."

- **Updating Language Files:** To update the language files in Redis, you must also update the enumerations within the Lumy-API project.

## Compilation

To compile the Lumy Program, use Maven and execute the following command:

```shell
mvn clean install
```

## Usage

The Lumy Program is an essential component for maintaining the accuracy and currency of text content within the QuillCraft server ecosystem. It ensures that players receive the correct information in their preferred language.

