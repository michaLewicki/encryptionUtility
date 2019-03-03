/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Michał Lewicki
 */
public class ZipClass {

    private List<String> fileList = new ArrayList<>();
    private static String source_path = "";

    public ZipClass(String source) {
        if (new File(source).isDirectory()) {
            source_path = source;
        }
    }

    public void getFileList(File f) {
        if (f.isFile()) {
            fileList.add(generateZipEntry(f.toString()));
        }
        if (f.isDirectory()) {
            String[] subFiles = f.list();
            for (String filename : subFiles) {
                getFileList(new File(f, filename));
            }
        }
    }

    private String generateZipEntry(String s) {
        if (!"".equals(source_path)) {
            return s.substring(source_path.length() + 1, s.length());
        } else {
            return s.substring(s.lastIndexOf('\\') + 1, s.length());
        }
    }

    public byte[] zip(String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = null;
        File source = new File(source_path);
        String name = source.getName();
        try {
            zos = new ZipOutputStream(baos);
            zos.setLevel(Deflater.DEFAULT_COMPRESSION);
            for (String file : this.fileList) {
                if (source.isDirectory()) {
                    ZipEntry ze = new ZipEntry(name + File.separator + file);
                    zos.putNextEntry(ze);
                    zos.write(Files.readAllBytes(new File(source_path + File.separator + file).toPath()));
                    zos.closeEntry();
                } else {
                    ZipEntry ze = new ZipEntry(file);
                    zos.putNextEntry(ze);
                    zos.write(Files.readAllBytes(new File(path).toPath()));
                    zos.closeEntry();
                }
            }
            zos.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return baos.toByteArray();
    }

    public static void unzip(byte[] zipIn, String destDir) throws IOException {
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipIn));
        String filepath = "";
        byte[] buffer = new byte[1024];
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            filepath = destDir + File.separator + ze.getName();
            if (Files.notExists(new File(filepath).toPath())) {
                if (!ze.isDirectory()) {
                    new File(filepath).getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(filepath)) {
                        int len = 0;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                } else {
                    File dir = new File(filepath);
                    dir.mkdirs();
                }
                ze = zis.getNextEntry();
            }
            else{
                JPanel j = new JPanel();
                JOptionPane.showMessageDialog(j, "W podanej lokalizacji istnieje plik o takiej samej nazwie.\nWybierz inną lokalizację.", "Błąd", JOptionPane.WARNING_MESSAGE);
                break;
            }
        }
        zis.closeEntry();
        zis.close();
    }

}
