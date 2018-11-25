package com.digigames_interactive.smack.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digigames_interactive.smack.Utilities.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    val TAG = AuthService::class.java.name

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        // in a Volley request, what we end up passing into the body that gets attached to the web request is actually a byte array
        // and we need our json body to be in a string format so that we can convert that to a byte array at a later point
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener {
            println(it) // will print the response
            complete(true)
        }, Response.ErrorListener {
            Log.d("ERROR", "Could not register user: $it")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        // this time we're expecting a JSON object from the API, so use JsonObjectRequest
        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener { response ->
            // this is where we parse the json object
            try {
                userEmail = response.getString("user")
                authToken = response.getString("token")
                isLoggedIn = true
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener {
            Log.d("ERROR", "Could not login user: $it")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(
        context: Context,
        name: String,
        email: String,
        avatarName: String,
        avatarColor: String,
        complete: (Boolean) -> Unit
    ) {
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)

        val requestBody = jsonBody.toString()

        val createRequest =
            object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener { response ->

                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.id = response.getString("_id")
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }

            }, Response.ErrorListener {
                Log.d("ERROR", "Could not add user: $it")
                complete(false)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }

                override fun getHeaders(): MutableMap<String, String> {
                    return hashMapOf("Authorization" to "Bearer $authToken")
                }
            }

        Volley.newRequestQueue(context).add(createRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {
        val findUserRequest =
            object : JsonObjectRequest(Method.GET, "$URL_GET_USER$userEmail", null, Response.Listener { response ->
                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.id = response.getString("_id")

                    val userDataChanged = Intent(BROADCAST_USER_DATA_CHANGED)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChanged)
                    complete(true)

                } catch (e: JSONException) {
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not find user: ${error.localizedMessage}")
                complete(false)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    return hashMapOf("Authorization" to "Bearer $authToken")
                }
            }

        Volley.newRequestQueue(context).add(findUserRequest)
    }
}