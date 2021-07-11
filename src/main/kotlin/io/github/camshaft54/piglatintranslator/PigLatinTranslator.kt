package io.github.camshaft54.piglatintranslator

import org.unix4j.Unix4j
import org.unix4j.unix.grep.GrepOption
import java.io.File

val separators = listOf(" ", "-", "—", ".", ",", "\"")

/**
 * Translates English to Pig Latin.
 */
fun String.enToPigLatin(): String {
    return wordIterator(this) { word ->
        // Get indices for first letter, last letter, and first vowel for use later
        val firstLetter = word.indexOfFirst { c -> c.isLetter() }
        val lastLetter = word.indexOfLast { c -> c.isLetter() }
        val firstVowel = word.indexOfFirst { c -> "aeiouy".contains(c) }

        // If there are any letters in the word...
        if (firstLetter > -1) {
            // Check if the word starts with a vowel
            if (firstLetter != firstVowel) {
                val builder = StringBuilder()
                // Add the front section of the word that does not contain letters
                builder.append(word.substring(0, firstLetter))
                // Check if there is a vowel in the word
                if (firstVowel != -1) {
                    // If there is, move the first consonant(s) before the first vowel to the back of the word
                    builder.append(word.substring(firstVowel, lastLetter + 1) + word.substring(firstLetter, firstVowel))
                } else {
                    // If there is not, simply keep the whole word
                    builder.append(word.substring(firstLetter, lastLetter + 1))
                }
                // Add "ay" to the end of the word and the back section of the word that does not contains letters
                builder.append("ay" + word.substring(lastLetter + 1))
                return@wordIterator builder.toString()
            } else {
                // If it does not, add yay to the end of the letters section
                return@wordIterator word.substring(0, lastLetter + 1) + "yay" + word.substring(lastLetter + 1)
            }
        } else {
            // Otherwise, return the unparsed word
            return@wordIterator word
        }
    }
}

/**
 * Attempts to translate Pig Latin back to English. If a word is recognized as potentially Pig Latin but could not be
 * translated, it will have curly braces around it. Note: this translation takes longer than converting English to Pig Latin
 */
fun String.pigLatinToEn(): String {
    return wordIterator(this) { word ->
        // Get first and last letter indices
        val firstLetter = word.indexOfFirst { c -> c.isLetter() }
        val lastLetterWithAy = word.indexOfLast { c -> c.isLetter() }
        // Make sure that there are letters in the word, the word is at least three characters long, and there is an "ay" at the end of the letter section.
        if (firstLetter > -1 && word.length > 2 && word.substring(lastLetterWithAy - 1..lastLetterWithAy) == "ay") {
            // Remove the "ay"
            val translation = word.removeRange(lastLetterWithAy - 1, lastLetterWithAy + 1)
            // Get a new last letter index (since the word was modified)
            val lastLetter = translation.indexOfLast { c -> c.isLetter() }
            // Get the last vowel in the word
            val lastVowel = translation.indexOfLast { c -> "aeiouy".contains(c) }

            if (translation[lastLetter] == 'y') {
                return@wordIterator translation.dropLast(1)
            }

            val builder = StringBuilder()
            // Add the non letter content at the start
            builder.append(translation.substring(0, firstLetter))

            // If there are vowels in the word...
            if (lastVowel > -1) {
                val dictWords = listOf<String>().toMutableList()
                val capitalizedWords = listOf<String>().toMutableList()
                // loop through the indices starting at the letter after the last vowel to the last letter. This is to
                // determine how many consonants at the end should be moved to the front by checking for capitalization
                // and real words. For example, if the input word is "eirtH" (this is excluding "ay"), then possible
                // options are "Heirt", "tHeir", and "rtHei". The loop will determine the real word and capitalized
                // options: "Heirt" (capitalization), and "tHeir" (real word). Finally if multiple of these words occur,
                // it will prioritize words that are real and capitalized, then capitalized words, then real words.
                for (j in lastVowel + 1..lastLetter) {
                    // Add the letter content from the first letter to the last vowel
                    // Then add the letter content from the first consonant after the last vowel to the last letter
                    val testString =
                        translation.substring(j, lastLetter + 1) + translation.substring(firstLetter, j)
                    // Use grep to search the dictionary for complete matches with the test string. Grep is used because
                    // it is much faster than more conventional methods.
                    val searchCommand = Unix4j.cat(File("src/main/resources/englishDict.txt"))
                        .grep(GrepOption.wholeLine, testString)
                    // If there was a match in the dictionary add it to the list of dictWords.
                    if (searchCommand.toStringList().isNotEmpty()) {
                        dictWords.add(testString)
                    }

                    // If the test string is capitalized, add it to capitalized words.
                    if (testString[0].isUpperCase()) {
                        capitalizedWords.add(testString)
                    }
                }
                // Find the intersection of the two word lists
                val dictAndCapitalizedWords = dictWords.intersect(capitalizedWords)
                when {
                    dictAndCapitalizedWords.isNotEmpty() -> {
                        builder.append(dictAndCapitalizedWords.toList()[0])
                    }
                    capitalizedWords.size > 0 -> {
                        builder.append(capitalizedWords[0])
                    }
                    dictWords.size > 0 -> {
                        builder.append(dictWords[0])
                    }
                    else -> {
                        // If there were no words in any of the lists, surround the raw input string with curly braces
                        // to show that it could not be translated to English.
                        return@wordIterator "{$word}"
                    }
                }
            } else {
                // If no vowels were found, then just add the whole letter section (excluding of course the "ay")
                builder.append(translation.substring(firstLetter, lastLetter + 1))
            }
            // At any non-letter characters at the end of the input word to the output
            builder.append(translation.substring(lastLetter + 1))
            return@wordIterator builder.toString()
        } else {
            // If the word is not in pig latin, return the raw version of it
            return@wordIterator word
        }
    }
}

/**
 * Given text, this function will iterate through each word in the text and pass the word to a lambda to translate the
 * word. It will then return a string.
 */
fun wordIterator(text : String, translator : (String) -> String) : String {
    // Initialize StringBuilder to create translated text
    val mainBuilder = StringBuilder()
    // While loop iterates through each word in the text
    var i = 0
    while (i < text.length) {
        // If the current character is a separator add it to the string and continue
        if (separators.contains(text[i].toString())) {
            mainBuilder.append(text[i])
            i += 1
        } else {
            // Otherwise find the next separator and create a substring to give to to the translator
            var nextSeparator = text.substring(i + 1).indexOfFirst { c -> separators.contains(c.toString()) } + i + 1
            if (nextSeparator == i) {
                nextSeparator = text.length
            }
            val word = text.substring(i, nextSeparator)
            i = nextSeparator
            mainBuilder.append(translator(word))
        }
    }
    return mainBuilder.toString()
}