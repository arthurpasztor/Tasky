package com.example.tasky

import com.example.tasky.auth.domain.NameError
import com.example.tasky.auth.domain.PasswordError
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.auth.domain.validateName
import com.example.tasky.auth.domain.validatePassword
import org.junit.Test

import org.junit.Assert.*

typealias TestSuccess = Result.Success<Unit, RootError>
typealias TestNameError = Result.Error<Unit, NameError>
typealias TestPasswordError = Result.Error<Unit, PasswordError>

class StringUtilsTest {

    @Test
    fun `test name validation`() {
        assertEquals(TestSuccess(Unit), "Arthur90".validateName())

        assertEquals(TestNameError(NameError.TOO_SHORT), "".validateName())
        assertEquals(TestNameError(NameError.TOO_SHORT), "Ar9".validateName())
        assertEquals(TestNameError(NameError.TOO_LONG), "1234567890qwertyuiopasdfghjkl;zxcvbnm,./!@#$%^&*()_MORE_THAN_50".validateName())
    }

    @Test
    fun `test password validation`() {
        assertEquals(TestSuccess(Unit), "Password8".validatePassword())

        assertEquals(TestPasswordError(PasswordError.TOO_SHORT), "".validatePassword())
        assertEquals(TestPasswordError(PasswordError.TOO_SHORT), "Passw8".validatePassword())
        assertEquals(TestPasswordError(PasswordError.NO_UPPERCASE), "passwd_lowercase_8".validatePassword())
        assertEquals(TestPasswordError(PasswordError.NO_LOWERCASE), "PASSWD_UPPERCASE_8".validatePassword())
        assertEquals(TestPasswordError(PasswordError.NO_DIGIT), "Passwd_no_number".validatePassword())
    }
}