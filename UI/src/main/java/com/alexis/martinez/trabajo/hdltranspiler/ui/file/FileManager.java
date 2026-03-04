/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.alexis.martinez.trabajo.hdltranspiler.ui.file;

import com.alexis.martinez.trabajo.hdltranspiler.ui.TranspilerUI;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexis Martinez
 */
public class FileManager {

    private File actual_file;
    private InputState is;

    public FileManager() {
        this.reset();
    }

    public void setActualFile(File actual_file) {
        this.actual_file = actual_file;
        this.is = new FileExists();
    }

    public boolean save(String text) {
        if (is instanceof None) {
            return false;
        }
        try {
            FileWriter fw = new FileWriter(actual_file.getAbsolutePath());
            fw.write(text);
            fw.close();

            is = is.new_file();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(TranspilerUI.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String getName() {
        return this.actual_file.getName();
    }

    public void reset() {
        this.actual_file = null;
        this.is = new None();

    }

    public String open_file(File file) throws IOException {
        setActualFile(file);
        return Files.readString(actual_file.toPath());
    }

    public String open_file() throws IOException {
        return Files.readString(actual_file.toPath());
    }

    public void create_new_file(File selectedFile) {
        setActualFile(selectedFile);
    }
}
