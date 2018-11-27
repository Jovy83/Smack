package com.digigames_interactive.smack.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.digigames_interactive.smack.Controller.App
import com.digigames_interactive.smack.Model.Channel
import com.digigames_interactive.smack.Model.Message
import com.digigames_interactive.smack.Utilities.URL_GET_CHANNELS
import com.digigames_interactive.smack.Utilities.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit) {

        val channelsRequest =
            object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
                try {
                    for (x in 0 until response.length()) {
                        val channel = response.getJSONObject(x)
                        val channelName = channel.getString("name")
                        val channelDescription = channel.getString("description")
                        val channelId = channel.getString("_id")

                        val newChannel = Channel(channelName, channelDescription, channelId)
                        channels.add(newChannel)
                    }
                    complete(true)

                } catch (e: JSONException) {
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not retrieve channels: ${error.localizedMessage}")
                complete(false)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    return hashMapOf("Authorization" to "Bearer ${App.prefs.authToken}")
                }
            }

        App.prefs.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {
        val url = "$URL_GET_MESSAGES$channelId"

        val messagesRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            clearMessages()
            try {
                for (x in 0 until response.length()) {
                    val message = response.getJSONObject(x)
                    val messageBody = message.getString("messageBody")
                    val messageChannelId = message.getString("channelId")
                    val id = message.getString("_id")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timeStamp = message.getString("timeStamp")

                    val newMessage = Message(messageBody, userName, messageChannelId, userAvatar, userAvatarColor, id, timeStamp)
                    messages.add(newMessage)
                }
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not retrieve messages: ${error.localizedMessage}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Authorization" to "Bearer ${App.prefs.authToken}")
            }
        }

        App.prefs.requestQueue.add(messagesRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}

