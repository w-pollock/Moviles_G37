package com.example.moviles_g37

import android.content.Context

object AppContext {
    private var ctx: Context? = null

    fun init(context: Context) {
        if (ctx == null) ctx = context.applicationContext
    }

    fun get(): Context? = ctx
}
