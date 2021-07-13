# PigLatinTranslator
[![Maven Central](https://img.shields.io/maven-central/v/io.github.camshaft54/PigLatinTranslator.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.camshaft54%22%20AND%20a:%22PigLatinTranslator%22)

A simple program that translates English to Pig Latin and Pig Latin to English (or at least tries to). This project is written in Kotlin,
but can of course also be used in Java projects. Currently it is not very customizable, but I plan on adding more options later on.

## Build Tools
### Maven
```xml
<dependency>
  <groupId>io.github.camshaft54</groupId>
  <artifactId>PigLatinTranslator</artifactId>
  <version>1.0.2</version>
</dependency>
```
### Gradle Groovy DSL
```gradle
implementation 'io.github.camshaft54:PigLatinTranslator:1.0.2'
```
## [Javadocs](https://camshaft54.github.io/PigLatinTranslator/)

## Attributions
EnglishDict.txt comes from [here](https://github.com/dwyl/english-words).

This translator also uses [Unix4j](https://github.com/tools4j/unix4j).
