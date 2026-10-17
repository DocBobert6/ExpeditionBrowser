package com.expedition.browser

import android.app.Application
import com.expedition.browser.data.db.ExpeditionDatabase
import com.expedition.browser.data.datastore.SettingsRepository
import com.expedition.browser.engine.TabHibernationManager

class ExpeditionApp : Application() {

    lateinit var database: ExpeditionDatabase
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var tabHibernationManager: TabHibernationManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = ExpeditionDatabase.getInstance(this)
        settingsRepository = SettingsRepository(this)
        tabHibernationManager = TabHibernationManager(this)
    }
}
