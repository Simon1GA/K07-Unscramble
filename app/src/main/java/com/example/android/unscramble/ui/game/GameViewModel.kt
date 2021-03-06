package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
   // private var _score = 0
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

   // private var _currentWordCount = 0
    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int> get() = _currentWordCount
    //val currentWordCount: Int get() = _currentWordCount
    private val _currentScrambledWord = MutableLiveData<String>()

    //val currentScrambledWord: LiveData<String> get() = _currentScrambledWord
    //Used spannable instead of string
    //spannable string is a string with extra info attached to it
    //did this to make talkback read each letter of the word
    //but for some reason it still reads the same thing
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

   // private lateinit var _currentScrambledWord: String
    //val currentScrambledWord: String
      //  get() = _currentScrambledWord
    private var wordsList: MutableList<String>
    private lateinit var currentWord: String

    init {
        Log.d("GameFragment", "GameViewModel created")
        wordsList = mutableListOf()
        getNextWord()
    }

    /*override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment","GameViewModel destroyed")
    }*/

    private fun getNextWord(){
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()
        while(tempWord.toString().equals(currentWord, false)){
            tempWord.shuffle()
        }
        Log.d("GameFragment",currentWord)
        if(wordsList.contains(currentWord)){
            getNextWord()
        } else {
            //_currentScrambledWord = String(tempWord)
            _currentScrambledWord.value = String(tempWord)
           // ++_currentWordCount.value
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }



    }

    fun nextWord(): Boolean {
        return if(_currentWordCount.value!! < MAX_NO_OF_WORDS){
            getNextWord()
            true
        } else false
    }

    private fun increaseScore(){
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean{
        if(playerWord.equals(currentWord, true)){
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData(){
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

}