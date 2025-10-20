package org.example;

import javax.sound.midi.*;

public class melodia {

    // Melodía simple de inicio
    public static void inicio() {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();

            MidiChannel canal = synth.getChannels()[0];
            int[] notas = {60, 62, 64, 65, 67};
            int duracion = 500;

            for (int nota : notas) {
                canal.noteOn(nota, 100);
                Thread.sleep(duracion);
                canal.noteOff(nota);
            }

            synth.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🎵 Melodía del pueblo
    public static void melodiaPueblo() {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            MidiChannel canal = synth.getChannels()[0];

            // Duración de cada nota en milisegundos
            int duracion = 300;
            // Duración para la nota final sostenida
            int duracionSostenida = 1200;

            // Melodía completa en valores MIDI
            int[] notas = {
                    // Intro (8 compases)
                    60, 64, 67, 72,  62, 65, 69, 74,  60, 64, 67, 72,  55, 59, 62, 67,

                    // Verso (16 compases)
                    60, 62, 64, 67, 64, 62, 60, 55,
                    65, 69, 72, 69, 67, 65, 64, 62,
                    67, 71, 74, 71, 69, 67, 65, 64,
                    60, 55, 64, 60, 62, 67, 64, 60,

                    // Puente (8 compases)
                    57, 60, 64, 69,   55, 59, 62, 67,
                    53, 57, 60, 65,   60, 64, 67, 72,

                    // Repetición con variación (16 compases)
                    60, 62, 64, 65, 67, 69, 67, 64,
                    65, 64, 62, 67, 69, 65, 64, 60,
                    67, 69, 71, 74, 71, 69, 67, 65,
                    60, 64, 67, 72, 67, 64, 62, 60,

                    // Final (4 compases)
                    65, 67, 72, 67,   64, 62, 60
            };

            for (int i = 0; i < notas.length; i++) {
                int nota = notas[i];
                canal.noteOn(nota, 100); // El segundo parámetro es la "velocidad" o volumen (0-127)

                // Si es la última nota, aplica la duración sostenida
                if (i == notas.length - 1) {
                    Thread.sleep(duracionSostenida);
                } else {
                    Thread.sleep(duracion);
                }

                canal.noteOff(nota);
            }

            synth.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 🌙 Melodía de noche
    // 🌙 Melodía de noche
    public static void melodiaNoche() {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            MidiChannel canal = synth.getChannels()[0];
            // Instrumento 42 es Cello, ideal para un sonido suave y nocturno
            canal.programChange(42);

            // Duración de las notas en milisegundos
            int duracion = 500;
            // Duración para la nota final mantenida
            int duracionMantenida = 2000;

            // Melodía completa traducida a valores MIDI
            int[] notas = {
                    // Intro (4 compases)
                    48, 55, 57, 53,

                    // Tema principal (8 compases)
                    60, 62, 67, 65,
                    64, 62, 57, 55,
                    60, 62, 65, 64,
                    67, 65, 62, 60,

                    // Repetición (8 compases)
                    60, 62, 67, 65,
                    69, 67, 64, 62,
                    60, 55, 53, 60,
                    62, 64, 60, 55,

                    // Puente con aire soñador (8 compases)
                    65, 67, 69, 72,
                    64, 62, 67, 69,
                    65, 64, 62, 60,
                    55, 57, 53, 48,

                    // Cierre (4 compases)
                    60, 55, 57, 53,
                    55, 53, 48
            };

            for (int i = 0; i < notas.length; i++) {
                int nota = notas[i];
                canal.noteOn(nota, 80); // Volumen (velocidad) a 80 para un toque más suave

                // Si es la última nota, aplica la duración mantenida
                if (i == notas.length - 1) {
                    Thread.sleep(duracionMantenida);
                } else {
                    Thread.sleep(duracion);
                }

                canal.noteOff(nota);
            }

            synth.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ⚙️ Melodía de taller
    public static void melodiaTaller() {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            MidiChannel canal = synth.getChannels()[0];
            // Instrumento 11 es Vibraphone, ideal para un sonido metálico
            canal.programChange(11);

            // --- Definición de duraciones ---
            int duracionNormal = 300;
            int duracionRapida = 150; // Para la segunda parte
            int duracionAcorde = 400;
            int duracionFinal = 800;
            int volumen = 90;

            // --- Intro (ritmo metálico) ---
            int[] intro = {60, 67, 64, 60, 60, 67, 64, 60};
            for (int nota : intro) {
                canal.noteOn(nota, volumen);
                Thread.sleep(duracionNormal);
                canal.noteOff(nota);
            }

            // --- Motivo principal ---
            int[] motivoPrincipal = {
                    60, 64, 67, 69, 65, 69, 72, 67,
                    67, 71, 74, 65, 64, 67, 71, 74
            };
            for (int nota : motivoPrincipal) {
                canal.noteOn(nota, volumen);
                Thread.sleep(duracionNormal);
                canal.noteOff(nota);
            }

            // --- Segunda parte (más rápida) ---
            int[] segundaParte = {
                    60, 62, 65, 67, 69, 65, 62, 60,
                    64, 67, 69, 72, 67, 65, 62, 64
            };
            for (int nota : segundaParte) {
                canal.noteOn(nota, volumen);
                Thread.sleep(duracionRapida);
                canal.noteOff(nota);
            }

            // --- Puente mecánico (acordes) ---
            int[][] puenteAcordes = {
                    {60, 64, 67}, {65, 69, 72}, // (C4 E4 G4) (F4 A4 C5)
                    {55, 59, 62}, {64, 67, 72}, // (G3 B3 D4) (E4 G4 C5)
                    {62, 65, 69}, {67, 71, 74}, // (D4 F4 A4) (G4 B4 D5)
                    {60, 64, 67}, {69, 72, 76}  // (C4 E4 G4) (A4 C5 E5)
            };
            for (int[] acorde : puenteAcordes) {
                for (int nota : acorde) canal.noteOn(nota, volumen);
                Thread.sleep(duracionAcorde);
                for (int nota : acorde) canal.noteOff(nota);
            }

            // --- Cierre (repetir dos veces) ---
            int[] cierre = {67, 65, 64, 62}; // G4 F4 E4 D4
            for (int i = 0; i < 2; i++) {
                for (int nota : cierre) {
                    canal.noteOn(nota, volumen);
                    Thread.sleep(duracionNormal);
                    canal.noteOff(nota);
                }
                // Tocar la nota final C4 y mantenerla
                canal.noteOn(60, volumen);
                Thread.sleep(duracionFinal);
                canal.noteOff(60);
            }

            synth.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

