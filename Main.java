/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Michał Lewicki
 */
public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        JFileChooser chooser = new JFileChooser();
        JFrame frame = new JFrame();
        Action details = chooser.getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);
        chooser.setDialogTitle("Wybierz plik");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.showOpenDialog(frame);

        String path = chooser.getSelectedFile().getAbsolutePath();
        String filename = chooser.getSelectedFile().getName();
        System.out.println(filename);
        System.out.println(path);
        String[] options = {"Odszyfruj", "Zaszyfruj"};
        int what = JOptionPane.showOptionDialog(null, "Wybierz, co chcesz zrobić z plikiem",
                "Określ rodzaj operacji",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (what == 1) {
            String password = JOptionPane.showInputDialog(null,"Podaj hasło, którym chcesz zaszyfrować plik:","Wpisz hasło",JOptionPane.PLAIN_MESSAGE);
            EncryptionClass.encrypt(path, password);
        }
        if (what == 0) {
            String password = JOptionPane.showInputDialog(null,"Podaj hasło, którym chcesz odszyfrować plik:","Wpisz hasło",JOptionPane.PLAIN_MESSAGE);
            EncryptionClass.decrypt(path, password);
        }
        System.exit(0);
    }
}
