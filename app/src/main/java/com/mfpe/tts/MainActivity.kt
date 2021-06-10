package com.mfpe.tts

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var mSoundMap: HashMap<String, Int> = HashMap()
    private var readingList: ArrayList<String> = arrayListOf()
    private var hMudaOn: Boolean = true
    private var rFuerteOn: Boolean = true

    //Lifecycle ====================================================================================
    //==============================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeDictionary()

        //setting up mediaplayer
        mediaPlayer.setOnCompletionListener {
            readingList.removeAt(0)
            if (readingList.size >= 1){
                mediaPlayer.reset()
                playSound(readingList[0])
            }
        }

        btnPlay.setOnClickListener{
            readText()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mitem_h -> {
                item.isChecked = !item.isChecked
                hMudaOn = item.isChecked
                initializeDictionary()
            }
            R.id.mitem_r -> {
                item.isChecked = !item.isChecked
                rFuerteOn = item.isChecked
            }
        }
        return true
    }


    //Main logic ===================================================================================
    //==============================================================================================

    private fun readText() {
        if(tiet_text.text.toString().isEmpty()){
            Toast.makeText(this, "Ingresa un texto.", Toast.LENGTH_SHORT).show()
            return
        }

        parseText()

        if (readingList.size != 0 && !mediaPlayer.isPlaying){
            mediaPlayer.reset()
            playSound(readingList[0])
        }
    }

    private fun parseText(){
        readingList = arrayListOf()
        var text = tiet_text.text.toString().toLowerCase(Locale.US)
        text = formatText(text)
        var textUnit: String

        var i = 0
        while (i < text.length) {

            // check combinations of 3 characters
            if (text.length >= i + 3) {
                textUnit = text.substring(i, i + 3)
                if (searchList(textUnit)) {
                    i += 3
                    continue
                }
            }

            // check combinations of 2 characters
            if (text.length >= i + 2) {
                textUnit = text.substring(i, i + 2)
                if (searchList(textUnit)) {
                    i += 2
                    continue
                }
            }

            // check a single character
            textUnit = text.substring(i, i + 1)
            searchList(textUnit)
            i++
        }
    }

    private fun searchList(textUnit: String) : Boolean {
        if (mSoundMap.containsKey(textUnit)) {
            readingList.add(textUnit)

            // make adjustemts if initial R is set to hard
            if (rFuerteOn) {
                if (readingList.size == 1)
                {
                    when (textUnit) {
                        "ra" -> {
                            readingList.removeAt(readingList.size - 1)
                            readingList.add("rra")
                        }
                        "re" -> {
                            readingList.removeAt(readingList.size - 1)
                            readingList.add("rre")
                        }
                        "ri" -> {
                            readingList.removeAt(readingList.size - 1)
                            readingList.add("rri")
                        }
                        "ro" -> {
                            readingList.removeAt(readingList.size - 1)
                            readingList.add("rro")
                        }
                        "ru" -> {
                            readingList.removeAt(readingList.size - 1)
                            readingList.add("rru")
                        }
                    }
                }
                else if(readingList.size > 1) // if it's not the first character of the whole text
                {
                    if (readingList[readingList.size - 2] == " ")
                    {
                        when (textUnit) {
                            "ra" -> {
                                readingList.removeAt(readingList.size - 1)
                                readingList.add("rra")
                            }
                            "re" -> {
                                readingList.removeAt(readingList.size - 1)
                                readingList.add("rre")
                            }
                            "ri" -> {
                                readingList.removeAt(readingList.size - 1)
                                readingList.add("rri")
                            }
                            "ro" -> {
                                readingList.removeAt(readingList.size - 1)
                                readingList.add("rro")
                            }
                            "ru" -> {
                                readingList.removeAt(readingList.size - 1)
                                readingList.add("rru")
                            }
                        }
                    }
                }
            }
            return true
        }
        return false
    }

    private fun playSound(sound: String) {
        mediaPlayer.setDataSource(
            this,
            Uri.parse("android.resource://" + packageName + "/"+ mSoundMap.getValue(sound))
        )
        mediaPlayer.prepare()
        mediaPlayer.start()
    }


    //Utilities ====================================================================================
    //==============================================================================================

    private fun initializeDictionary() {
        mSoundMap = HashMap()
        val fields: Array<Field> = R.raw::class.java.fields
        for (field: Field in fields) {
            mSoundMap[field.name] = field.getInt(field)
        }

        mSoundMap["_"] = R.raw.blank

        mSoundMap["ca"] = mSoundMap.getValue("ka")
        mSoundMap["ce"] = mSoundMap.getValue("ze")
        mSoundMap["ci"] = mSoundMap.getValue("zi")
        mSoundMap["co"] = mSoundMap.getValue("ko")
        mSoundMap["cu"] = mSoundMap.getValue("ku")

        mSoundMap["q"] = mSoundMap.getValue("k")
        mSoundMap["que"] = mSoundMap.getValue("ke")
        mSoundMap["qui"] = mSoundMap.getValue("ki")
        mSoundMap["qu"] = mSoundMap.getValue("ku")

        mSoundMap["va"] = mSoundMap.getValue("ba")
        mSoundMap["ve"] = mSoundMap.getValue("be")
        mSoundMap["vi"] = mSoundMap.getValue("bi")
        mSoundMap["vo"] = mSoundMap.getValue("bo")
        mSoundMap["vu"] = mSoundMap.getValue("bu")

        mSoundMap["lla"] = mSoundMap.getValue("ya")
        mSoundMap["lle"] = mSoundMap.getValue("ye")
        mSoundMap["lli"] = mSoundMap.getValue("yi")
        mSoundMap["llo"] = mSoundMap.getValue("yo")
        mSoundMap["llu"] = mSoundMap.getValue("yu")

        if (hMudaOn)
        {
            mSoundMap["ha"] = mSoundMap.getValue("a")
            mSoundMap["he"] = mSoundMap.getValue("e")
            mSoundMap["hi"] = mSoundMap.getValue("i")
            mSoundMap["ho"] = mSoundMap.getValue("o")
            mSoundMap["hu"] = mSoundMap.getValue("u")
        }
    }

    private fun formatText(text: String): String {
        var newText = text

        newText = newText.replace("0", "cero")
        newText = newText.replace("1", "uno")
        newText = newText.replace("2", "dos")
        newText = newText.replace("3", "tres")
        newText = newText.replace("4", "cuatro")
        newText = newText.replace("5", "cinco")
        newText = newText.replace("6", "seis")
        newText = newText.replace("7", "siete")
        newText = newText.replace("8", "ocho")
        newText = newText.replace("9", "nueve")
        newText = newText.replace('á','a')
        newText = newText.replace('é','e')
        newText = newText.replace('í','i')
        newText = newText.replace('ó','o')
        newText = newText.replace('ú','u')
        newText = newText.replace(' ','_')
        newText = newText.replace(',','_')
        newText = newText.replace(';','_')
        newText = newText.replace(".","__")
        newText = newText.replace(":","__")
        newText = newText.replace("\n","___")

        return newText
    }

}