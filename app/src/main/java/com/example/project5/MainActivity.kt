package com.example.project5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var characterImageURL = ""
    var characterName = ""
    var characterTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<Button>(R.id.getCharacter)
        val image = findViewById<ImageView>(R.id.characterImage)
        val name = findViewById<TextView>(R.id.characterName)
        val title = findViewById<TextView>(R.id.characterTitle)
        val characterRequest = findViewById<EditText>(R.id.findCharacter)

        getNextCharacter(button, image, name, title, characterRequest)

    }

    private fun getNextCharacter(button: Button, imageView: ImageView, name: TextView, title: TextView, characterRequest: EditText) {
        button.setOnClickListener {
            val findCharacter = characterRequest.text.toString()

            getCharacterImage(findCharacter)

            Glide.with(this)
                .load(characterImageURL)
                .fitCenter()
                .into(imageView)

            name.text = characterName
            title.text = characterTitle
        }
    }



    private fun getCharacterImage(findCharacter: String?) {
        val client = AsyncHttpClient()
        client["https://thronesapi.com/api/v2/Characters", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                val jsonArray = json.jsonArray
                if (findCharacter != "") {
                    for (i in 0 until jsonArray.length()) {
                        val characterObject = jsonArray.getJSONObject(i)
                        val fullName = characterObject.getString("fullName")
                        val title = characterObject.getString("title")
                        if (fullName.equals(findCharacter, ignoreCase = true) ||
                            title.equals(findCharacter, ignoreCase = true)
                        ) {
                            characterImageURL = characterObject.getString("imageUrl")
                            characterName = fullName
                            characterTitle = title
                            Log.d("CharacterURL", "Character found by name")
                            break
                        }
                    }
                }
                else {
                    val randomID = Random.nextInt(jsonArray.length())
                    characterImageURL = jsonArray.getJSONObject(randomID).getString("imageUrl")
                    characterName = jsonArray.getJSONObject(randomID).getString("fullName")
                    characterTitle = jsonArray.getJSONObject(randomID).getString("title")
                    Log.d("CharacterURL", "character image URL set")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Member Error", errorResponse)
            }
        }]
    }
}