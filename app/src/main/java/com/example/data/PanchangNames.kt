package com.example.data

data class PanchangNameEntry(
    val id: Int,
    val nameTe: String,
    val nameEn: String
)

object PanchangNames {
    // 30 Tithis (1–15: Shukla Paksha, 16–30: Krishna Paksha)
    val tithis = listOf(
        PanchangNameEntry(1, "శుక్ల పాడ్యమి", "Shukla Padyami"),
        PanchangNameEntry(2, "శుక్ల విదియ", "Shukla Vidiya"),
        PanchangNameEntry(3, "శుక్ల తదియ", "Shukla Tadiya"),
        PanchangNameEntry(4, "శుక్ల చవితి", "Shukla Chavithi"),
        PanchangNameEntry(5, "శుక్ల పంచమి", "Shukla Panchami"),
        PanchangNameEntry(6, "శుక్ల షష్ఠి", "Shukla Shasthi"),
        PanchangNameEntry(7, "శుక్ల సప్తమి", "Shukla Saptami"),
        PanchangNameEntry(8, "శుక్ల అష్టమి", "Shukla Ashtami"),
        PanchangNameEntry(9, "శుక్ల నవమి", "Shukla Navami"),
        PanchangNameEntry(10, "శుక్ల దశమి", "Shukla Dashami"),
        PanchangNameEntry(11, "శుక్ల ఏకాదశి", "Shukla Ekadashi"),
        PanchangNameEntry(12, "శుక్ల ద్వాదశి", "Shukla Dwadashi"),
        PanchangNameEntry(13, "శుక్ల త్రయోదశి", "Shukla Trayodashi"),
        PanchangNameEntry(14, "శుక్ల చతుర్దశి", "Shukla Chaturdashi"),
        PanchangNameEntry(15, "పూర్ణిమ (పౌర్ణమి)", "Purnima (Full Moon)"),
        PanchangNameEntry(16, "కృష్ణ పాడ్యమి", "Krishna Padyami"),
        PanchangNameEntry(17, "కృష్ణ విదియ", "Krishna Vidiya"),
        PanchangNameEntry(18, "కృష్ణ తదియ", "Krishna Tadiya"),
        PanchangNameEntry(19, "కృష్ణ చవితి", "Krishna Chavithi"),
        PanchangNameEntry(20, "కృష్ణ పంచమి", "Krishna Panchami"),
        PanchangNameEntry(21, "కృష్ణ షష్ఠి", "Krishna Shasthi"),
        PanchangNameEntry(22, "కృష్ణ సప్తమి", "Krishna Saptami"),
        PanchangNameEntry(23, "కృష్ణ అష్టమి", "Krishna Ashtami"),
        PanchangNameEntry(24, "కృష్ణ నవమి", "Krishna Navami"),
        PanchangNameEntry(25, "కృష్ణ దశమి", "Krishna Dashami"),
        PanchangNameEntry(26, "కృష్ణ ఏకాదశి", "Krishna Ekadashi"),
        PanchangNameEntry(27, "కృష్ణ ద్వాదశి", "Krishna Dwadashi"),
        PanchangNameEntry(28, "కృష్ణ త్రయోదశి", "Krishna Trayodashi"),
        PanchangNameEntry(29, "కృష్ణ చతుర్దశి", "Krishna Chaturdashi"),
        PanchangNameEntry(30, "అమావాస్య", "Amavasya (New Moon)")
    )

    // 27 Nakshatras
    val nakshatras = listOf(
        PanchangNameEntry(1, "అశ్విని", "Ashwini"),
        PanchangNameEntry(2, "భరణి", "Bharani"),
        PanchangNameEntry(3, "కృత్తిక", "Krittika"),
        PanchangNameEntry(4, "రోహిణి", "Rohini"),
        PanchangNameEntry(5, "మృగశిర", "Mrigashira"),
        PanchangNameEntry(6, "ఆర్ద్ర", "Ardra"),
        PanchangNameEntry(7, "పునర్వసు", "Punarvasu"),
        PanchangNameEntry(8, "పుష్యమి", "Pushyami"),
        PanchangNameEntry(9, "ఆశ్లేష", "Ashlesha"),
        PanchangNameEntry(10, "మఖ", "Magha"),
        PanchangNameEntry(11, "పూర్వ ఫల్గుణి (పుబ్బ)", "Purva Phalguni (Pubba)"),
        PanchangNameEntry(12, "ఉత్తర ఫల్గుణి (ఉత్తర)", "Uttara Phalguni"),
        PanchangNameEntry(13, "హస్త", "Hasta"),
        PanchangNameEntry(14, "చిత్త", "Chitra"),
        PanchangNameEntry(15, "స్వాతి", "Swati"),
        PanchangNameEntry(16, "విశాఖ", "Vishakha"),
        PanchangNameEntry(17, "అనూరాధ", "Anuradha"),
        PanchangNameEntry(18, "జ్యేష్ఠ", "Jyeshtha"),
        PanchangNameEntry(19, "మూల", "Mula"),
        PanchangNameEntry(20, "పూర్వాషాఢ", "Purvashadha"),
        PanchangNameEntry(21, "ఉత్తరాషాఢ", "Uttarashadha"),
        PanchangNameEntry(22, "శ్రవణం", "Shravana"),
        PanchangNameEntry(23, "ధనిష్ఠ", "Dhanishta"),
        PanchangNameEntry(24, "శతభిషం", "Shatabhisha"),
        PanchangNameEntry(25, "పూర్వాభాద్ర", "Purvabhadra"),
        PanchangNameEntry(26, "ఉత్తరాభాద్ర", "Uttarabhadra"),
        PanchangNameEntry(27, "రేవతి", "Revati")
    )

