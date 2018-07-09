package io.github.tigerjang.dshschedulemanagerandroidclient

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.provider.Settings
import android.view.MenuItem
import org.jetbrains.anko.*
import android.preference.PreferenceScreen
import io.github.tigerjang.dshschedulemanagerandroidclient.SettingsActivity.SettingsFragment





class SettingsActivity : AppCompatActivity() {
    private var settingsFragment: SettingsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        this.settingsFragment = SettingsFragment()
        fragmentManager.beginTransaction()
                .replace(R.id.settingsFrameLayout, settingsFragment)
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            if (settingsFragment!!.goBack()) {
                super.onBackPressed()
            }
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        if (settingsFragment!!.goBack()) {
            super.onBackPressed()
        }
    }

    class SettingsFragment : PreferenceFragment() {
        private var xmlId: Int? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
//            addPreferencesFromResource(R.xml.pref_settings)
//            this.xmlId = R.xml.pref_settings
            changePrefScreen(R.xml.pref_settings, R.string.pref_title_settings)
        }

        private fun changePrefScreen(xmlId: Int, titleId: Int) {
            this.xmlId = xmlId
            (activity as AppCompatActivity).supportActionBar!!.setTitle(activity.resources.getString(titleId))
            preferenceScreen?.removeAll()
            addPreferencesFromResource(xmlId)

            when (xmlId) {
                R.xml.pref_settings -> registerMainSettings()
                R.xml.pref_safe_update -> {
                    val safeLevelPref = findPreference(R.string.pref_key_safe_level)
                    flushSafeLevel()
                    safeLevelPref.setOnPreferenceChangeListener { pref, value ->
                        flushSafeLevel(pref, "$value")
                        true
                    }
                }
            }
        }

        // will be called by SettingsActivity (Host Activity)
        fun goBack(): Boolean {
            if (xmlId == R.xml.pref_settings)
            // in top-level
            {
                // Switch to MainActivity
//                val intent = Intent(activity, MainActivity::class.java)
//                startActivity(intent)
                return true
            } else
            // in sub-level
            {
                changePrefScreen(R.xml.pref_settings, R.string.pref_title_settings)
                return false
            }
        }

        override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {
            val key = preference.key

            //
            // Top level PreferenceScreen
            //
            if (key == "pref_key_safe_update") {
                changePrefScreen(R.xml.pref_safe_update, R.string.pref_title_safe_update) // descend into second level
                return true
            }

            // ...

            //
            // Second level PreferenceScreens
            //
            if (key == "second_level_key_0") {
                // do something...
            }

            return false

            // ...
        }

        fun registerMainSettings() {
            val userVipPref = findPreference(R.string.pref_key_user_vip)
            userVipPref.setOnPreferenceChangeListener { pref, value ->
                flushUserVip(pref, value)
                true
            }
            val userLogoutPref = findPreference(R.string.pref_key_user_logout)
            userLogoutPref.setOnPreferenceClickListener {
//                UserHolder.deleteUser(activity)
//                ActivityUtils.finishActivities { startActivity(Intent(activity, LoginActivity::class.java)) }
                true
            }
            val safeUpdatePref = findPreference(R.string.pref_key_safe_switch)
            safeUpdatePref.setOnPreferenceChangeListener { pref, value ->
                flushSafeUpdate(value as Boolean)
                true
            }
            val netOfflinePref = findPreference(R.string.pref_key_net_offline)
            netOfflinePref.setOnPreferenceChangeListener { pref, any ->
                flushFlowSet(isOffline = any as Boolean)
                findPreference(R.string.pref_key_notify_settings).isEnabled = !any
                true
            }
            val netFlowMsPref = findPreference(R.string.pref_key_net_flow_change)
            netFlowMsPref.setOnPreferenceChangeListener { pref, values ->
                flushFlowSet(pref, values as Set<String>)
                true
            }
            val notifySwitchPref = findPreference(R.string.pref_key_notify_switch)
            notifySwitchPref.setOnPreferenceChangeListener { pref, any ->
                flushNotifySwitch(pref, any as Boolean)
                flushNotifyRingtone(allowNotify = any)
                true
            }
            val notifyRingPref = findPreference(R.string.pref_key_notify_ring)
            notifyRingPref.setOnPreferenceChangeListener { pref, any ->
                flushNotifyRingtone(pref, any as String)
                true
            }

            flushUserInfo()
            flushUserVip()
            flushSafeUpdate()
//            flushSafeLevel()
            flushFlowSet()
            flushNetState()
            flushNotifySwitch()
//            flushNotifyRingtone()  // TODO why error ???
        }

