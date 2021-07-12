package io.github.camshaft54.piglatintranslator

import org.junit.jupiter.api.Test

class PigLatinTranslatorTest {
    private val testEnglishText =
        "The text you are currently reading has been generated for the sole purpose of testing. For all intents and purposes, " +
                "this text will provide metrics as to how well the translator to English and the translator to Pig Latin are performing."

    private val testPigLatinText =
        "eThay exttay youyay areyay urrentlycay eadingray ashay eenbay eneratedgay orfay ethay olesay urposepay ofyay estingtay. " +
                "orFay allyay intentsyay andyay urposespay, isthay exttay illway ovidepray etricsmay asyay otay owhay ellway " +
                "ethay anslatortray otay Englishyay andyay ethay anslatortray otay igPay atinLay areyay erformingpay."

    @Test
    fun enToPigLatinTest() {
        val pigLatin = PigLatinTranslator().enToPigLatin(testEnglishText)
        println("Received: $pigLatin")
        println("Correct: $testPigLatinText")
        assert(pigLatin == testPigLatinText)
    }

    @Test
    fun pigLatinToEnTest() {
        val english = PigLatinTranslator().pigLatinToEn(testEnglishText)
        println("Received: $english")
        println("Correct: $testEnglishText")
        assert(english == testEnglishText)
    }
}