    // 27 Yogas
    val yogas = listOf(
        PanchangNameEntry(1, "విష్కంభ", "Vishkambha"),
        PanchangNameEntry(2, "ప్రీతి", "Priti"),
        PanchangNameEntry(3, "ఆయుష్మాన్", "Ayushman"),
        PanchangNameEntry(4, "సౌభాగ్య", "Saubhagya"),
        PanchangNameEntry(5, "శోభన", "Shobhana"),
        PanchangNameEntry(6, "అతిగండ", "Atiganda"),
        PanchangNameEntry(7, "సుకర్మ", "Sukarma"),
        PanchangNameEntry(8, "ధృతి", "Dhriti"),
        PanchangNameEntry(9, "శూల", "Shula"),
        PanchangNameEntry(10, "గండ", "Ganda"),
        PanchangNameEntry(11, "వృద్ధి", "Vriddhi"),
        PanchangNameEntry(12, "ధ్రువ", "Dhruva"),
        PanchangNameEntry(13, "వ్యాఘాత", "Vyaghata"),
        PanchangNameEntry(14, "హర్షణ", "Harshana"),
        PanchangNameEntry(15, "వజ్ర", "Vajra"),
        PanchangNameEntry(16, "సిద్ధి", "Siddhi"),
        PanchangNameEntry(17, "వ్యతీపాత", "Vyatipata"),
        PanchangNameEntry(18, "వరీయాన్", "Variyan"),
        PanchangNameEntry(19, "పరిఘ", "Parigha"),
        PanchangNameEntry(20, "శివ", "Shiva"),
        PanchangNameEntry(21, "సిద్ధ", "Siddha"),
        PanchangNameEntry(22, "సాధ్య", "Sadhya"),
        PanchangNameEntry(23, "శుభ", "Shubha"),
        PanchangNameEntry(24, "శుభ్ర", "Shubhra"),
        PanchangNameEntry(25, "బ్రహ్మ", "Brahma"),
        PanchangNameEntry(26, "ఐంద్ర", "Aindra"),
        PanchangNameEntry(27, "వైధృతి", "Vaidhriti")
    )

    // 11 Karanas (4 Fixed + 7 Repeating)
    val repeatingKaranas = listOf(
        PanchangNameEntry(1, "బవ", "Bava"),
        PanchangNameEntry(2, "బాలవ", "Balava"),
        PanchangNameEntry(3, "కౌలవ", "Kaulava"),
        PanchangNameEntry(4, "తైతిల", "Taitila"),
        PanchangNameEntry(5, "గరజ", "Garaja"),
        PanchangNameEntry(6, "వణిజ", "Vanija"),
        PanchangNameEntry(7, "విష్టి (భద్ర)", "Vishti (Bhadra)")
    )

    val fixedKaranas = mapOf(
        0 to PanchangNameEntry(0, "కింస్తుఘ్న", "Kimstughna"),
        57 to PanchangNameEntry(57, "శకుని", "Shakuni"),
        58 to PanchangNameEntry(58, "చతుష్పాద", "Chatushpada"),
        59 to PanchangNameEntry(59, "నాగవ", "Naga")
    )

    fun getKarana(halfTithiIndex: Int): PanchangNameEntry {
        return fixedKaranas[halfTithiIndex] ?: run {
            val idx = (halfTithiIndex - 1) % 7
            repeatingKaranas[if (idx >= 0) idx else idx + 7]
        }
    }

    fun getTithi(number: Int): PanchangNameEntry {
        val safe = ((number - 1) % 30 + 30) % 30
        return tithis[safe]
    }

    fun getNakshatra(number: Int): PanchangNameEntry {
        val safe = ((number - 1) % 27 + 27) % 27
        return nakshatras[safe]
    }

    fun getYoga(number: Int): PanchangNameEntry {
        val safe = ((number - 1) % 27 + 27) % 27
        return yogas[safe]
    }
}
