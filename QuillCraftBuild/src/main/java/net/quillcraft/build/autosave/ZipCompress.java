package net.quillcraft.build.autosave;

import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ZipCompress {

    private ZipCompress() {
        throw new IllegalStateException("Utility class");
    }


    public static void zip4j(String name, File file, Logger logger){
        //zip file with multiple files
        try(final ZipFile zipFile = new ZipFile(name+".zip")) {
            zipFile.addFolder(file);
        } catch(IOException exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

}
