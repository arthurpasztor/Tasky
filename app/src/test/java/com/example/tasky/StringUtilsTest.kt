package com.example.tasky

import com.example.tasky.auth.domain.isNameValid
import com.example.tasky.auth.domain.isPasswordValid
import org.junit.Test

import org.junit.Assert.*

class StringUtilsTest {

    @Test
    fun `test name validation`() {
        assertTrue("Arthur90".isNameValid())

        assertFalse("".isNameValid())
        assertFalse("Ar9".isNameValid())
        assertFalse("1234567890qwertyuiopasdfghjkl;zxcvbnm,./!@#$%^&*()_MORE_THAN_50".isNameValid())
    }

    @Test
    fun `test password validation`() {
        assertTrue("Password8".isPasswordValid())

        assertFalse("".isPasswordValid())
        assertFalse("Passw8".isPasswordValid())
        assertFalse("passwd_lowercase_8".isPasswordValid())
        assertFalse("PASSWD_UPPERCASE_8".isPasswordValid())
        assertFalse("Passwd_no_number".isPasswordValid())
    }
}