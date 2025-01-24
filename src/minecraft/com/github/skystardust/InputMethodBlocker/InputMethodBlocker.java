package com.github.skystardust.InputMethodBlocker;

import cn.stars.reversal.util.ReversalLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputMethodBlocker {
    public static void init(){
        try {
            saveNativeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NativeUtils.inactiveInputMethod("");
    }

    public static void saveNativeFile() throws IOException {
        OSChecker.OSType osType = OSChecker.getOsType();
        if (osType== OSChecker.OSType.WIN_X64){
            saveTempNativeFile("InputMethodBlocker-Natives-x64.dll");
        }
        else if (osType== OSChecker.OSType.WIN_X32){
            saveTempNativeFile("InputMethodBlocker-Natives-x86.dll");
        }
    }
    private static void saveTempNativeFile(String fileName) throws IOException {
        InputStream fileInputStream = InputMethodBlocker.class.getResource("/assets/minecraft/reversal/" + fileName).openStream();
        File nativeFile = File.createTempFile("InputMethodBlocker", ".dll");
        FileOutputStream out = new FileOutputStream(nativeFile);
        int i;
        byte [] buf = new byte[1024];
        while((i=fileInputStream.read(buf))!=-1) {
            out.write(buf,0,i);
        }
        fileInputStream.close();
        out.close();
        nativeFile.deleteOnExit();
        System.load(nativeFile.toString());
        ReversalLogger.warn("[InputMethodBlocker] Loaded native file: " + fileName);
    }
}
