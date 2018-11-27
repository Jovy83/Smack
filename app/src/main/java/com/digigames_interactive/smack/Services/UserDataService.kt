package com.digigames_interactive.smack.Services

import android.graphics.Color
import com.digigames_interactive.smack.Controller.App
import java.util.*

object UserDataService {
    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
        App.prefs.authToken = ""
        App.prefs.userEmail = ""
        App.prefs.isLoggedIn = false
        MessageService.clearMessages()
        MessageService.clearChannels()
    }

    fun returnAvatarColor(rgbComponents: String): Int {
        //    [0.6823529411764706, 0.9764705882352941, 0.26666666666666666, 1]
        // string manipulation example
        val strippedColor = rgbComponents
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var r = 0 // inferred as Int by Kotlin because 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)
        // just to make sure that there is actually something to be scanned
        if (scanner.hasNextDouble()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(r, g, b)
    }
}