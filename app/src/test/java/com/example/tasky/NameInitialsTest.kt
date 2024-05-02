package com.example.tasky

import com.example.tasky.main.domain.getInitials
import org.junit.Assert.assertEquals
import org.junit.Test

class NameInitialsTest {

    @Test
    fun `test name initials with 2 tokens`() {
        assertEquals("AP", "Arthur Pasztor".getInitials())
    }

    @Test
    fun `test name initials with 2 tokens with white spaces`() {
        assertEquals("AP", "     Arthur Pasztor     ".getInitials())
    }

    @Test
    fun `test name initials with 3 tokens`() {
        assertEquals("AP", "Arthur MiddleName Pasztor".getInitials())
    }
    
    @Test
    fun `test name initials with many tokens`() {
        assertEquals("AP", "Arthur Name1 Name2 Name3 Name5 Name6 Pasztor".getInitials())
    }

    @Test
    fun `test name initials with 1 token`() {
        assertEquals("AR", "Arthur".getInitials())
    }

    @Test
    fun `test name initials with only 2 characters`() {
        assertEquals("AR", "Ar".getInitials())
    }

    @Test
    fun `test name initials with only 1 character`() {
        assertEquals("AR", "Ar".getInitials())
    }


    @Test
    fun `test name initials with only 1 character and white spaces`() {
        assertEquals("AR", "        Ar      ".getInitials())
    }

    @Test
    fun `test name initials with empty string`() {
        assertEquals("-", "".getInitials())
    }

    @Test
    fun `test name initials with white spaces only`() {
        assertEquals("-", "         ".getInitials())
    }
}