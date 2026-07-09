package com.ObservatoireCampus.mobile.model.parking

data class ParkingStatusDto(
    val id: Long,
    val ident: String,
    val nom: String,
    val adresse: String?,
    val exploit: String?,
    val infor: String?,
    val taType: String?,
    val type: String?,          // SURFACE / SILO / ENTERRE / MIXTE
    val latitude: Double?,
    val longitude: Double?,

    // Capacités
    val npTotal: Int?,
    val npGlobal: Int?,
    val npPmr: Int?,
    val npVle: Int?,
    val npVeltot: Int?,
    val npVelec: Int?,
    val np2rmot: Int?,
    val npCovoit: Int?,

    // Tarifs horaires (th)
    val thQuar: Double?,
    val thDemi: Double?,
    val thHeur: Double?,
    val th2: Double?,
    val th3: Double?,
    val th4: Double?,
    val th10: Double?,
    val th24: Double?,
    val thNuit: Double?,

    // Tarifs abonnements (ta)
    val taTitul: Double?,
    val taNtitul: Double?,
    val taResmoi: Double?,
    val taNres7j: Double?,
    val taMoimot: Double?,
    val taMoivel: Double?,
    val taHandi: String?,

    // Caractéristiques Infra
    val anServ: String?,
    val secteur: String?,
    val propr: String?,
    val typgest: String?,
    val nbNiv: Int?,
    val gabariStd: Double?,
    val gabariMax: Double?,

    // Données dynamiques (Temps Réel)
    val etat: String?,          // OUVERT / FERME / COMPLET
    val libre: Int?,
    val totalTempsReel: Int?,
    val connecte: Boolean?,
    val mdate: String?,
    val dataFraiche: Boolean,

    val url: String?
)