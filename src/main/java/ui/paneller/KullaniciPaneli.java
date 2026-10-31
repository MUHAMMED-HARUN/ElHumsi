package ui.paneller;

import bal.servisler.KullaniciServisi;
import bal.varliklar.Kullanici;
import bal.veri_baglama.TabloBaglayici;
import ui.dogrulama.GirisDogrulama;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class KullaniciPaneli extends JPanel {
    private final KullaniciServisi servis = new KullaniciServisi();
    private JTextField txtAdSoyad;
    private JTextField txtTelefon;
    private JTable tablo;
    private DefaultTableModel model;
    private int seciliId = -1;

    public KullaniciPaneli() {
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("Ad Soyad:"));
        txtAdSoyad = new JTextField();
        form.add(txtAdSoyad);
        form.add(new JLabel("Telefon:"));
        txtTelefon = new JTextField();
        form.add(txtTelefon);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEkle = new JButton("Ekle");
        JButton btnGuncelle = new JButton("Güncelle");
        JButton btnSil = new JButton("Sil");
        JButton btnTemizle = new JButton("Temizle");
        btnPanel.add(btnEkle);
        btnPanel.add(btnGuncelle);
        btnPanel.add(btnSil);
        btnPanel.add(btnTemizle);

        JPanel ust = new JPanel(new BorderLayout());
        ust.add(form, BorderLayout.CENTER);
        ust.add(btnPanel, BorderLayout.SOUTH);
        add(ust, BorderLayout.NORTH);

        tablo = new JTable();
        add(new JScrollPane(tablo), BorderLayout.CENTER);

        btnEkle.addActionListener(e -> ekle());
        btnGuncelle.addActionListener(e -> guncelle());
        btnSil.addActionListener(e -> sil());
        btnTemizle.addActionListener(e -> temizle());

        tablo.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tablo.getSelectedRow();
            if (row >= 0) {
                seciliId = (Integer) model.getValueAt(row, 0);
                Kullanici k = servis.idIleGetir(seciliId);
                txtAdSoyad.setText(k.adSoyad);
                txtTelefon.setText(k.telefon);
            }
        });

        yenile();
    }

    private void yenile() {
        model = TabloBaglayici.kullaniciTablosuOlustur(servis.tumKullanicilariGetir());
        tablo.setModel(model);
    }

    private void temizle() {
        txtAdSoyad.setText("");
        txtTelefon.setText("");
        seciliId = -1;
        tablo.clearSelection();
    }

    private void ekle() {
        if (!GirisDogrulama.zorunluAlanDoluMu(txtAdSoyad.getText())) {
            JOptionPane.showMessageDialog(this, "Ad soyad boş olamaz.");
            return;
        }
        Kullanici k = new Kullanici();
        k.adSoyad = txtAdSoyad.getText().trim();
        k.telefon = txtTelefon.getText().trim();
        servis.kaydet(k);
        yenile();
        temizle();
    }

    private void guncelle() {
        if (seciliId == -1) {
            JOptionPane.showMessageDialog(this, "Güncellemek için bir kullanıcı seçin.");
            return;
        }
        Kullanici k = servis.idIleGetir(seciliId);
        k.adSoyad = txtAdSoyad.getText().trim();
        k.telefon = txtTelefon.getText().trim();
        servis.kaydet(k);
        yenile();
        temizle();
    }

    private void sil() {
        if (seciliId == -1) {
            JOptionPane.showMessageDialog(this, "Silmek için bir kullanıcı seçin.");
            return;
        }
        Kullanici k = servis.idIleGetir(seciliId);
        k.silindiMi = true;
        servis.kaydet(k);
        yenile();
        temizle();
    }
}