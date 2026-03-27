package br.com.phs.fullexample

import platform.Foundation.NSUserDefaults

private class IosUserStorage : UserStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun read(key: String): String? = userDefaults.stringForKey(key)

    override fun write(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }
}

class IosUserRegistrationEngine : UserRegistrationEngine(IosUserStorage())
