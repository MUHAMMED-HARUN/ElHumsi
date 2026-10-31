package ui.paneller;

import bal.servisler.BisikletServisi;
import bal.servisler.IstasyonServisi;
import bal.servisler.KiralamaServisi;
import bal.servisler.KullaniciServisi;
import bal.varliklar.Bisiklet;
import bal.varliklar.Istasyon;
import bal.varliklar.KiralamaHareketi;
import bal.varliklar.Kullanici;
import bal.veri_baglama.TabloBaglayici;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SorgulamaPaneli extends JPanel {
    private final BisikletServisi bisikletServisi = new BisikletServisi();
    private final IstasyonServisi istasyonServisi = new IstasyonServisi();
    private final KullaniciServisi kullaniciServisi = new KullaniciServisi();
    private final KiralamaServisi kiralamaServisi = new KiralamaServisi();

    private JComboBox<Istasyon> cmbIstasyon;
    private JComboBox<Kullanici> cmbKullanici;
    private JTable tablo;
    private DefaultTableModel model;

    public SorgulamaPaneli() {
        setLayout(new BorderLayout(5, 5));

        JPanel filtre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtre.add(new JLabel("İstasyon:"));
        cmbIstasyon = new JComboBox<Istasyon>();
        filtre.add(cmbIstasyon);
        JButton btnIstasyonBisiklet = new JButton("İstasyondaki Bisikletler");
        filtre.add(btnIstasyonBisiklet);

        filtre.add(new JLabel("Kullanıcı:"));
        cmbKullanici = new JComboBox<Kullanici>();
        filtre.add(cmbKullanici);
        JButton btnKullaniciGecmis = new JButton("Kullanıcı Geçmişi");
        filtre.add(btnKullaniciGecmis);

        JPanel butonlar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEnCok = new JButton("En Çok Kiralanan Bisikletler");
        JButton btnBosYer = new JButton("Boş Yeri Olan İstasyonlar");
        JButton btnBakim = new JButton("Bakımdaki Bisikletler");
        JButton btnAktif = new JButton("Aktif Kiralamalar");
        JButton btnTumIstasyon = new JButton("Tüm İstasyonlar");
        JButton btnTumBisiklet = new JButton("Tüm Bisikletler");
        butonlar.add(btnEnCok);
        butonlar.add(btnBosYer);
        butonlar.add(btnBakim);
        butonlar.add(btnAktif);
        butonlar.add(btnTumIstasyon);
        butonlar.add(btnTumBisiklet);

        JPanel ust = new JPanel(new BorderLayout());
        ust.add(filtre, BorderLayout.NORTH);
        ust.add(butonlar, BorderLayout.SOUTH);
        add(ust, BorderLayout.NORTH);

        tablo = new JTable();
        add(new JScrollPane(tablo), BorderLayout.CENTER);

        btnIstasyonBisiklet.addActionListener(e -> istasyondakiBisikletler());
        btnKullaniciGecmis.addActionListener(e -> kullaniciGecmisi());
        btnEnCok.addActionListener(e -> enCokKiralanan());
        btnBosYer.addActionListener(e -> bosYeriOlanIstasyonlar());
        btnBakim.addActionListener(e -> bakimdakiBisikletler());
        btnAktif.addActionListener(e -> aktifKiralamalar());
        btnTumIstasyon.addActionListener(e -> tumIstasyonlar());
        btnTumBisiklet.addActionListener(e -> tumBisikletler());

        yenileCombolar();
        tumIstasyonlar();
    }

    private void yenileCombolar() {
        TabloBaglayici.istasyonComboBoxDoldur(cmbIstasyon, istasyonServisi.tumIstasyonlariGetir());
        TabloBaglayici.kullaniciComboBoxDoldur(cmbKullanici, kullaniciServisi.tumKullanicilariGetir());
    }

    private void istasyondakiBisikletler() {
        Istasyon st = (Istasyon) cmbIstasyon.getSelectedItem();
        if (st == null) return;
        List<Bisiklet> sonuc = new ArrayList<Bisiklet>();
        for (Bisiklet b : bisikletServisi.tumBisikletleriGetir()) {
            if (b.silindiMi == true) continue;
            if (b.bulunduguIstasyonId == st.id) sonuc.add(b);
        }
        model = TabloBaglayici.bisikletTablosuOlustur(sonuc);
        tablo.setModel(model);
    }

    private void kullaniciGecmisi() {
        Kullanici k = (Kullanici) cmbKullanici.getSelectedItem();
        if (k == null) return;
        model = TabloBaglayici.kiralamaTablosuOlustur(kiralamaServisi.kullaniciHareketleri(k.id));
        tablo.setModel(model);
    }

    private void enCokKiralanan() {
        List<Bisiklet> liste = new ArrayList<Bisiklet>();
        for (Bisiklet b : bisikletServisi.tumBisikletleriGetir()) {
            if (b.silindiMi == true) continue;
            liste.add(b);
        }
        // basit sıralama (bubble) azalan kiralanmaSayisi
        for (int i = 0; i < liste.size(); i++) {
            for (int j = i + 1; j < liste.size(); j++) {
                if (liste.get(j).kiralanmaSayisi > liste.get(i).kiralanmaSayisi) {
                    Bisiklet tmp = liste.get(i);
                    liste.set(i, liste.get(j));
                    liste.set(j, tmp);
                }
            }
        }
        model = TabloBaglayici.bisikletTablosuOlustur(liste);
        tablo.setModel(model);
    }

    private void bosYeriOlanIstasyonlar() {
        List<Istasyon> sonuc = new ArrayList<Istasyon>();
        for (Istasyon i : istasyonServisi.tumIstasyonlariGetir()) {
            if (i.silindiMi == true) continue;
            if (i.bosYerSayisiHesapla() > 0) sonuc.add(i);
        }
        model = TabloBaglayici.istasyonTablosuOlustur(sonuc);
        tablo.setModel(model);
    }

    private void bakimdakiBisikletler() {
        List<Bisiklet> sonuc = new ArrayList<Bisiklet>();
        for (Bisiklet b : bisikletServisi.tumBisikletleriGetir()) {
            if (b.silindiMi == true) continue;
            if ("Bakımda".equals(b.durum)) sonuc.add(b);
        }
        model = TabloBaglayici.bisikletTablosuOlustur(sonuc);
        tablo.setModel(model);
    }

    private void aktifKiralamalar() {
        model = TabloBaglayici.kiralamaTablosuOlustur(kiralamaServisi.aktifHareketleriGetir());
        tablo.setModel(model);
    }

    private void tumIstasyonlar() {
        model = TabloBaglayici.istasyonTablosuOlustur(istasyonServisi.tumIstasyonlariGetir());
        tablo.setModel(model);
    }

    private void tumBisikletler() {
        model = TabloBaglayici.bisikletTablosuOlustur(bisikletServisi.tumBisikletleriGetir());
        tablo.setModel(model);
    }
}