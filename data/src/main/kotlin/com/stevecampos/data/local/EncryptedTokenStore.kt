package com.stevecampos.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.stevecampos.domain.model.AuthSession
import com.stevecampos.domain.repository.TokenStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedTokenStore @Inject constructor(
    @ApplicationContext context: Context,
) : TokenStore {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override suspend fun save(session: AuthSession) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, session.accessToken)
            .putString(KEY_USERNAME, session.username)
            .putLong(KEY_ISSUED_AT, session.issuedAtMillis)
            .putLong(KEY_EXPIRES_AT, session.expiresAtMillis)
            .putLong(KEY_LAST_ACTIVITY_AT, session.lastActivityAtMillis)
            .apply()
    }

    override suspend fun get(): AuthSession? {
        val accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null) ?: return null
        val username = sharedPreferences.getString(KEY_USERNAME, null) ?: return null

        return AuthSession(
            accessToken = accessToken,
            username = username,
            issuedAtMillis = sharedPreferences.getLong(KEY_ISSUED_AT, 0L),
            expiresAtMillis = sharedPreferences.getLong(KEY_EXPIRES_AT, 0L),
            lastActivityAtMillis = sharedPreferences.getLong(KEY_LAST_ACTIVITY_AT, 0L),
        )
    }

    override suspend fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "secure_banking_session"
        const val KEY_ACCESS_TOKEN = "key_access_token"
        const val KEY_USERNAME = "key_username"
        const val KEY_ISSUED_AT = "key_issued_at"
        const val KEY_EXPIRES_AT = "key_expires_at"
        const val KEY_LAST_ACTIVITY_AT = "key_last_activity_at"
    }
}
