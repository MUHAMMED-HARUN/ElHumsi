package ui.formlar;

import ui.paneller.BisikletPaneli;
import ui.paneller.IstasyonPaneli;
import ui.paneller.KiralamaPaneli;
import ui.paneller.KullaniciPaneli;
import ui.paneller.SorgulamaPaneli;

import javax.swing.*;

public class AnaEkranFormu extends JFrame {
    public AnaEkranFormu() {
        super("VY-016 | Bisiklet Kiralama ve İstasyon Yönetim Sistemi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("İstasyonlar", new IstasyonPaneli());
        tabs.addTab("Kullanıcılar", new KullaniciPaneli());
        tabs.addTab("Bisikletler", new BisikletPaneli());
        tabs.addTab("Kiralama", new KiralamaPaneli());
        tabs.addTab("Sorgulama", new SorgulamaPaneli());

        add(tabs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AnaEkranFormu form = new AnaEkranFormu();
                form.setVisible(true);
            }
        });
    }
}