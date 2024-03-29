package com.example.tasky

import com.example.tasky.auth.domain.NameError
import com.example.tasky.auth.domain.PasswordError
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.validateName
import com.example.tasky.auth.domain.validatePassword
import org.junit.Test

import org.junit.Assert.*

typealias TestSuccess = Result.Success<Unit>
typealias TestNameError = Result.Error<NameError>
typealias TestPasswordError = Result.Error<PasswordError>

class StringUtilsTest {

    @Test
    fun `test name validation success`() {
        assertEquals(TestSuccess(Unit), "Arthur90".validateName())
    }

    @Test
    fun `test name validation failure - too short`() {
        assertEquals(TestNameError(NameError.TOO_SHORT), "".validateName())
        assertEquals(TestNameError(NameError.TOO_SHORT), "Ar9".validateName())
    }

    @Test
    fun `test name validation failure - too long`() {
        assertEquals(TestNameError(NameError.TOO_LONG), "1234567890qwertyuiopasdfghjkl;zxcvbnm,./!@#$%^&*()_MORE_THAN_50".validateName())
    }

    @Test
    fun `test password validation success`() {
        assertEquals(TestSuccess(Unit), "Password8".validatePassword())
    }

    @Test
    fun `test password validation failure - too short`() {
        assertEquals(TestPasswordError(PasswordError.TOO_SHORT), "".validatePassword())
        assertEquals(TestPasswordError(PasswordError.TOO_SHORT), "Passw8".validatePassword())
    }

    @Test
    fun `test password validation failure - no uppercase character`() {
        assertEquals(TestPasswordError(PasswordError.NO_UPPERCASE), "passwd_lowercase_8".validatePassword())
    }

    @Test
    fun `test password validation failure - no lowercase character`() {
        assertEquals(TestPasswordError(PasswordError.NO_LOWERCASE), "PASSWD_UPPERCASE_8".validatePassword())
    }

    @Test
    fun `test password validation failure - no digit`() {
        assertEquals(TestPasswordError(PasswordError.NO_DIGIT), "Passwd_no_number".validatePassword())
    }
}