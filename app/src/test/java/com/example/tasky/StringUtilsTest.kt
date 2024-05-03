package com.example.tasky

import assertk.assertThat
import assertk.assertions.isEqualTo
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
        assertThat("Arthur90".validateName()).isEqualTo(TestSuccess(Unit))
    }

    @Test
    fun `test name validation failure - too short`() {
        assertThat("".validateName()).isEqualTo(TestNameError(NameError.TOO_SHORT))
        assertThat("Ar9".validateName()).isEqualTo(TestNameError(NameError.TOO_SHORT))
    }

    @Test
    fun `test name validation failure - too long`() {
        assertThat("1234567890qwertyuiopasdfghjkl;zxcvbnm,./!@#$%^&*()_MORE_THAN_50".validateName()).isEqualTo(TestNameError(NameError.TOO_LONG))
    }

    @Test
    fun `test password validation success`() {
        assertThat("Password8".validatePassword()).isEqualTo(TestSuccess(Unit))
    }

    @Test
    fun `test password validation failure - too short`() {
        assertThat("".validatePassword()).isEqualTo(TestPasswordError(PasswordError.TOO_SHORT))
        assertThat("Passw8".validatePassword()).isEqualTo(TestPasswordError(PasswordError.TOO_SHORT))
    }

    @Test
    fun `test password validation failure - no uppercase character`() {
        assertThat("passwd_lowercase_8".validatePassword()).isEqualTo(TestPasswordError(PasswordError.NO_UPPERCASE))
    }

    @Test
    fun `test password validation failure - no lowercase character`() {
        assertThat("PASSWD_UPPERCASE_8".validatePassword()).isEqualTo(TestPasswordError(PasswordError.NO_LOWERCASE))
    }

    @Test
    fun `test password validation failure - no digit`() {
        assertThat("Passwd_no_number".validatePassword()).isEqualTo(TestPasswordError(PasswordError.NO_DIGIT))
    }
}