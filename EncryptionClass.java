/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author Michał Lewicki
 */
public class EncryptionClass {
    
    public static void encrypt(String path, String password) {
        byte[] passwordArray = password.getBytes(Charset.forName("UTF-8"));
        byte[] fileArray = null;
        ZipClass zc = new ZipClass(path);
        zc.getFileList(new File(path));
        fileArray = zc.zip(path);
        try (FileOutputStream fos = new FileOutputStream(path + ".lew")) {
            fos.write(XOR(XOR(XOR(fileArray, SHA256(addSalt(password))), Base64.getEncoder().encode(passwordArray)), myOwnHash(password)));
            fos.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void decrypt(String path, String password) throws NoSuchAlgorithmException, IOException {
        byte[] passwordArray = password.getBytes(Charset.forName("UTF-8"));
        byte[] fileArray = null;
        try {
            fileArray = Files.readAllBytes(new File(path).toPath());
        } catch (IOException e) {
            System.out.println(e);
        }
        
        JFileChooser chooser = new JFileChooser();
        JFrame frame = new JFrame();
        Action details = chooser.getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Wybierz, gdzie zapisać odszyfrowany plik");
        chooser.showOpenDialog(frame);
        String directory = chooser.getSelectedFile().getAbsolutePath();
        ZipClass.unzip(XOR(XOR(XOR(fileArray, myOwnHash(password)), Base64.getEncoder().encode(passwordArray)),SHA256(addSalt(password))), directory);
            
    }

    private static byte[] XOR(byte[] file, byte[] password) {
        byte[] outputArray = new byte[file.length];
        int i = 0, j = 0;
        while (j < file.length) {
            for (j = i; (j - i) < password.length; j++) {
                if (j >= file.length) {
                    break;
                }
                outputArray[j] = (byte) (file[j] ^ password[j - i]);
            }
            i += password.length;
        }
        return outputArray;
    }
    
    private static byte[] SHA256(String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(s.getBytes(Charset.forName("UTF-8")));
        return hash;
    }
    
    private static String addSalt(String s) {
        char[] unsalted = s.toCharArray();
        char[] temp = new char[2*unsalted.length];
        for(int i = 0; i < temp.length; i++){
            if(i % 2 == 0) temp[i] = unsalted[i/2];
            else temp[i] = '@';
        }
        String salty = String.valueOf(temp);
        return salty + "Much_m0re|S@lt";
    }
    
    private static byte[] myOwnHash(String s){
        byte[] temp = s.getBytes(Charset.forName("UTF-8"));
        double val = 0, multiplier;
        int current;
        for(int i = 0; i < temp.length; i++){
            current = Math.abs(new Byte(temp[i]).intValue());
            multiplier = ((double)((int)((current*Math.pow(10, -1*Integer.toString(current).length()) + 1) *1000.0)))/1000.0;
            switch(current % 3){
                case 0: 
                    val += current*multiplier;
                    break;
                case 1: 
                    val += current*multiplier;
                    break;
                case 2: 
                    val -= current*multiplier;
                    break;
            }
        }
        return String.valueOf(val*3.14159265359/3+1.57244168031).getBytes(Charset.forName("UTF-8"));
    }
}
