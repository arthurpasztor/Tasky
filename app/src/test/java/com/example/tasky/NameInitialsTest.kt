package com.example.tasky

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.tasky.main.domain.getInitials
import org.junit.Test

class NameInitialsTest {

    @Test
    fun `test name initials with 2 tokens`() {
        assertThat("Arthur Pasztor".getInitials()).isEqualTo("AP")
    }

    @Test
    fun `test name initials with 2 tokens with white spaces`() {
        assertThat("     Arthur Pasztor     ".getInitials()).isEqualTo("AP")
    }

    @Test
    fun `test name initials with 3 tokens`() {
        assertThat("Arthur MiddleName Pasztor".getInitials()).isEqualTo("AP")
    }

    @Test
    fun `test name initials with many tokens`() {
        assertThat("Arthur Name1 Name2 Name3 Name5 Name6 Pasztor".getInitials()).isEqualTo("AP")
    }

    @Test
    fun `test name initials with 1 token`() {
        assertThat("Arthur".getInitials()).isEqualTo("AR")
    }

    @Test
    fun `test name initials with only 2 characters`() {
        assertThat("Ar".getInitials()).isEqualTo("AR")
    }

    @Test
    fun `test name initials with only 1 character`() {
        assertThat("Ar".getInitials()).isEqualTo("AR")
    }


    @Test
    fun `test name initials with only 1 character and white spaces`() {
        assertThat("        Ar      ".getInitials()).isEqualTo("AR")
    }

    @Test
    fun `test name initials with empty string`() {
        assertThat("".getInitials()).isEqualTo("-")
    }

    @Test
    fun `test name initials with white spaces only`() {
        assertThat("         ".getInitials()).isEqualTo("-")
    }
}