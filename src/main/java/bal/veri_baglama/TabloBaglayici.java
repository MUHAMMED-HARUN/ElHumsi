package bal.veri_baglama;

import bal.varliklar.Bisiklet;
import bal.varliklar.Istasyon;
import bal.varliklar.KiralamaHareketi;
import bal.varliklar.Kullanici;

import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class TabloBaglayici {

    public static DefaultTableModel istasyonTablosuOlustur(List<Istasyon> liste) {
        String[] kolonlar = {"ID", "İstasyon Adı", "Kapasite", "Mevcut", "Boş Yer"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        if (liste != null) {
            for (Istasyon i : liste) {
                if (i.silindiMi == true) continue;
                Object[] satir = {i.id, i.istasyonAdi, i.kapasite, i.mevcutBisikletSayisi, i.bosYerSayisiHesapla()};
                model.addRow(satir);
            }
        }
        return model;
    }

    public static DefaultTableModel bisikletTablosuOlustur(List<Bisiklet> liste) {
        String[] kolonlar = {"ID", "Bisiklet Kodu", "Model", "Durum", "İstasyon ID", "Kiralanma Sayısı"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        if (liste != null) {
            for (Bisiklet b : liste) {
                if (b.silindiMi == true) continue;
                Object[] satir = {b.id, b.bisikletKodu, b.modelBilgisi, b.durum, b.bulunduguIstasyonId, b.kiralanmaSayisi};
                model.addRow(satir);
            }
        }
        return model;
    }

    public static DefaultTableModel kullaniciTablosuOlustur(List<Kullanici> liste) {
        String[] kolonlar = {"ID", "Ad Soyad", "Telefon"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        if (liste != null) {
            for (Kullanici k : liste) {
                if (k.silindiMi == true) continue;
                Object[] satir = {k.id, k.adSoyad, k.telefon};
                model.addRow(satir);
            }
        }
        return model;
    }

    public static DefaultTableModel kiralamaTablosuOlustur(List<KiralamaHareketi> liste) {
        String[] kolonlar = {"ID", "Bisiklet Kodu", "Kullanıcı ID", "Başlangıç İst.", "İade İst.", "Aktif"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        if (liste != null) {
            for (KiralamaHareketi k : liste) {
                if (k.silindiMi == true) continue;
                Object[] satir = {k.id, k.bisikletKodu, k.kullaniciId, k.baslangicIstasyonId, k.iadeIstasyonId, k.aktifMi};
                model.addRow(satir);
            }
        }
        return model;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void istasyonComboBoxDoldur(JComboBox comboBox, List<Istasyon> liste) {
        comboBox.removeAllItems();
        if (liste == null) return;
        for (Istasyon i : liste) {
            if (i.silindiMi == true) continue;
            comboBox.addItem(i);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void bisikletComboBoxDoldur(JComboBox comboBox, List<Bisiklet> liste) {
        comboBox.removeAllItems();
        if (liste == null) return;
        for (Bisiklet b : liste) {
            if (b.silindiMi == true) continue;
            comboBox.addItem(b);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void kullaniciComboBoxDoldur(JComboBox comboBox, List<Kullanici> liste) {
        comboBox.removeAllItems();
        if (liste == null) return;
        for (Kullanici k : liste) {
            if (k.silindiMi == true) continue;
            comboBox.addItem(k);
        }
    }
}