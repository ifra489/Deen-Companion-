//package com.deencompanion.app.presentation.navigation
//
///**
// * LEARNING NOTE:
// * This sealed class represents all the navigation routes within the Deen Companion application.
// * By defining our routes centrally, we prevent typos and duplicate paths.
// * Simple routes use a static string, while parameterized routes (like QuranSurah) provide a formatting function
// * to safely build path strings with arguments (e.g. "quran_surah/1").
// */
//sealed class NavRoutes(val route: String) {
//    object AppSettings : NavRoutes("app_settings")
//    // Auth Navigation Group
//    object Login : NavRoutes("login")
//    object Register : NavRoutes("register")
//    object ForgotPassword : NavRoutes("forgot_password")
//
//    // Main Tab / Core Screen Navigation Group
//    object Home : NavRoutes("home")
//    object Prayer : NavRoutes("prayer")
//    object Quran : NavRoutes("quran")
//    object Dua : NavRoutes("dua")
//    object Hadith : NavRoutes("hadith")
//    object Tasbeeh : NavRoutes("tasbeeh")
//    object Goals : NavRoutes("goals")
//    object Tracker : NavRoutes("tracker")
//    object Journal : NavRoutes("journal")
//    object Mood : NavRoutes("mood")
//    object Journey : NavRoutes("journey")
//    object Achievements : NavRoutes("achievements")
//    object Zakat : NavRoutes("zakat")
//    object HijriCalendar : NavRoutes("hijri")
//    object Settings : NavRoutes("settings")
//
//    // Detail Screen Navigation Group (With Parameterized Arguments)
//    object QuranSurah : NavRoutes("quran_surah/{surahId}") {
//        fun createRoute(surahId: Int): String = "quran_surah/$surahId"
//    }
//
//    object QuranAyah : NavRoutes("quran_ayah/{surahId}/{ayahId}") {
//        fun createRoute(surahId: Int, ayahId: Int): String = "quran_ayah/$surahId/$ayahId"
//    }
//
//    object DuaDetail : NavRoutes("dua_detail/{duaId}") {
//        fun createRoute(duaId: Int): String = "dua_detail/$duaId"
//    }
//
//    object HadithDetail : NavRoutes("hadith_detail/{hadithId}") {
//        fun createRoute(hadithId: Int): String = "hadith_detail/$hadithId"
//    }
//
//    object PrayerHistory : NavRoutes("prayer_history")
//
//    object AzkarDetail : NavRoutes("azkar_detail/{type}") {
//        fun createRoute(type: String): String = "azkar_detail/$type"
//    }
//}



package com.deencompanion.app.presentation.navigation

/**
 * LEARNING NOTE:
 * This sealed class represents all the navigation routes within the Deen Companion application.
 * By defining our routes centrally, we prevent typos and duplicate paths.
 * Simple routes use a static string, while parameterized routes (like QuranSurah) provide a formatting function
 * to safely build path strings with arguments (e.g. "quran_surah/1").
 */
sealed class NavRoutes(val route: String) {
    object AppSettings : NavRoutes("app_settings")
    // Auth Navigation Group
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object ForgotPassword : NavRoutes("forgot_password")

    // Main Tab / Core Screen Navigation Group
    object Home : NavRoutes("home")
    object Prayer : NavRoutes("prayer")
    object Quran : NavRoutes("quran")
    object Dua : NavRoutes("dua")
    object Hadith : NavRoutes("hadith")
    object Tasbeeh : NavRoutes("tasbeeh")
    object Goals : NavRoutes("goals")
    object Tracker : NavRoutes("tracker")
    object Journal : NavRoutes("journal")
    object Mood : NavRoutes("mood")
    object Journey : NavRoutes("journey")
    object Achievements : NavRoutes("achievements")
    object Zakat : NavRoutes("zakat")
    object HijriCalendar : NavRoutes("hijri")
    object Settings : NavRoutes("settings")
    object Bookmarks : NavRoutes("bookmarks")
    object Qibla : NavRoutes("qibla")

    // Detail Screen Navigation Group (With Parameterized Arguments)
    object QuranSurah : NavRoutes("quran_surah/{surahId}") {
        fun createRoute(surahId: Int): String = "quran_surah/$surahId"
    }

    object QuranAyah : NavRoutes("quran_ayah/{surahId}/{ayahId}") {
        fun createRoute(surahId: Int, ayahId: Int): String = "quran_ayah/$surahId/$ayahId"
    }

    object DuaDetail : NavRoutes("dua_detail/{duaId}") {
        fun createRoute(duaId: Int): String = "dua_detail/$duaId"
    }

    object HadithDetail : NavRoutes("hadith_detail/{hadithId}") {
        fun createRoute(hadithId: Int): String = "hadith_detail/$hadithId"
    }

    object PrayerHistory : NavRoutes("prayer_history")

    object AzkarDetail : NavRoutes("azkar_detail/{type}") {
        fun createRoute(type: String): String = "azkar_detail/$type"
    }
}