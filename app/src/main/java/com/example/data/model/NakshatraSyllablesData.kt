package com.example.data.model

// verify with a purohit or printed panchangam
object NakshatraSyllablesData {
    val all = listOf(
        NakshatraSyllables(1, listOf(PadaSyllable("Chu", "చు"), PadaSyllable("Che", "చే"), PadaSyllable("Cho", "చో"), PadaSyllable("La", "ల"))),
        NakshatraSyllables(2, listOf(PadaSyllable("Li", "లీ"), PadaSyllable("Lu", "లూ"), PadaSyllable("Le", "లే"), PadaSyllable("Lo", "లో"))),
        NakshatraSyllables(3, listOf(PadaSyllable("A", "అ"), PadaSyllable("I", "ఈ"), PadaSyllable("U", "ఉ"), PadaSyllable("E", "ఏ"))),
        NakshatraSyllables(4, listOf(PadaSyllable("O", "ఓ"), PadaSyllable("Va", "వా"), PadaSyllable("Vi", "వీ"), PadaSyllable("Vu", "వూ"))),
        NakshatraSyllables(5, listOf(PadaSyllable("Ve", "వే"), PadaSyllable("Vo", "వో"), PadaSyllable("Ka", "కా"), PadaSyllable("Ki", "కీ"))),
        NakshatraSyllables(6, listOf(PadaSyllable("Ku", "కూ"), PadaSyllable("Gha", "ఘ"), PadaSyllable("Ng", "ఙ"), PadaSyllable("Chha", "ఛ"))),
        NakshatraSyllables(7, listOf(PadaSyllable("Ke", "కే"), PadaSyllable("Ko", "కో"), PadaSyllable("Ha", "హా"), PadaSyllable("Hi", "హీ"))),
        NakshatraSyllables(8, listOf(PadaSyllable("Hu", "హూ"), PadaSyllable("He", "హే"), PadaSyllable("Ho", "హో"), PadaSyllable("Da", "డా"))),
        NakshatraSyllables(9, listOf(PadaSyllable("Di", "డీ"), PadaSyllable("Du", "డూ"), PadaSyllable("De", "డే"), PadaSyllable("Do", "డో"))),
        NakshatraSyllables(10, listOf(PadaSyllable("Ma", "మా"), PadaSyllable("Mi", "మీ"), PadaSyllable("Mu", "మూ"), PadaSyllable("Me", "మే"))),
        NakshatraSyllables(11, listOf(PadaSyllable("Mo", "మో"), PadaSyllable("Ta", "టా"), PadaSyllable("Ti", "టీ"), PadaSyllable("Tu", "టూ"))),
        NakshatraSyllables(12, listOf(PadaSyllable("Te", "టే"), PadaSyllable("To", "టో"), PadaSyllable("Pa", "పా"), PadaSyllable("Pi", "పీ"))),
        NakshatraSyllables(13, listOf(PadaSyllable("Pu", "పూ"), PadaSyllable("Sha", "ష"), PadaSyllable("Na", "ణ"), PadaSyllable("Tha", "ఠ"))),
        NakshatraSyllables(14, listOf(PadaSyllable("Pe", "పే"), PadaSyllable("Po", "పో"), PadaSyllable("Ra", "రా"), PadaSyllable("Ri", "రీ"))),
        NakshatraSyllables(15, listOf(PadaSyllable("Ru", "రూ"), PadaSyllable("Re", "రే"), PadaSyllable("Ro", "రో"), PadaSyllable("Ta", "తా"))),
        NakshatraSyllables(16, listOf(PadaSyllable("Ti", "తీ"), PadaSyllable("Tu", "తూ"), PadaSyllable("Te", "తే"), PadaSyllable("To", "తో"))),
        NakshatraSyllables(17, listOf(PadaSyllable("Na", "నా"), PadaSyllable("Ni", "నీ"), PadaSyllable("Nu", "నూ"), PadaSyllable("Ne", "నే"))),
        NakshatraSyllables(18, listOf(PadaSyllable("No", "నో"), PadaSyllable("Ya", "యా"), PadaSyllable("Yi", "యీ"), PadaSyllable("Yu", "యూ"))),
        NakshatraSyllables(19, listOf(PadaSyllable("Ye", "యే"), PadaSyllable("Yo", "యో"), PadaSyllable("Bha", "భా"), PadaSyllable("Bhi", "భీ"))),
        NakshatraSyllables(20, listOf(PadaSyllable("Bhu", "భూ"), PadaSyllable("Dha", "ధ"), PadaSyllable("Pha", "ఫ"), PadaSyllable("Dha", "ఢ"))),
        NakshatraSyllables(21, listOf(PadaSyllable("Bhe", "భే"), PadaSyllable("Bho", "భో"), PadaSyllable("Ja", "జా"), PadaSyllable("Ji", "జీ"))),
        NakshatraSyllables(22, listOf(PadaSyllable("Ju", "జూ"), PadaSyllable("Je", "జే"), PadaSyllable("Jo", "జో"), PadaSyllable("Gha", "ఘ"))),
        NakshatraSyllables(23, listOf(PadaSyllable("Ga", "గా"), PadaSyllable("Gi", "గీ"), PadaSyllable("Gu", "గూ"), PadaSyllable("Ge", "గే"))),
        NakshatraSyllables(24, listOf(PadaSyllable("Go", "గో"), PadaSyllable("Sa", "సా"), PadaSyllable("Si", "సీ"), PadaSyllable("Su", "సూ"))),
        NakshatraSyllables(25, listOf(PadaSyllable("Se", "సే"), PadaSyllable("So", "సో"), PadaSyllable("Da", "దా"), PadaSyllable("Di", "దీ"))),
        NakshatraSyllables(26, listOf(PadaSyllable("Du", "దూ"), PadaSyllable("Tha", "థ"), PadaSyllable("Jha", "ఝ"), PadaSyllable("Na", "ఞ"))),
        NakshatraSyllables(27, listOf(PadaSyllable("De", "దే"), PadaSyllable("Do", "దో"), PadaSyllable("Cha", "చా"), PadaSyllable("Chi", "చీ")))
    )

    fun forNakshatra(number: Int): NakshatraSyllables = all[number - 1]
}
