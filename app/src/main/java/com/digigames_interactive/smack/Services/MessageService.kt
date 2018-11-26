package com.digigames_interactive.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.digigames_interactive.smack.Model.Channel
import com.digigames_interactive.smack.Utilities.URL_GET_CHANNELS
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {

        val channelsRequest =
            object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
                try {
                    for (x in 0 until response.length()) {
                        val channel = response.getJSONObject(x)
                        val channelName = channel.getString("name")
                        val channelDescription = channel.getString("description")
                        val channelId =  channel.getString("_id")

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
                    return hashMapOf("Authorization" to "Bearer ${AuthService.authToken}")
                }
            }

        Volley.newRequestQueue(context).add(channelsRequest)
    }
}