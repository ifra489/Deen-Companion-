package com.deencompanion.app.util


object DuaTitleGenerator {

    private val keywordMap = linkedMapOf(
        "sleep" to "Dua Before Sleeping",
        "wake" to "Dua Upon Waking Up",
        "morning" to "Morning Dua",
        "evening" to "Evening Dua",
        "confidence" to "Dua for Confidence",
        "anxiety" to "Dua for Anxiety & Worry",
        "worry" to "Dua for Anxiety & Worry",
        "fear" to "Dua for Fear & Protection",
        "protection" to "Dua for Protection",
        "forgive" to "Dua for Forgiveness",
        "sin" to "Dua for Forgiveness",
        "travel" to "Dua for Travel",
        "food" to "Dua Before Eating",
        "eating" to "Dua Before Eating",
        "rain" to "Dua for Rain",
        "exam" to "Dua for Success",
        "success" to "Dua for Success",
        "knowledge" to "Dua for Knowledge",
        "parents" to "Dua for Parents",
        "health" to "Dua for Health & Healing",
        "heal" to "Dua for Health & Healing",
        "guidance" to "Dua for Guidance",
        "sadness" to "Dua for Ease in Difficulty",
        "difficulty" to "Dua for Ease in Difficulty",
        "hardship" to "Dua for Ease in Difficulty",
        "gratitude" to "Dua of Gratitude",
        "thank" to "Dua of Gratitude",
        "entering" to "Dua for Entering a Place",
        "leaving" to "Dua for Leaving a Place",
        "mosque" to "Dua for Entering/Leaving Mosque",
        "bathroom" to "Dua for Entering/Leaving Bathroom"
    )

    fun generateTitle(englishText: String): String {
        val lowerText = englishText.lowercase()
        for ((keyword, title) in keywordMap) {
            if (lowerText.contains(keyword)) {
                return title
            }
        }
        return "Dua for Daily Life" // fallback default
    }
}