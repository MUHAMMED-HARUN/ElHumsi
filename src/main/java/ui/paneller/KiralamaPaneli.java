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
import java.util.List;

public class KiralamaPaneli extends JPanel {
    private final KiralamaServisi kiralamaServisi = new KiralamaServisi();
    private final KullaniciServisi kullaniciServisi = new KullaniciServisi();
    private final BisikletServisi bisikletServisi = new BisikletServisi();
    private final IstasyonServisi istasyonServisi = new IstasyonServisi();

    private JComboBox<Kullanici> cmbKullanici;
    private JComboBox<Bisiklet> cmbBisiklet;
    private JComboBox<Istasyon> cmbBaslangicIstasyon;
    private JComboBox<Bisiklet> cmbIadeBisiklet;
    private JComboBox<Istasyon> cmbIadeIstasyon;
    private JTable tablo;
    private DefaultTableModel model;

    public KiralamaPaneli() {
        setLayout(new BorderLayout(5, 5));

        JPanel kiralaForm = new JPanel(new GridLayout(4, 2, 5, 5));
        kiralaForm.setBorder(BorderFactory.createTitledBorder("Kiralama"));
        kiralaForm.add(new JLabel("Kullanıcı:"));
        cmbKullanici = new JComboBox<Kullanici>();
        kiralaForm.add(cmbKullanici);
        kiralaForm.add(new JLabel("Bisiklet (Müsait):"));
        cmbBisiklet = new JComboBox<Bisiklet>();
        kiralaForm.add(cmbBisiklet);
        kiralaForm.add(new JLabel("Başlangıç İstasyonu:"));
        cmbBaslangicIstasyon = new JComboBox<Istasyon>();
        kiralaForm.add(cmbBaslangicIstasyon);
        JButton btnKirala = new JButton("Kirala");
        kiralaForm.add(new JLabel(""));
        kiralaForm.add(btnKirala);

        JPanel iadeForm = new JPanel(new GridLayout(3, 2, 5, 5));
        iadeForm.setBorder(BorderFactory.createTitledBorder("İade"));
        iadeForm.add(new JLabel("Kiradaki Bisiklet:"));
        cmbIadeBisiklet = new JComboBox<Bisiklet>();
        iadeForm.add(cmbIadeBisiklet);
        iadeForm.add(new JLabel("İade İstasyonu:"));
        cmbIadeIstasyon = new JComboBox<Istasyon>();
        iadeForm.add(cmbIadeIstasyon);
        JButton btnIade = new JButton("İade Et");
        iadeForm.add(new JLabel(""));
        iadeForm.add(btnIade);

        JPanel ust = new JPanel(new GridLayout(2, 1, 5, 5));
        ust.add(kiralaForm);
        ust.add(iadeForm);
        add(ust, BorderLayout.NORTH);

        tablo = new JTable();
        add(new JScrollPane(tablo), BorderLayout.CENTER);

        JButton btnYenile = new JButton("Yenile");
        JPanel alt = new JPanel(new FlowLayout(FlowLayout.LEFT));
        alt.add(btnYenile);
        add(alt, BorderLayout.SOUTH);

        btnKirala.addActionListener(e -> kirala());
        btnIade.addActionListener(e -> iade());
        btnYenile.addActionListener(e -> yenile());

        cmbBisiklet.addActionListener(e -> {
            Bisiklet b = (Bisiklet) cmbBisiklet.getSelectedItem();
            if (b != null) {
                for (int i = 0; i < cmbBaslangicIstasyon.getItemCount(); i++) {
                    Istasyon st = cmbBaslangicIstasyon.getItemAt(i);
                    if (st.id == b.bulunduguIstasyonId) {
                        cmbBaslangicIstasyon.setSelectedIndex(i);
                        return;
                    }
                }
            }
        });

        yenile();
    }

    private void yenile() {
        // kullanıcılar
        TabloBaglayici.kullaniciComboBoxDoldur(cmbKullanici, kullaniciServisi.tumKullanicilariGetir());
        // müsait bisikletler
        cmbBisiklet.removeAllItems();
        List<Bisiklet> tumB = bisikletServisi.tumBisikletleriGetir();
        for (Bisiklet b : tumB) {
            if (b.silindiMi == true) continue;
            if ("Müsait".equals(b.durum)) cmbBisiklet.addItem(b);
        }
        // kiradaki bisikletler
        cmbIadeBisiklet.removeAllItems();
        for (Bisiklet b : tumB) {
            if (b.silindiMi == true) continue;
            if ("Kirada".equals(b.durum)) cmbIadeBisiklet.addItem(b);
        }
        // istasyonlar
        List<Istasyon> istler = istasyonServisi.tumIstasyonlariGetir();
        TabloBaglayici.istasyonComboBoxDoldur(cmbBaslangicIstasyon, istler);
        TabloBaglayici.istasyonComboBoxDoldur(cmbIadeIstasyon, istler);

        model = TabloBaglayici.kiralamaTablosuOlustur(kiralamaServisi.tumHareketleriGetir());
        tablo.setModel(model);
    }

    private void kirala() {
        Kullanici k = (Kullanici) cmbKullanici.getSelectedItem();
        Bisiklet b = (Bisiklet) cmbBisiklet.getSelectedItem();
        Istasyon st = (Istasyon) cmbBaslangicIstasyon.getSelectedItem();
        if (k == null || b == null || st == null) {
            JOptionPane.showMessageDialog(this, "Tüm alanları seçin.");
            return;
        }
        boolean ok = kiralamaServisi.kirala(k.id, b.bisikletKodu, st.id);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Kiralama başarısız.");
        } else {
            JOptionPane.showMessageDialog(this, "Kiralama başarılı.");
        }
        yenile();
    }

    private void iade() {
        Bisiklet b = (Bisiklet) cmbIadeBisiklet.getSelectedItem();
        Istasyon st = (Istasyon) cmbIadeIstasyon.getSelectedItem();
        if (b == null || st == null) {
            JOptionPane.showMessageDialog(this, "Tüm alanları seçin.");
            return;
        }
        if (st.mevcutBisikletSayisi >= st.kapasite) {
            JOptionPane.showMessageDialog(this, "İstasyon dolu, iade edilemez.");
            return;
        }
        boolean ok = kiralamaServisi.iadeEt(b.bisikletKodu, st.id);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "İade başarısız.");
        } else {
            JOptionPane.showMessageDialog(this, "İade başarılı.");
        }
        yenile();
    }
}