## What is Lumy ?
Project Lumy allows the QuillCraft server to easily update texts stored in Redis via local or non-local requests combined with a specific port. 

## How to use it ?
Lumy has an API to facilitate communication with the server. [![Build Status](https://img.shields.io/badge/API-passing-green)](https://github.com/LyFl0w/Lumy-API)

## How to configure it ?
- The Lumy server connection port can be modified in the **config-lumy.yml** configuration file.
- The ip and port of connection to the Redis server can be modified in the **config-redis.yml** configuration file.
- The text files are in YAML format and are named according to the iso assigned to the language of the text contained in the file *(example: English (USA) -> en_us, French -> fr_fr)*. 
- To update the language files in Redis, you must also update the enumerations of the Lumy-API project
