package ui.paneller;

import bal.servisler.BisikletServisi;
import bal.servisler.IstasyonServisi;
import bal.varliklar.Bisiklet;
import bal.varliklar.Istasyon;
import bal.veri_baglama.TabloBaglayici;
import ui.dogrulama.GirisDogrulama;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BisikletPaneli extends JPanel {
    private final BisikletServisi servis = new BisikletServisi();
    private final IstasyonServisi istasyonServisi = new IstasyonServisi();

    private JTextField txtKod;
    private JTextField txtModel;
    private JComboBox<String> cmbDurum;
    private JComboBox<Istasyon> cmbIstasyon;
    private JTable tablo;
    private DefaultTableModel model;
    private int seciliId = -1;

    public BisikletPaneli() {
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Bisiklet Kodu:"));
        txtKod = new JTextField();
        form.add(txtKod);
        form.add(new JLabel("Model Bilgisi:"));
        txtModel = new JTextField();
        form.add(txtModel);
        form.add(new JLabel("Durum:"));
        cmbDurum = new JComboBox<String>();
        cmbDurum.addItem("Müsait");
        cmbDurum.addItem("Kirada");
        cmbDurum.addItem("Bakımda");
        form.add(cmbDurum);
        form.add(new JLabel("İstasyon:"));
        cmbIstasyon = new JComboBox<Istasyon>();
        form.add(cmbIstasyon);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEkle = new JButton("Ekle");
        JButton btnGuncelle = new JButton("Güncelle");
        JButton btnSil = new JButton("Sil");
        JButton btnBakim = new JButton("Bakıma Al");
        JButton btnMusait = new JButton("Müsait Yap");
        JButton btnTemizle = new JButton("Temizle");
        btnPanel.add(btnEkle);
        btnPanel.add(btnGuncelle);
        btnPanel.add(btnSil);
        btnPanel.add(btnBakim);
        btnPanel.add(btnMusait);
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
        btnBakim.addActionListener(e -> durumDegistir("Bakımda"));
        btnMusait.addActionListener(e -> durumDegistir("Müsait"));
        btnTemizle.addActionListener(e -> temizle());

        tablo.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tablo.getSelectedRow();
            if (row >= 0) {
                seciliId = (Integer) model.getValueAt(row, 0);
                Bisiklet b = servis.idIleGetir(seciliId);
                txtKod.setText(b.bisikletKodu);
                txtModel.setText(b.modelBilgisi);
                cmbDurum.setSelectedItem(b.durum);
                // istasyon seç
                for (int i = 0; i < cmbIstasyon.getItemCount(); i++) {
                    Istasyon st = cmbIstasyon.getItemAt(i);
                    if (st.id == b.bulunduguIstasyonId) {
                        cmbIstasyon.setSelectedIndex(i);
                        break;
                    }
                }
                if (b.bulunduguIstasyonId == -1) cmbIstasyon.setSelectedIndex(-1);
            }
        });

        yenile();
    }

    private void yenile() {
        model = TabloBaglayici.bisikletTablosuOlustur(servis.tumBisikletleriGetir());
        tablo.setModel(model);
        TabloBaglayici.istasyonComboBoxDoldur(cmbIstasyon, istasyonServisi.tumIstasyonlariGetir());
    }

    private void temizle() {
        txtKod.setText("");
        txtModel.setText("");
        cmbDurum.setSelectedIndex(0);
        cmbIstasyon.setSelectedIndex(-1);
        seciliId = -1;
        tablo.clearSelection();
    }

    private Istasyon seciliIstasyon() {
        Object o = cmbIstasyon.getSelectedItem();
        if (o instanceof Istasyon) return (Istasyon) o;
        return null;
    }

    private void ekle() {
        if (!GirisDogrulama.zorunluAlanDoluMu(txtKod.getText())) {
            JOptionPane.showMessageDialog(this, "Bisiklet kodu boş olamaz.");
            return;
        }
        Bisiklet b = new Bisiklet();
        b.bisikletKodu = txtKod.getText().trim();
        b.modelBilgisi = txtModel.getText().trim();
        b.durum = String.valueOf(cmbDurum.getSelectedItem());
        Istasyon st = seciliIstasyon();
        if (st != null) {
            b.bulunduguIstasyonId = st.id;
        } else {
            b.bulunduguIstasyonId = -1;
        }
        b.kiralanmaSayisi = 0;
        servis.kaydet(b);

        // istasyon sayısını artır
        if (st != null) {
            Istasyon ist = istasyonServisi.idIleGetir(st.id);
            ist.mevcutBisikletSayisi = ist.mevcutBisikletSayisi + 1;
            istasyonServisi.kaydet(ist);
        }

        yenile();
        temizle();
    }

    private void guncelle() {
        if (seciliId == -1) {
            JOptionPane.showMessageDialog(this, "Güncellemek için bir bisiklet seçin.");
            return;
        }
        Bisiklet b = servis.idIleGetir(seciliId);
        // eski istasyon
        int eskiIstasyonId = b.bulunduguIstasyonId;
        b.bisikletKodu = txtKod.getText().trim();
        b.modelBilgisi = txtModel.getText().trim();
        b.durum = String.valueOf(cmbDurum.getSelectedItem());
        Istasyon yeniSt = seciliIstasyon();
        int yeniIstasyonId = (yeniSt != null) ? yeniSt.id : -1;

        // istasyon değiştiyse sayaçları güncelle
        if (eskiIstasyonId != yeniIstasyonId && !"Kirada".equals(b.durum)) {
            if (eskiIstasyonId != -1) {
                Istasyon eski = istasyonServisi.idIleGetir(eskiIstasyonId);
                if (eski.id != -1 && eski.mevcutBisikletSayisi > 0) {
                    eski.mevcutBisikletSayisi = eski.mevcutBisikletSayisi - 1;
                    istasyonServisi.kaydet(eski);
                }
            }
            if (yeniIstasyonId != -1) {
                Istasyon yeni = istasyonServisi.idIleGetir(yeniIstasyonId);
                if (yeni.id != -1) {
                    yeni.mevcutBisikletSayisi = yeni.mevcutBisikletSayisi + 1;
                    istasyonServisi.kaydet(yeni);
                }
            }
        }
        b.bulunduguIstasyonId = yeniIstasyonId;
        servis.kaydet(b);
        yenile();
        temizle();
    }

    private void sil() {
        if (seciliId == -1) {
            JOptionPane.showMessageDialog(this, "Silmek için bir bisiklet seçin.");
            return;
        }
        Bisiklet b = servis.idIleGetir(seciliId);
        if ("Kirada".equals(b.durum)) {
            JOptionPane.showMessageDialog(this, "Kiradaki bisiklet silinemez.");
            return;
        }
        // istasyon sayısını düşür
        if (b.bulunduguIstasyonId != -1) {
            Istasyon st = istasyonServisi.idIleGetir(b.bulunduguIstasyonId);
            if (st.id != -1 && st.mevcutBisikletSayisi > 0) {
                st.mevcutBisikletSayisi = st.mevcutBisikletSayisi - 1;
                istasyonServisi.kaydet(st);
            }
        }
        b.silindiMi = true;
        servis.kaydet(b);
        yenile();
        temizle();
    }

    private void durumDegistir(String yeniDurum) {
        if (seciliId == -1) {
            JOptionPane.showMessageDialog(this, "Bir bisiklet seçin.");
            return;
        }
        Bisiklet b = servis.idIleGetir(seciliId);
        b.durum = yeniDurum;
        if ("Bakımda".equals(yeniDurum) && b.bulunduguIstasyonId != -1) {
            // bakıma alındıysa istasyondan çıkar
            Istasyon st = istasyonServisi.idIleGetir(b.bulunduguIstasyonId);
            if (st.id != -1 && st.mevcutBisikletSayisi > 0) {
                st.mevcutBisikletSayisi = st.mevcutBisikletSayisi - 1;
                istasyonServisi.kaydet(st);
            }
            b.bulunduguIstasyonId = -1;
        }
        servis.kaydet(b);
        yenile();
        temizle();
    }
}