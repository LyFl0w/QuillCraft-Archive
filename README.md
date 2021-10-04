# QuillCraft Core Plugin 
The core of all QuillCraft Minecraft servers

## How to install it on IntelliJ
This project uses Maven 3 and its shade plugin in its snapshot (3.3.+) which uses Java 16.

- Install [Maven 3](https://maven.apache.org/download.cgi) / [Tuto Youtube](https://youtu.be/RfCWg5ay5B0?t=221) Obligatory
- Copy the https link on the "code" button
- In IntelliJ, select `New > Project from Version Control...`
- Paste the link in the URL section 
- Don't forget to add `\QuillCraftCore` in your directory

### If you want to try your code
To compile the plugin, you must install a snapshot version of a maven plugin for the shadow (which has no update (without the snapshots) for Java 16).
- In your terminal (in IntelliJ) write this :
```
git clone https://github.com/apache/maven-shade-plugin.git
cd maven-shade-plugin
mvn clean install
```
