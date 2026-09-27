package com.example.data

import com.example.model.KeralaLocation

object KeralaLocationDatabase {

    val districts = listOf(
        "തിരുവനന്തപുരം" to "Thiruvananthapuram",
        "കൊല്ലം" to "Kollam",
        "പത്തനംതിട്ട" to "Pathanamthitta",
        "ആലപ്പുഴ" to "Alappuzha",
        "കോട്ടയം" to "Kottayam",
        "ഇടുക്കി" to "Idukki",
        "എറണാകുളം" to "Ernakulam",
        "തൃശ്ശൂർ" to "Thrissur",
        "പാലക്കാട്" to "Palakkad",
        "മലപ്പുറം" to "Malappuram",
        "കോഴിക്കോട്" to "Kozhikode",
        "വയനാട്" to "Wayanad",
        "കണ്ണൂർ" to "Kannur",
        "കാസർഗോഡ്" to "Kasaragod"
    )

    val allLocations: List<KeralaLocation> = listOf(
        // 1. THIRUVANANTHAPURAM (തിരുവനന്തപുരം)
        KeralaLocation("tvm_corp", "തിരുവനന്തപുരം", "Thiruvananthapuram", "തിരുവനന്തപുരം", "Thiruvananthapuram", "കോർപ്പറേഷൻ / ജില്ലാ ആസ്ഥാനം", "തിരുവനന്തപുരം", 8.5241, 76.9366),
        KeralaLocation("tvm_neyyattinkara", "നെയ്യാറ്റിൻകര", "Neyyattinkara", "തിരുവനന്തപുരം", "Thiruvananthapuram", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "നെയ്യാറ്റിൻകര", 8.4006, 77.0861),
        KeralaLocation("tvm_attingal", "ആറ്റിങ്ങൽ", "Attingal", "തിരുവനന്തപുരം", "Thiruvananthapuram", "മുനിസിപ്പാലിറ്റി", "ചിറയിൻകീഴ്", 8.6960, 76.8155),
        KeralaLocation("tvm_nedumangad", "നെടുമങ്ങാട്", "Nedumangad", "തിരുവനന്തപുരം", "Thiruvananthapuram", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "നെടുമങ്ങാട്", 8.6033, 77.0019),
        KeralaLocation("tvm_varkala", "വർക്കല", "Varkala", "തിരുവനന്തപുരം", "Thiruvananthapuram", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "വർക്കല", 8.7379, 76.7163),
        KeralaLocation("tvm_kazhakoottam", "കഴക്കൂട്ടം", "Kazhakoottam", "തിരുവനന്തപുരം", "Thiruvananthapuram", "പട്ടണം / മേഖല", "തിരുവനന്തപുരം", 8.5665, 76.8732),
        KeralaLocation("tvm_kattakada", "കാട്ടാക്കട", "Kattakada", "തിരുവനന്തപുരം", "Thiruvananthapuram", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "കാട്ടാക്കട", 8.5042, 77.0811),
        KeralaLocation("tvm_chirayinkeezhu", "ചിറയിൻകീഴ്", "Chirayinkeezhu", "തിരുവനന്തപുരം", "Thiruvananthapuram", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "ചിറയിൻകീഴ്", 8.6625, 76.7836),
        KeralaLocation("tvm_kovalam", "കോവളം", "Kovalam", "തിരുവനന്തപുരം", "Thiruvananthapuram", "പട്ടണം / തീരദേശം", "തിരുവനന്തപുരം", 8.4004, 76.9787),
        KeralaLocation("tvm_parassala", "പാറശ്ശാല", "Parassala", "തിരുവനന്തപുരം", "Thiruvananthapuram", "ഗ്രാമപഞ്ചായത്ത്", "നെയ്യാറ്റിൻകര", 8.3340, 77.1520),
        KeralaLocation("tvm_kilimanoor", "കിളിമാനൂർ", "Kilimanoor", "തിരുവനന്തപുരം", "Thiruvananthapuram", "ഗ്രാമപഞ്ചായത്ത്", "ചിറയിൻകീഴ്", 8.7697, 76.8797),
        KeralaLocation("tvm_balaramapuram", "ബാലരാമപുരം", "Balaramapuram", "തിരുവനന്തപുരം", "Thiruvananthapuram", "ഗ്രാമപഞ്ചായത്ത്", "നെയ്യാറ്റിൻകര", 8.4278, 77.0439),
        KeralaLocation("tvm_venjaramoodu", "വെഞ്ഞാറമൂട്", "Venjaramoodu", "തിരുവനന്തപുരം", "Thiruvananthapuram", "പട്ടണം / പഞ്ചായത്ത്", "നെടുമങ്ങാട്", 8.6817, 76.9089),
        KeralaLocation("tvm_aryanad", "ആര്യനാട്", "Aryanad", "തിരുവനന്തപുരം", "Thiruvananthapuram", "ഗ്രാമപഞ്ചായത്ത്", "നെടുമങ്ങാട്", 8.5794, 77.0886),

        // 2. KOLLAM (കൊല്ലം)
        KeralaLocation("klm_corp", "കൊല്ലം", "Kollam", "കൊല്ലം", "Kollam", "കോർപ്പറേഷൻ / ജില്ലാ ആസ്ഥാനം", "കൊല്ലം", 8.8932, 76.6141),
        KeralaLocation("klm_karunagappally", "കരുനാഗപ്പള്ളി", "Karunagappally", "കൊല്ലം", "Kollam", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "കരുനാഗപ്പള്ളി", 9.0592, 76.5356),
        KeralaLocation("klm_punalur", "പുനലൂർ", "Punalur", "കൊല്ലം", "Kollam", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "പുനലൂർ", 9.0175, 76.9264),
        KeralaLocation("klm_paravur", "പരവൂർ", "Paravur", "കൊല്ലം", "Kollam", "മുനിസിപ്പാലിറ്റി", "കൊല്ലം", 8.8130, 76.6694),
        KeralaLocation("klm_kottarakkara", "കൊട്ടാരക്കര", "Kottarakkara", "കൊല്ലം", "Kollam", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "കൊട്ടാരക്കര", 9.0005, 76.7746),
        KeralaLocation("klm_kunnathur", "കുന്നത്തൂർ (ശാസ്താംകോട്ട)", "Kunnathur (Sasthamkotta)", "കൊല്ലം", "Kollam", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "കുന്നത്തൂർ", 9.0419, 76.6283),
        KeralaLocation("klm_pathanapuram", "പത്തനാപുരം", "Pathanapuram", "കൊല്ലം", "Kollam", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "പത്തനാപുരം", 9.0928, 76.8608),
        KeralaLocation("klm_anchal", "അഞ്ചൽ", "Anchal", "കൊല്ലം", "Kollam", "ഗ്രാമപഞ്ചായത്ത്", "പുനലൂർ", 8.9261, 76.9125),
        KeralaLocation("klm_chadayamangalam", "ചടയമംഗലം", "Chadayamangalam", "കൊല്ലം", "Kollam", "ഗ്രാമപഞ്ചായത്ത്", "കൊട്ടാരക്കര", 8.8744, 76.8686),
        KeralaLocation("klm_chavara", "ചവറ", "Chavara", "കൊല്ലം", "Kollam", "ഗ്രാമപഞ്ചായത്ത്", "കരുനാഗപ്പള്ളി", 8.9906, 76.5389),
        KeralaLocation("klm_ochira", "ഓച്ചിറ", "Oachira", "കൊല്ലം", "Kollam", "ഗ്രാമപഞ്ചായത്ത്", "കരുനാഗപ്പള്ളി", 9.1297, 76.5092),
        KeralaLocation("klm_kundara", "കുണ്ടറ", "Kundara", "കൊല്ലം", "Kollam", "ഗ്രാമപഞ്ചായത്ത്", "കൊല്ലം", 8.9617, 76.6808),

        // 3. PATHANAMTHITTA (പത്തനംതിട്ട)
        KeralaLocation("pta_hq", "പത്തനംതിട്ട", "Pathanamthitta", "പത്തനംതിട്ട", "Pathanamthitta", "മുനിസിപ്പാലിറ്റി / ജില്ലാ ആസ്ഥാനം", "കോഴഞ്ചേരി", 9.2648, 76.7870),
        KeralaLocation("pta_pandalam", "പന്തളം", "Pandalam", "പത്തനംതിട്ട", "Pathanamthitta", "മുനിസിപ്പാലിറ്റി", "അടൂർ", 9.2250, 76.6784),
        KeralaLocation("pta_adoor", "അടൂർ", "Adoor", "പത്തനംതിട്ട", "Pathanamthitta", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "അടൂർ", 9.1529, 76.7356),
        KeralaLocation("pta_thiruvalla", "തിരുവല്ല", "Thiruvalla", "പത്തനംതിട്ട", "Pathanamthitta", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "തിരുവല്ല", 9.3853, 76.5750),
        KeralaLocation("pta_ranni", "റാന്നി", "Ranni", "പത്തനംതിട്ട", "Pathanamthitta", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "റാന്നി", 9.3833, 76.7833),
        KeralaLocation("pta_konni", "കോന്നി", "Konni", "പത്തനംതിട്ട", "Pathanamthitta", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "കോന്നി", 9.2292, 76.8467),
        KeralaLocation("pta_mallappally", "മല്ലപ്പള്ളി", "Mallappally", "പത്തനംതിട്ട", "Pathanamthitta", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "മല്ലപ്പള്ളി", 9.4422, 76.6669),
        KeralaLocation("pta_kozhinjampara", "കോഴഞ്ചേരി", "Kozhencherry", "പത്തനംതിട്ട", "Pathanamthitta", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "കോഴഞ്ചേരി", 9.3328, 76.7089),
        KeralaLocation("pta_aranmula", "ആറന്മുള", "Aranmula", "പത്തനംതിട്ട", "Pathanamthitta", "ഗ്രാമപഞ്ചായത്ത്", "കോഴഞ്ചേരി", 9.3275, 76.6872),
        KeralaLocation("pta_sabarimala", "ശബരിമല", "Sabarimala", "പത്തനംതിട്ട", "Pathanamthitta", "പുണ്യകേന്ദ്രം / പെരുനാട് പഞ്ചായത്ത്", "റാന്നി", 9.4328, 77.0815),
        KeralaLocation("pta_erumely_border", "സീതത്തോട്", "Seethathodu", "പത്തനംതിട്ട", "Pathanamthitta", "ഗ്രാമപഞ്ചായത്ത്", "കോന്നി", 9.3247, 76.9694),

        // 4. ALAPPUZHA (ആലപ്പുഴ)
        KeralaLocation("alp_hq", "ആലപ്പുഴ", "Alappuzha", "ആലപ്പുഴ", "Alappuzha", "മുനിസിപ്പാലിറ്റി / ജില്ലാ ആസ്ഥാനം", "അമ്പലപ്പുഴ", 9.4981, 76.3388),
        KeralaLocation("alp_chengannur", "ചെങ്ങന്നൂർ", "Chengannur", "ആലപ്പുഴ", "Alappuzha", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "ചെങ്ങന്നൂർ", 9.3183, 76.6152),
        KeralaLocation("alp_cherthala", "ചേർത്തല", "Cherthala", "ആലപ്പുഴ", "Alappuzha", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "ചേർത്തല", 9.6844, 76.3364),
        KeralaLocation("alp_kayamkulam", "കായംകുളം", "Kayamkulam", "ആലപ്പുഴ", "Alappuzha", "മുനിസിപ്പാലിറ്റി", "കാർത്തികപ്പള്ളി", 9.1748, 76.5009),
        KeralaLocation("alp_mavelikkara", "മാവേലിക്കര", "Mavelikkara", "ആലപ്പുഴ", "Alappuzha", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "മാവേലിക്കര", 9.2471, 76.5446),
        KeralaLocation("alp_haripad", "ഹരിപ്പാട്", "Haripad", "ആലപ്പുഴ", "Alappuzha", "മുനിസിപ്പാലിറ്റി / കാർത്തികപ്പള്ളി താലൂക്ക്", "കാർത്തികപ്പള്ളി", 9.2805, 76.4561),
        KeralaLocation("alp_ambalappuzha", "അമ്പലപ്പുഴ", "Ambalappuzha", "ആലപ്പുഴ", "Alappuzha", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "അമ്പലപ്പുഴ", 9.3834, 76.3578),
        KeralaLocation("alp_kuttanad", "കുട്ടനാട് (മങ്കൊമ്പ്)", "Kuttanad (Mankombu)", "ആലപ്പുഴ", "Alappuzha", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "കുട്ടനാട്", 9.4319, 76.4253),
        KeralaLocation("alp_mannarasala", "മണ്ണാറശ്ശാല", "Mannarasala", "ആലപ്പുഴ", "Alappuzha", "പ്രദേശം / ഹരിപ്പാട്", "കാർത്തികപ്പള്ളി", 9.2889, 76.4450),
        KeralaLocation("alp_arooor", "അരൂർ", "Aroor", "ആലപ്പുഴ", "Alappuzha", "ഗ്രാമപഞ്ചായത്ത്", "ചേർത്തല", 9.8736, 76.3061),

        // 5. KOTTAYAM (കോട്ടയം)
        KeralaLocation("ktm_hq", "കോട്ടയം", "Kottayam", "കോട്ടയം", "Kottayam", "മുനിസിപ്പാലിറ്റി / ജില്ലാ ആസ്ഥാനം", "കോട്ടയം", 9.5916, 76.5222),
        KeralaLocation("ktm_changanassery", "ചങ്ങനാശ്ശേരി", "Changanassery", "കോട്ടയം", "Kottayam", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "ചങ്ങനാശ്ശേരി", 9.4456, 76.5410),
        KeralaLocation("ktm_pala", "പാലാ", "Pala", "കോട്ടയം", "Kottayam", "മുനിസിപ്പാലിറ്റി / മീനച്ചിൽ താലൂക്ക്", "മീനച്ചിൽ", 9.7138, 76.6829),
        KeralaLocation("ktm_vaikom", "വൈക്കം", "Vaikom", "കോട്ടയം", "Kottayam", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "വൈക്കം", 9.7488, 76.3929),
        KeralaLocation("ktm_ettumanoor", "ഏറ്റുമാനൂർ", "Ettumanoor", "കോട്ടയം", "Kottayam", "മുനിസിപ്പാലിറ്റി", "കോട്ടയം", 9.6700, 76.5606),
        KeralaLocation("ktm_erattupetta", "ഈരാറ്റുപേട്ട", "Erattupetta", "കോട്ടയം", "Kottayam", "മുനിസിപ്പാലിറ്റി", "മീനച്ചിൽ", 9.6879, 76.7766),
        KeralaLocation("ktm_kanjirappally", "കാഞ്ഞിരപ്പള്ളി", "Kanjirappally", "കോട്ടയം", "Kottayam", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "കാഞ്ഞിരപ്പള്ളി", 9.5572, 76.7894),
        KeralaLocation("ktm_kumarakom", "കുമരകം", "Kumarakom", "കോട്ടയം", "Kottayam", "ഗ്രാമപഞ്ചായത്ത്", "കോട്ടയം", 9.6175, 76.4301),
        KeralaLocation("ktm_erumely", "എരുമേലി", "Erumely", "കോട്ടയം", "Kottayam", "ഗ്രാമപഞ്ചായത്ത്", "കാഞ്ഞിരപ്പള്ളി", 9.4800, 76.8458),
        KeralaLocation("ktm_ponkunnam", "പൊൻകുന്നം", "Ponkunnam", "കോട്ടയം", "Kottayam", "പട്ടണം / ചിറക്കടവ് പഞ്ചായത്ത്", "കാഞ്ഞിരപ്പള്ളി", 9.5661, 76.7575),

        // 6. IDUKKI (ഇടുക്കി)
        KeralaLocation("idk_painavu", "ഇടുക്കി (പൈനാവ്)", "Idukki (Painavu)", "ഇടുക്കി", "Idukki", "ജില്ലാ ആസ്ഥാനം / താലൂക്ക്", "ഇടുക്കി", 9.8497, 76.9425),
        KeralaLocation("idk_thodupuzha", "തൊടുപുഴ", "Thodupuzha", "ഇടുക്കി", "Idukki", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "തൊടുപുഴ", 9.8959, 76.7184),
        KeralaLocation("idk_kattappana", "കട്ടപ്പന", "Kattappana", "ഇടുക്കി", "Idukki", "മുനിസിപ്പാലിറ്റി", "ഇടുക്കി", 9.7558, 77.1169),
        KeralaLocation("idk_munnar", "മൂന്നാർ", "Munnar", "ഇടുക്കി", "Idukki", "ഗ്രാമപഞ്ചായത്ത് / ദേവികുളം താലൂക്ക്", "ദേവികുളം", 10.0889, 77.0595),
        KeralaLocation("idk_devikulam", "ദേവികുളം", "Devikulam", "ഇടുക്കി", "Idukki", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "ദേവികുളം", 10.0628, 77.1040),
        KeralaLocation("idk_peermade", "പീരുമേട്", "Peermade", "ഇടുക്കി", "Idukki", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "പീരുമേട്", 9.5667, 76.9833),
        KeralaLocation("idk_udumbanchola", "ഉടുമ്പൻചോല (നെടുങ്കണ്ടം)", "Udumbanchola (Nedumkandam)", "ഇടുക്കി", "Idukki", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "ഉടുമ്പൻചോല", 9.8394, 77.1561),
        KeralaLocation("idk_adimali", "അടിമാലി", "Adimali", "ഇടുക്കി", "Idukki", "ഗ്രാമപഞ്ചായത്ത്", "ദേവികുളം", 10.0119, 76.9539),
        KeralaLocation("idk_kumily", "കുമളി", "Kumily", "ഇടുക്കി", "Idukki", "ഗ്രാമപഞ്ചായത്ത്", "പീരുമേട്", 9.6046, 77.1670),

        // 7. ERNAKULAM (എറണാകുളം)
        KeralaLocation("ekm_kochi", "കൊച്ചി (എറണാകുളം)", "Kochi (Ernakulam)", "എറണാകുളം", "Ernakulam", "കോർപ്പറേഷൻ / ജില്ലാ ആസ്ഥാനം", "കണയന്നൂർ", 9.9312, 76.2673),
        KeralaLocation("ekm_aluva", "ആലുവ", "Aluva", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "ആലുവ", 10.1076, 76.3516),
        KeralaLocation("ekm_angamaly", "അങ്കമാലി", "Angamaly", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി", "ആലുവ", 10.1960, 76.3860),
        KeralaLocation("ekm_perumbavoor", "പെരുമ്പാവൂർ", "Perumbavoor", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി / കുന്നത്തുനാട് താലൂക്ക്", "കുന്നത്തുനാട്", 10.1154, 76.4760),
        KeralaLocation("ekm_muvattupuzha", "മൂവാറ്റുപുഴ", "Muvattupuzha", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "മൂവാറ്റുപുഴ", 9.9894, 76.5790),
        KeralaLocation("ekm_kothamangalam", "കോതമംഗലം", "Kothamangalam", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "കോതമംഗലം", 10.0602, 76.6353),
        KeralaLocation("ekm_north_paravur", "വടക്കൻ പറവൂർ", "North Paravur", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "പറവൂർ", 10.1472, 76.2289),
        KeralaLocation("ekm_thrippunithura", "തൃപ്പൂണിത്തുറ", "Thrippunithura", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി", "കണയന്നൂർ", 9.9486, 76.3464),
        KeralaLocation("ekm_kalamassery", "കളമശ്ശേരി", "Kalamassery", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി", "കണയന്നൂർ", 10.0531, 76.3228),
        KeralaLocation("ekm_thrikkakara", "തൃക്കാക്കര (കാക്കനാട്)", "Thrikkakara (Kakkanad)", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി", "കണയന്നൂർ", 10.0159, 76.3419),
        KeralaLocation("ekm_maradu", "മരട്", "Maradu", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി", "കണയന്നൂർ", 9.9408, 76.3219),
        KeralaLocation("ekm_piravom", "പിറവം", "Piravom", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി", "മൂവാറ്റുപുഴ", 9.8732, 76.4922),
        KeralaLocation("ekm_koothattukulam", "കൂത്താട്ടുകുളം", "Koothattukulam", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി", "മൂവാറ്റുപുഴ", 9.8631, 76.5944),
        KeralaLocation("ekm_eloor", "ഏലൂർ", "Eloor", "എറണാകുളം", "Ernakulam", "മുനിസിപ്പാലിറ്റി", "പറവൂർ", 10.0725, 76.2878),
        KeralaLocation("ekm_kalady", "കാലടി", "Kalady", "എറണാകുളം", "Ernakulam", "ഗ്രാമപഞ്ചായത്ത്", "ആലുവ", 10.1683, 76.4408),
        KeralaLocation("ekm_chottanikkara", "ചോറ്റാനിക്കര", "Chottanikkara", "എറണാകുളം", "Ernakulam", "ഗ്രാമപഞ്ചായത്ത്", "കണയന്നൂർ", 9.9325, 76.3908),

        // 8. THRISSUR (തൃശ്ശൂർ)
        KeralaLocation("tsr_corp", "തൃശ്ശൂർ", "Thrissur", "തൃശ്ശൂർ", "Thrissur", "കോർപ്പറേഷൻ / ജില്ലാ ആസ്ഥാനം", "തൃശ്ശൂർ", 10.5276, 76.2144),
        KeralaLocation("tsr_guruvayur", "ഗുരുവായൂർ", "Guruvayur", "തൃശ്ശൂർ", "Thrissur", "മുനിസിപ്പാലിറ്റി", "ചാവക്കാട്", 10.5946, 76.0411),
        KeralaLocation("tsr_chalakudy", "ചാലക്കുടി", "Chalakudy", "തൃശ്ശൂർ", "Thrissur", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "ചാലക്കുടി", 10.3007, 76.3361),
        KeralaLocation("tsr_irinjalakuda", "ഇരിങ്ങാലക്കുട", "Irinjalakuda", "തൃശ്ശൂർ", "Thrissur", "മുനിസിപ്പാലിറ്റി / മുകുന്ദപുരം താലൂക്ക്", "മുകുന്ദപുരം", 10.3444, 76.2111),
        KeralaLocation("tsr_kodungallur", "കൊടുങ്ങല്ലൂർ", "Kodungallur", "തൃശ്ശൂർ", "Thrissur", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "കൊടുങ്ങല്ലൂർ", 10.2217, 76.1972),
        KeralaLocation("tsr_kunnamkulam", "കുന്നംകുളം", "Kunnamkulam", "തൃശ്ശൂർ", "Thrissur", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "കുന്നംകുളം", 10.6508, 76.0694),
        KeralaLocation("tsr_chavakkad", "ചാവക്കാട്", "Chavakkad", "തൃശ്ശൂർ", "Thrissur", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "ചാവക്കാട്", 10.5836, 76.0189),
        KeralaLocation("tsr_wadakkanchery", "വടക്കാഞ്ചേരി", "Wadakkanchery", "തൃശ്ശൂർ", "Thrissur", "മുനിസിപ്പാലിറ്റി / തലപ്പിള്ളി താലൂക്ക്", "തലപ്പിള്ളി", 10.6628, 76.2458),
        KeralaLocation("tsr_thriprayar", "തൃപ്രയാർ", "Thriprayar", "തൃശ്ശൂർ", "Thrissur", "ഗ്രാമപഞ്ചായത്ത് (നാട്ടിക)", "ചാവക്കാട്", 10.4222, 76.1075),
        KeralaLocation("tsr_cheruthuruthy", "ചെറുതുരുത്തി", "Cheruthuruthy", "തൃശ്ശൂർ", "Thrissur", "ഗ്രാമപഞ്ചായത്ത് (വള്ളത്തോൾ നഗർ)", "തലപ്പിള്ളി", 10.7469, 76.2789),

        // 9. PALAKKAD (പാലക്കാട്)
        KeralaLocation("pkd_hq", "പാലക്കാട്", "Palakkad", "പാലക്കാട്", "Palakkad", "മുനിസിപ്പാലിറ്റി / ജില്ലാ ആസ്ഥാനം", "പാലക്കാട്", 10.7867, 76.6548),
        KeralaLocation("pkd_ottapalam", "ഒറ്റപ്പാലം", "Ottapalam", "പാലക്കാട്", "Palakkad", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "ഒറ്റപ്പാലം", 10.7712, 76.3775),
        KeralaLocation("pkd_shoranur", "ഷൊർണ്ണൂർ", "Shoranur", "പാലക്കാട്", "Palakkad", "മുനിസിപ്പാലിറ്റി", "ഒറ്റപ്പാലം", 10.7618, 76.2711),
        KeralaLocation("pkd_chittur", "ചിറ്റൂർ-തത്തമംഗലം", "Chittur-Thathamangalam", "പാലക്കാട്", "Palakkad", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "ചിറ്റൂർ", 10.6997, 76.7408),
        KeralaLocation("pkd_pattambi", "പട്ടാമ്പി", "Pattambi", "പാലക്കാട്", "Palakkad", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "പട്ടാമ്പി", 10.8058, 76.1856),
        KeralaLocation("pkd_mannarkkad", "മണ്ണാർക്കാട്", "Mannarkkad", "പാലക്കാട്", "Palakkad", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "മണ്ണാർക്കാട്", 10.9931, 76.4561),
        KeralaLocation("pkd_cherpulassery", "ചെർപ്പുളശ്ശേരി", "Cherpulassery", "പാലക്കാട്", "Palakkad", "മുനിസിപ്പാലിറ്റി", "ഒറ്റപ്പാലം", 10.8797, 76.3133),
        KeralaLocation("pkd_alathur", "ആലത്തൂർ", "Alathur", "പാലക്കാട്", "Palakkad", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "ആലത്തൂർ", 10.6458, 76.5453),
        KeralaLocation("pkd_kollengode", "കൊല്ലങ്കോട്", "Kollengode", "പാലക്കാട്", "Palakkad", "ഗ്രാമപഞ്ചായത്ത്", "ചിറ്റൂർ", 10.6186, 76.6908),
        KeralaLocation("pkd_attappady", "അട്ടപ്പാടി (അഗളി)", "Attappady (Agali)", "പാലക്കാട്", "Palakkad", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "അട്ടപ്പാടി", 11.1167, 76.6500),

        // 10. MALAPPURAM (മലപ്പുറം)
        KeralaLocation("mlp_hq", "മലപ്പുറം", "Malappuram", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി / ജില്ലാ ആസ്ഥാനം", "ഏറനാട്", 11.0510, 76.0711),
        KeralaLocation("mlp_manjeri", "മഞ്ചേരി", "Manjeri", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി / ഏറനാട് താലൂക്ക്", "ഏറനാട്", 11.1194, 76.1219),
        KeralaLocation("mlp_perinthalmanna", "പെരിന്തൽമണ്ണ", "Perinthalmanna", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "പെരിന്തൽമണ്ണ", 10.9760, 76.2254),
        KeralaLocation("mlp_ponnani", "പൊന്നാനി", "Ponnani", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "പൊന്നാനി", 10.7677, 75.9252),
        KeralaLocation("mlp_tirur", "തിരൂർ", "Tirur", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "തിരൂർ", 10.9167, 75.9242),
        KeralaLocation("mlp_nilambur", "നിലമ്പൂർ", "Nilambur", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "നിലമ്പൂർ", 11.2756, 76.2253),
        KeralaLocation("mlp_kottakkal", "കോട്ടയ്ക്കൽ", "Kottakkal", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി", "തിരൂർ", 11.0003, 76.0047),
        KeralaLocation("mlp_kondotty", "കൊണ്ടോട്ടി", "Kondotty", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "കൊണ്ടോട്ടി", 11.1444, 75.9631),
        KeralaLocation("mlp_tirurangadi", "തിരൂരങ്ങാടി", "Tirurangadi", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "തിരൂരങ്ങാടി", 11.0439, 75.9258),
        KeralaLocation("mlp_tanur", "താനൂർ", "Tanur", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി", "തിരൂർ", 10.9789, 75.8753),
        KeralaLocation("mlp_parappanangadi", "പരപ്പനങ്ങാടി", "Parappanangadi", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി", "തിരൂരങ്ങാടി", 11.0531, 75.8628),
        KeralaLocation("mlp_valanchery", "വളാഞ്ചേരി", "Valanchery", "മലപ്പുറം", "Malappuram", "മുനിസിപ്പാലിറ്റി", "തിരൂർ", 10.8878, 76.0736),

        // 11. KOZHIKODE (കോഴിക്കോട്)
        KeralaLocation("kkd_corp", "കോഴിക്കോട്", "Kozhikode", "കോഴിക്കോട്", "Kozhikode", "കോർപ്പറേഷൻ / ജില്ലാ ആസ്ഥാനം", "കോഴിക്കോട്", 11.2588, 75.7804),
        KeralaLocation("kkd_vadakara", "വടകര", "Vadakara", "കോഴിക്കോട്", "Kozhikode", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "വടകര", 11.6086, 75.5917),
        KeralaLocation("kkd_koyilandy", "കൊയിലാണ്ടി", "Koyilandy", "കോഴിക്കോട്", "Kozhikode", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "കൊയിലാണ്ടി", 11.4429, 75.6976),
        KeralaLocation("kkd_feroke", "ഫറോക്ക്", "Feroke", "കോഴിക്കോട്", "Kozhikode", "മുനിസിപ്പാലിറ്റി", "കോഴിക്കോട്", 11.1833, 75.8333),
        KeralaLocation("kkd_payyoli", "പയ്യോളി", "Payyoli", "കോഴിക്കോട്", "Kozhikode", "മുനിസിപ്പാലിറ്റി", "കൊയിലാണ്ടി", 11.5175, 75.6228),
        KeralaLocation("kkd_ramനാട്ടുകര", "രാമനാട്ടുകര", "Ramanattukara", "കോഴിക്കോട്", "Kozhikode", "മുനിസിപ്പാലിറ്റി", "കോഴിക്കോട്", 11.1783, 75.8658),
        KeralaLocation("kkd_koduvally", "കൊടുവള്ളി", "Koduvally", "കോഴിക്കോട്", "Kozhikode", "മുനിസിപ്പാലിറ്റി", "താമരശ്ശേരി", 11.3592, 75.9103),
        KeralaLocation("kkd_mukkam", "മുക്കം", "Mukkam", "കോഴിക്കോട്", "Kozhikode", "മുനിസിപ്പാലിറ്റി", "താമരശ്ശേരി", 11.3217, 75.9961),
        KeralaLocation("kkd_thamarassery", "താമരശ്ശേരി", "Thamarassery", "കോഴിക്കോട്", "Kozhikode", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "താമരശ്ശേരി", 11.4142, 75.9369),
        KeralaLocation("kkd_balussery", "ബാലുശ്ശേരി", "Balussery", "കോഴിക്കോട്", "Kozhikode", "ഗ്രാമപഞ്ചായത്ത്", "കൊയിലാണ്ടി", 11.4453, 75.8258),
        KeralaLocation("kkd_perambra", "പേരാമ്പ്ര", "Perambra", "കോഴിക്കോട്", "Kozhikode", "ഗ്രാമപഞ്ചായത്ത്", "കൊയിലാണ്ടി", 11.5608, 75.7564),

        // 12. WAYANAD (വയനാട്)
        KeralaLocation("wyd_kalpetta", "കൽപ്പറ്റ (വയനാട്)", "Kalpetta (Wayanad)", "വയനാട്", "Wayanad", "മുനിസിപ്പാലിറ്റി / ജില്ലാ ആസ്ഥാനം", "വൈത്തിരി", 11.6103, 76.0828),
        KeralaLocation("wyd_sulthan_bathery", "സുൽത്താൻ ബത്തേരി", "Sulthan Bathery", "വയനാട്", "Wayanad", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "സുൽത്താൻ ബത്തേരി", 11.6629, 76.2570),
        KeralaLocation("wyd_mananthavady", "മാനന്തവാടി", "Mananthavady", "വയനാട്", "Wayanad", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "മാനന്തവാടി", 11.8014, 76.0044),
        KeralaLocation("wyd_vythiri", "വൈത്തിരി", "Vythiri", "വയനാട്", "Wayanad", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "വൈത്തിരി", 11.5517, 76.0403),
        KeralaLocation("wyd_pulpally", "പുൽപ്പള്ളി", "Pulpally", "വയനാട്", "Wayanad", "ഗ്രാമപഞ്ചായത്ത്", "സുൽത്താൻ ബത്തേരി", 11.7922, 76.1667),
        KeralaLocation("wyd_meenangadi", "മീനങ്ങാടി", "Meenangadi", "വയനാട്", "Wayanad", "ഗ്രാമപഞ്ചായത്ത്", "സുൽത്താൻ ബത്തേരി", 11.6572, 76.1714),
        KeralaLocation("wyd_thirunelli", "തിരുനെല്ലി", "Thirunelli", "വയനാട്", "Wayanad", "ഗ്രാമപഞ്ചായത്ത്", "മാനന്തവാടി", 11.9108, 76.0006),

        // 13. KANNUR (കണ്ണൂർ)
        KeralaLocation("knr_corp", "കണ്ണൂർ", "Kannur", "കണ്ണൂർ", "Kannur", "കോർപ്പറേഷൻ / ജില്ലാ ആസ്ഥാനം", "കണ്ണൂർ", 11.8745, 75.3704),
        KeralaLocation("knr_thalassery", "തലശ്ശേരി", "Thalassery", "കണ്ണൂർ", "Kannur", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "തലശ്ശേരി", 11.7491, 75.4890),
        KeralaLocation("knr_payyanur", "പയ്യന്നൂർ", "Payyanur", "കണ്ണൂർ", "Kannur", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "പയ്യന്നൂർ", 12.0977, 75.1934),
        KeralaLocation("knr_taliparamba", "തളിപ്പറമ്പ്", "Taliparamba", "കണ്ണൂർ", "Kannur", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "തളിപ്പറമ്പ്", 12.0402, 75.3596),
        KeralaLocation("knr_mattannur", "മട്ടന്നൂർ", "Mattannur", "കണ്ണൂർ", "Kannur", "മുനിസിപ്പാലിറ്റി", "ഇരിട്ടി", 11.9289, 75.5725),
        KeralaLocation("knr_iritty", "ഇരിട്ടി", "Iritty", "കണ്ണൂർ", "Kannur", "മുനിസിപ്പാലിറ്റി / താലൂക്ക്", "ഇരിട്ടി", 11.9822, 75.6725),
        KeralaLocation("knr_koothuparamba", "കൂത്തുപറമ്പ്", "Koothuparamba", "കണ്ണൂർ", "Kannur", "മുനിസിപ്പാലിറ്റി", "തലശ്ശേരി", 11.8294, 75.5653),
        KeralaLocation("knr_anthoor", "ആന്തൂർ", "Anthoor", "കണ്ണൂർ", "Kannur", "മുനിസിപ്പാലിറ്റി", "തളിപ്പറമ്പ്", 11.9922, 75.3669),
        KeralaLocation("knr_panoor", "പാനൂർ", "Panoor", "കണ്ണൂർ", "Kannur", "മുനിസിപ്പാലിറ്റി", "തലശ്ശേരി", 11.7625, 75.5764),
        KeralaLocation("knr_sreekandapuram", "ശ്രീകണ്ഠാപുരം", "Sreekandapuram", "കണ്ണൂർ", "Kannur", "മുനിസിപ്പാലിറ്റി", "തളിപ്പറമ്പ്", 12.0417, 75.5067),

        // 14. KASARAGOD (കാസർഗോഡ്)
        KeralaLocation("ksd_hq", "കാസർഗോഡ്", "Kasaragod", "കാസർഗോഡ്", "Kasaragod", "മുനിസിപ്പാലിറ്റി / ജില്ലാ ആസ്ഥാനം", "കാസർഗോഡ്", 12.4996, 74.9869),
        KeralaLocation("ksd_kanhangad", "കാഞ്ഞങ്ങാട്", "Kanhangad", "കാസർഗോഡ്", "Kasaragod", "മുനിസിപ്പാലിറ്റി / ഹോസ്ദുർഗ് താലൂക്ക്", "ഹോസ്ദുർഗ്", 12.3315, 75.0916),
        KeralaLocation("ksd_nileshwaram", "നീലേശ്വരം", "Nileshwaram", "കാസർഗോഡ്", "Kasaragod", "മുനിസിപ്പാലിറ്റി", "ഹോസ്ദുർഗ്", 12.2570, 75.1333),
        KeralaLocation("ksd_manjeshwaram", "മഞ്ചേശ്വരം", "Manjeshwaram", "കാസർഗോഡ്", "Kasaragod", "താലൂക്ക് / ഗ്രാമപഞ്ചായത്ത്", "മഞ്ചേശ്വരം", 12.7119, 74.8886),
        KeralaLocation("ksd_vellarikundu", "വെള്ളരിക്കുണ്ട്", "Vellarikundu", "കാസർഗോഡ്", "Kasaragod", "താലൂക്ക് / ബളാൽ പഞ്ചായത്ത്", "വെള്ളരിക്കുണ്ട്", 12.3783, 75.3017),
        KeralaLocation("ksd_bekal", "ബേക്കൽ (പള്ളിക്കര)", "Bekal (Pallikkara)", "കാസർഗോഡ്", "Kasaragod", "ഗ്രാമപഞ്ചായത്ത്", "ഹോസ്ദുർഗ്", 12.3929, 75.0334),
        KeralaLocation("ksd_cheruvathur", "ചെറുവത്തൂർ", "Cheruvathur", "കാസർഗോഡ്", "Kasaragod", "ഗ്രാമപഞ്ചായത്ത്", "ഹോസ്ദുർഗ്", 12.2156, 75.1628),
        KeralaLocation("ksd_uppla", "ഉപ്പള", "Uppala", "കാസർഗോഡ്", "Kasaragod", "പട്ടണം / മംഗൽപാടി പഞ്ചായത്ത്", "മഞ്ചേശ്വരം", 12.6775, 74.9061),
        KeralaLocation("ksd_kumbla", "കുമ്പള", "Kumbla", "കാസർഗോഡ്", "Kasaragod", "ഗ്രാമപഞ്ചായത്ത്", "കാസർഗോഡ്", 12.5936, 74.9458)
    )

    fun searchLocations(query: String, districtFilter: String? = null): List<KeralaLocation> {
        val trimmed = query.trim().lowercase()
        return allLocations.filter { loc ->
            val matchesDistrict = districtFilter.isNullOrBlank() ||
                loc.districtMalayalam == districtFilter ||
                loc.districtEnglish.equals(districtFilter, ignoreCase = true)
            if (!matchesDistrict) return@filter false

            if (trimmed.isEmpty()) return@filter true

            loc.nameMalayalam.lowercase().contains(trimmed) ||
                loc.nameEnglish.lowercase().contains(trimmed) ||
                loc.districtMalayalam.lowercase().contains(trimmed) ||
                loc.districtEnglish.lowercase().contains(trimmed) ||
                loc.talukMalayalam.lowercase().contains(trimmed) ||
                loc.categoryMalayalam.lowercase().contains(trimmed)
        }
    }
}
