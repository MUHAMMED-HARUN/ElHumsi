package ui.paneller;

import bal.servisler.IstasyonServisi;
import bal.varliklar.Istasyon;
import bal.veri_baglama.TabloBaglayici;
import ui.dogrulama.GirisDogrulama;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class IstasyonPaneli extends JPanel {
    private final IstasyonServisi servis = new IstasyonServisi();
    private JTextField txtAd;
    private JTextField txtKapasite;
    private JTable tablo;
    private DefaultTableModel model;
    private int seciliId = -1;

    public IstasyonPaneli() {
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("İstasyon Adı:"));
        txtAd = new JTextField();
        form.add(txtAd);
        form.add(new JLabel("Kapasite:"));
        txtKapasite = new JTextField();
        form.add(txtKapasite);

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
                txtAd.setText(String.valueOf(model.getValueAt(row, 1)));
                txtKapasite.setText(String.valueOf(model.getValueAt(row, 2)));
            }
        });

        yenile();
    }

    private void yenile() {
        model = TabloBaglayici.istasyonTablosuOlustur(servis.tumIstasyonlariGetir());
        tablo.setModel(model);
    }

    private void temizle() {
        txtAd.setText("");
        txtKapasite.setText("");
        seciliId = -1;
        tablo.clearSelection();
    }

    private void ekle() {
        if (!GirisDogrulama.zorunluAlanDoluMu(txtAd.getText())) {
            JOptionPane.showMessageDialog(this, "İstasyon adı boş olamaz.");
            return;
        }
        int kap = 0;
        try {
            kap = Integer.parseInt(txtKapasite.getText().trim());
        } catch (Exception ex) {
            kap = 0;
        }
        if (kap < 0) kap = 0;

        Istasyon i = new Istasyon();
        i.istasyonAdi = txtAd.getText().trim();
        i.kapasite = kap;
        i.mevcutBisikletSayisi = 0;
        servis.kaydet(i);
        yenile();
        temizle();
    }

    private void guncelle() {
        if (seciliId == -1) {
            JOptionPane.showMessageDialog(this, "Güncellemek için bir istasyon seçin.");
            return;
        }
        int kap = 0;
        try {
            kap = Integer.parseInt(txtKapasite.getText().trim());
        } catch (Exception ex) {
            kap = 0;
        }
        if (kap < 0) kap = 0;

        Istasyon i = servis.idIleGetir(seciliId);
        i.istasyonAdi = txtAd.getText().trim();
        i.kapasite = kap;
        servis.kaydet(i);
        yenile();
        temizle();
    }

    private void sil() {
        if (seciliId == -1) {
            JOptionPane.showMessageDialog(this, "Silmek için bir istasyon seçin.");
            return;
        }
        Istasyon i = servis.idIleGetir(seciliId);
        i.silindiMi = true;
        boolean ok = servis.kaydet(i);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "İstasyon silinemedi (içinde bisiklet var).");
        }
        yenile();
        temizle();
    }
}