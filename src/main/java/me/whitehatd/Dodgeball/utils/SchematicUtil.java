package me.whitehatd.Dodgeball.utils;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import me.whitehatd.Dodgeball.Core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematicUtil {

    private final Core core;

    public SchematicUtil(Core core){
        this.core = core;

        if(!core.getDataFolder().exists())
            core.getDataFolder().mkdirs();
    }

    public Clipboard getArena(){

        File file = new File(core.getDataFolder(),  "arena.schem");

        if(!file.exists())
            return null;

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Clipboard getLobby(){
        File file = new File(core.getDataFolder(),  "lobby.schem");

        if(!file.exists())
            return null;

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
