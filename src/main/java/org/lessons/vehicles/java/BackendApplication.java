package org.lessons.vehicles.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}

// Logiche di calcolo del prezzo

// • Prezzo base Ogni veicolo ha un prezzo base predefinito, ad esempio:

// • Fiat Panda: €12.000

// • Yamaha MT-07: €7.000

// • Ford Transit: €20.000

// • Cilindrata Per ogni 500cc oltre la soglia di 1000cc, aggiungi il 5% al
// prezzo base.

// • Esempio:
// • Cilindrata 1500cc → +5% (una soglia superata)
// • Cilindrata 2200cc → +10% (due soglie superate)
// • Anno di immatricolazione Considera l’anno corrente (2025).
// • Immatricolazione 2025: nessuna variazione
// • Immatricolazione 2023-2024: 4% sul prezzo base
// • Immatricolazione 2021-2022: 8% sul prezzo base
// • Immatricolazione 2020 o precedente: 12% sul prezzo base
// • Alimentazione

// • Benzina: nessuna variazione
// • Diesel: 3%
// • Elettrico: +10%
// • Ibrido: +5%
// • Gpl: 5%

// • Optional Somma il costo di ogni optional selezionato.
// • Qualche esempio:
// • Climatizzatore: €500
// • Navigatore: €300

// • Sensori di parcheggio: €200
// Logiche di sconto
// • Se l’utente seleziona almeno 3 optional, applica uno sconto del 3% sul
// totale.
// • Se il veicolo è stato immatricolato negli ultimi 12 mesi, applica uno
// sconto extra del 2% sul
// totale.
// • Se il prezzo finale supera i €20.000, applica uno sconto del 5% sulla parte
// eccedente.
// Se l’utente seleziona sia “climatizzatore” che “navigatore”, applica uno
// sconto combinato di
// €100.
// • Se il richiedente è nuovo (primo preventivo salvato), applica uno sconto di
// benvenuto del 2%