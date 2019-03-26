import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class App {
    private ArrayList<JCheckBox> checkboxListe;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private String[] instrumentNamen = {"Bassdrum", "Hi-Hat, geschlossen",
            "Hi-Hat, offen", "Snaredrum", "Crashbecken", "Händeklatschen",
            "Hohes Tom-Tom", "Hohes Bongo", "Maracas", "Trillerpfeife",
            "Tiefe Conga", "Kuhglocke", "Vibraslap", "Tieferes Tom-Tom",
            "Hohes Agogo", "Hohe Conga, offen"};
    private int[] instrumente = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        new App().guiErstellen();
    }

    private void guiErstellen() {
        JFrame derFrame = new JFrame("Cyber-BeatBox");
        derFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel hintergrund = new JPanel(layout);
        hintergrund.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        checkboxListe = new ArrayList<>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        JButton start = new JButton("Starten");
        start.addActionListener(new MeinStartListener());
        buttonBox.add(start);
        JButton stopp = new JButton("Stoppen");
        stopp.addActionListener(new MeinStoppListener());
        buttonBox.add(stopp);
        JButton schneller = new JButton("Schneller");
        schneller.addActionListener(new MeinSchnellerListener());
        buttonBox.add(schneller);
        JButton langsamer = new JButton("Langsamer");
        langsamer.addActionListener(new MeinLangsamerListener());
        buttonBox.add(langsamer);
        Box namensBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            namensBox.add(new Label(instrumentNamen[i]));
        }
        hintergrund.add(BorderLayout.EAST, buttonBox);
        hintergrund.add(BorderLayout.WEST, namensBox);
        derFrame.getContentPane().add(hintergrund);
        GridLayout raster = new GridLayout(16, 16);
        raster.setVgap(1);
        raster.setHgap(2);
        JPanel hauptPanel = new JPanel(raster);
        hintergrund.add(BorderLayout.CENTER, hauptPanel);
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxListe.add(c);
            hauptPanel.add(c);
        } // Ende der Schleife
        midiEinrichten();
        derFrame.setBounds(50, 50, 300, 300);
        derFrame.pack();
        derFrame.setVisible(true);
    } // Methode schließen

    private void midiEinrichten() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // Methode schließen

    private void trackErstellenUndStarten() {
        int[] trackListe;
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        for (int i = 0; i < 16; i++) {
            trackListe = new int[16];
            int taste = instrumente[i];
            for (int j = 0; j < 16; j++) {
                JCheckBox jc = checkboxListe.get(j + (16 * i));
                if (jc.isSelected()) {
                    trackListe[j] = taste;
                } else {
                    trackListe[j] = 0;
                }
            } // Ende der inneren Schleife
            tracksErzeugen(trackListe);
        } // Ende der äußeren Schleife
        track.add(eventErzeugen(192, 1, 0, 16));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // Methode trackErstellenUndStarten schließen

    private void tracksErzeugen(int[] liste) {
        for (int i = 0; i < 16; i++) {
            int taste = liste[i];
            if (taste != 0) {
                track.add(eventErzeugen(144, taste, 100, i));
                track.add(eventErzeugen(128, taste, 100, i + 1));
            }
        }
    }

    private MidiEvent eventErzeugen(int comd, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, 9, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }

    public class MeinStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            trackErstellenUndStarten();
        }
    } // innere Klasse schließen

    public class MeinStoppListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }
    } // innere Klasse schließen

    public class MeinSchnellerListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    } // innere Klasse schließen

    public class MeinLangsamerListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * .97));
        }
    } // innere Klasse schließen
} // Klasse schließen