//        fun flush() {
//            flushUserInfo()
//            flushUserVip()
//            flushSafeUpdate()
//            flushSafeLevel()
//            flushFlowSet()
//            flushNetState()
//            flushNotifySwitch()
//            flushNotifyRingtone()
//        }

        /**
         * 刷新用户信息
         */
        private fun flushUserInfo() {
            val pref = findPreference(R.string.pref_key_user_info)
            findPreference(R.string.pref_key_user_logout).isEnabled = true
            pref.title = "您好 DSH"
            pref.summary = "您当前的积分:2333"
            pref.intent = null
//            val user = UserHolder.getUser()
//            if (user == null) {
//                findPreference(R.string.pref_key_user_logout).isEnabled = false
//                if (NetState.state.value > 0) {
//                    pref.title = "未登录"
//                    pref.summary = "点击登录"
//                    pref.intent = Intent(activity, LoginActivity::class.java)
//                } else {
//                    pref.title = "网络连接失败"
//                    pref.summary = "点击打开网络连接"
//                    pref.intent = Intent(Settings.ACTION_WIFI_SETTINGS)
//                }
//            } else {
//                findPreference(R.string.pref_key_user_logout).isEnabled = true
//                pref.title = "您好 陈小默"
//                pref.summary = "您当前的积分:${user.score}"
//                pref.intent = null
//            }
        }

        /**
         * 刷新用户会员状态
         */
        private fun flushUserVip(preference: Preference? = null, value: Any? = null) {
            val pref = preference ?: findPreference(R.string.pref_key_user_vip)
            pref.summary = getString(R.string.pref_summary_user_vip)
//            val user = UserHolder.getUser()
//            if (user == null) {
//                pref.summary = getString(R.string.pref_summary_user_vip)
//            } else {
//                if (value != null) {
//                    if (value.toString().toInt() > 0) {
//                        pref.summary = "您的会员时长为 $value 个月"
//                    } else pref.summary = getString(R.string.pref_summary_user_vip)
//                } else {
//                    val v = get(R.string.pref_key_user_vip, "0")?.toInt() ?: 0
//                    if (v > 0) {
//                        pref.summary = "您的会员时长为 $v 个月"
//                    } else pref.summary = getString(R.string.pref_summary_user_vip)
//                }
//            }
        }

        /**
         * 刷新修改手势状态
         */
        private fun flushSafeUpdate(value: Boolean? = null) {
            val pref = findPreference(R.string.pref_key_safe_update)
            pref.isEnabled = value ?: getBoolean(R.string.pref_key_safe_switch)
        }

        /**
         * 刷新安全级别
         */
        private fun flushSafeLevel(preference: Preference? = null, any: String? = null) {
            val pref = preference ?: findPreference(R.string.pref_key_safe_level)
            val value = any ?: get(R.string.pref_key_safe_level)
            val entryValues = resources.getStringArray(R.array.pref_entryValues_safe_level)
            val entries = resources.getStringArray(R.array.pref_entries_safe_level)
            val index = entryValues.indexOf(value)
            if (index >= 0) {
                val level = entries[index]
                pref.summary = "当前安全级别:$level"
            }
        }

        /**
         * 更新省流量视图
         */
        private fun flushFlowSet(preference: Preference? = null, values: Set<String>? = null, isOffline: Boolean? = null) {
            val pref = preference ?: findPreference(R.string.pref_key_net_flow_change)
            if (isOffline ?: getBoolean(R.string.pref_key_net_offline)) {
                pref.summary = "当前为离线模式"
            } else {
                val set = values ?: getStringSet(R.string.pref_key_net_flow_change)
                val builder = StringBuilder()
                builder.append("允许WiFi")
                set?.forEach { action -> builder.append(",").append(action) }
                builder.append("下联网")
                pref.summary = builder.toString()
            }
        }

        /**
         * 更新网络信息
         */
        private fun flushNetState() {
            val pref = findPreference(R.string.pref_key_net_flow_info)
            pref.title = "网络未连接"
//            val state = NetState.state
//            if (state.value > 0) {
//                pref.title = "当前使用的是${NetState.state.name}网络"
//                if (NetState.state == NetworkType.WIFI) {
//                    pref.summary = "WiFi信号强度${NetUtils(activity).WIFI_RSSI}dBm"
//                }
//            } else pref.title = "网络未连接"
        }

        /**
         * 刷新推送说明
         */
        private fun flushNotifySwitch(preference: Preference? = null, value: Boolean? = null) {
            val pref = preference ?: findPreference(R.string.pref_key_notify_switch)
            pref.summary = if (value ?: getBoolean(R.string.pref_key_notify_switch))
                "当前允许推送消息"
            else "当前禁止推送消息"
        }

        /**
         * 刷新通知铃声状态
         */
        private fun flushNotifyRingtone(preference: Preference? = null, content: String? = null, allowNotify: Boolean? = null) {
            val pref = preference ?: findPreference(R.string.pref_key_notify_ring)
            pref.isEnabled = (allowNotify ?: getBoolean(R.string.pref_key_notify_switch))
            val uri = Uri.parse(content ?: get(R.string.pref_key_notify_ring))
            val ring = RingtoneManager.getRingtone(activity, uri)
            val name = ring.getTitle(activity)
//            pref.summary = if (StringUtils.isBlank(name)) "无" else name
            pref.summary = name
        }


        private fun findPreference(resId: Int): Preference {
            val key = getString(resId)
            return findPreference(key)
        }

        private fun getBoolean(resId: Int, defaultValue: Boolean = false): Boolean {
            val key = getString(resId)
            return preferenceManager.sharedPreferences.getBoolean(key, defaultValue)
        }

        private fun getStringSet(resId: Int, defaultValue: Set<String>? = null): Set<String>? {
            val key = getString(resId)
            return preferenceManager.sharedPreferences.getStringSet(key, defaultValue)
        }

        private fun get(keyId: Int, defaultValue: String? = null): String? {
            val key = getString(keyId)
            return preferenceManager.sharedPreferences.getString(key, defaultValue)
        }

        var netTag = ""

        override fun onStart() {
            super.onStart()
//            register()  TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            netTag = NetState.subscribe {
//                flush()
//            }
        }

        override fun onStop() {
//            NetState.unSubscribe(netTag)
            super.onStop()
        }
    }
}
