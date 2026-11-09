package com.example.data.model

data class City(
    val id: String,
    val nameEn: String,
    val nameTe: String,
    val stateEn: String,
    val stateTe: String,
    val latitude: Double,
    val longitude: Double
)

object CityData {
    val cities = listOf(
        City("hyd", "Hyderabad", "హైదరాబాద్", "Telangana", "తెలంగాణ", 17.3850, 78.4867),
        City("vja", "Vijayawada", "విజయవాడ", "Andhra Pradesh", "ఆంధ్రప్రదేశ్", 16.5062, 80.6480),
        City("vzg", "Visakhapatnam", "విశాఖపట్నం", "Andhra Pradesh", "ఆంధ్రప్రదేశ్", 17.6868, 83.2185),
        City("tpt", "Tirupati", "తిరుపతి", "Andhra Pradesh", "ఆంధ్రప్రదేశ్", 13.6288, 79.4192),
        City("gnt", "Guntur", "గుంటూరు", "Andhra Pradesh", "ఆంధ్రప్రదేశ్", 16.3067, 80.4365),
        City("wgl", "Warangal", "వరంగల్", "Telangana", "తెలంగాణ", 17.9689, 79.5941),
        City("nlr", "Nellore", "నెల్లూరు", "Andhra Pradesh", "ఆంధ్రప్రదేశ్", 14.4426, 79.9865),
        City("knl", "Kurnool", "కర్నూలు", "Andhra Pradesh", "ఆంధ్రప్రదేశ్", 15.8281, 78.0373),
        City("rjy", "Rajahmundry", "రాజమండ్రి", "Andhra Pradesh", "ఆంధ్రప్రదేశ్", 17.0005, 81.8040),
        City("kkd", "Kakinada", "కాకినాడ", "Andhra Pradesh", "ఆంధ్రప్రదేశ్", 16.9891, 82.2475),
        City("blr", "Bengaluru", "బెంగళూరు", "Karnataka", "కర్ణాటక", 12.9716, 77.5946),
        City("chn", "Chennai", "చెన్నై", "Tamil Nadu", "తమిళనాడు", 13.0827, 80.2707),
        City("mum", "Mumbai", "ముంబై", "Maharashtra", "మహారాష్ట్ర", 19.0760, 72.8777),
        City("del", "Delhi", "ఢిల్లీ", "Delhi NCR", "ఢిల్లీ ఎన్సీఆర్", 28.7041, 77.1025)
    )

    val defaultCity = cities.first()
}
