package me.jo.netscan.data

import org.junit.Assert.*
import org.junit.Test

class DeepScanResultTest {

    @Test
    fun `hasOpenPorts returns true when openPorts is not empty`() {
        val result = DeepScanResult(
            ipAddress = "192.168.1.1",
            openPorts = listOf(PortInfo(port = 22))
        )
        assertTrue(result.hasOpenPorts)
    }

    @Test
    fun `hasOpenPorts returns false when openPorts is empty`() {
        val result = DeepScanResult(ipAddress = "192.168.1.1")
        assertFalse(result.hasOpenPorts)
    }

    @Test
    fun `portCount returns correct count`() {
        val result = DeepScanResult(
            ipAddress = "192.168.1.1",
            openPorts = listOf(
                PortInfo(port = 22),
                PortInfo(port = 80),
                PortInfo(port = 443)
            )
        )
        assertEquals(3, result.portCount)
    }

    @Test
    fun `portCount returns 0 for empty result`() {
        val result = DeepScanResult(ipAddress = "192.168.1.1")
        assertEquals(0, result.portCount)
    }

    @Test
    fun `OsFamily displayName returns correct names`() {
        assertEquals("Windows", OsFamily.WINDOWS.displayName)
        assertEquals("Linux", OsFamily.LINUX.displayName)
        assertEquals("macOS", OsFamily.MACOS.displayName)
        assertEquals("Router OS", OsFamily.ROUTER_OS.displayName)
        assertEquals("Printer", OsFamily.PRINTER_OS.displayName)
    }
}
