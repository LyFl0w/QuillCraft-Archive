# QuillCraft Core Plugin 
The core of all QuillCraft Minecraft servers

## First of all, configure your GitHub account correctly in IntelliJ
To join this project, you must have a GitHub account.
- You can link your GitHub account in IntelliJ by going to `Settings > Version Control > GitHub`
- Link your email (the same as your GitHub account) to the sender of the code in IntelliJ by writing in the terminal `git config --global user.email <email@example.com>`

## How to install it on IntelliJ
This project uses Maven 3 and its shade plugin in its snapshot (3.3.+) which uses Java 16.

- (not to do, because IntelliJ already updated it) Install [Maven 3](https://maven.apache.org/download.cgi) / [Tuto Youtube](https://youtu.be/RfCWg5ay5B0?t=221) Obligatory
- Copy the https link on the "code" button
- In IntelliJ, select `New > Project from Version Control...`
- Paste the link in the URL section 
- Don't forget to add `\QuillCraftCore` in your directory


### If you want to try your code
To compile the plugin, you must install a snapshot version of a maven plugin for the shadow (which has no update (without the snapshots) for Java 16).
You just have to do it once
- In your terminal (in IntelliJ) write this :
```
git clone https://github.com/apache/maven-shade-plugin.git
cd maven-shade-plugin
mvn clean install
```
### How to build the Plugin ?
To build the plugin, you must use the Maven interface and select `clean and install` in your module.  
You will find your builds in the "QuillCraftProjectOutPut" folder in your Workspace.
