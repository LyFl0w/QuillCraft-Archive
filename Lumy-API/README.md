# Lumy-API

The Lumy-API project serves as a critical component within the QuillCraft server ecosystem, enabling seamless updates and retrieval of text content stored in Redis. It accomplishes this through local or non-local requests combined with a specific port to connect to the Lumy server.

## Overview

- **Text Update and Retrieval:** Lumy-API plays a pivotal role in the QuillCraft server environment by facilitating the efficient updating and retrieval of text content stored in Redis.

## Configuration

To configure Lumy-API and tailor its behavior to your server's needs, follow these steps:

- **Lumy Server Connection Port:** Modify the Lumy server's connection port by editing the **config-lumy.yml** configuration file.

- **Updating Language Files:** To keep your language files in sync with the Lumy Server in Redis, make sure to update the enumerations within this Lumy-API project.

## Compile 
Run the following command to compile and install the program:
```shell
mvn clean install
```

## Usage

Lumy-API is designed to work seamlessly with QuillCraftCore, QuillCraftBungee, and other QuillCraft components. It serves as a fundamental bridge for text-related operations in your QuillCraft server ecosystem. Refer to the documentation of the respective QuillCraft components for information on how to integrate and use Lumy-API effectively.
