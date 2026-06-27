package me.jo.netscan.data

import org.junit.Assert.*
import org.junit.Test

class CommonPortsTest {

    @Test
    fun `getServiceName returns SSH for port 22`() {
        assertEquals("SSH", CommonPorts.getServiceName(22))
    }

    @Test
    fun `getServiceName returns HTTP for port 80`() {
        assertEquals("HTTP", CommonPorts.getServiceName(80))
    }

    @Test
    fun `getServiceName returns HTTPS for port 443`() {
        assertEquals("HTTPS", CommonPorts.getServiceName(443))
    }

    @Test
    fun `getServiceName returns MySQL for port 3306`() {
        assertEquals("MySQL", CommonPorts.getServiceName(3306))
    }

    @Test
    fun `getServiceName returns RDP for port 3389`() {
        assertEquals("RDP", CommonPorts.getServiceName(3389))
    }

    @Test
    fun `getServiceName returns null for unknown port`() {
        assertNull(CommonPorts.getServiceName(12345))
    }

    @Test
    fun `getServiceDescription returns service name for known port`() {
        assertEquals("SSH", CommonPorts.getServiceDescription(22))
    }

    @Test
    fun `getServiceDescription returns Port N for unknown port`() {
        assertEquals("Port 12345", CommonPorts.getServiceDescription(12345))
    }

    @Test
    fun `TOP_PORTS contains common ports`() {
        assertTrue(CommonPorts.TOP_PORTS.contains(22))
        assertTrue(CommonPorts.TOP_PORTS.contains(80))
        assertTrue(CommonPorts.TOP_PORTS.contains(443))
        assertTrue(CommonPorts.TOP_PORTS.contains(3306))
        assertTrue(CommonPorts.TOP_PORTS.contains(3389))
    }

    @Test
    fun `PortInfo displayName uses serviceName when available`() {
        val portInfo = PortInfo(port = 22, serviceName = "Custom SSH")
        assertEquals("Custom SSH", portInfo.displayName)
    }

    @Test
    fun `PortInfo displayName falls back to CommonPorts when serviceName is null`() {
        val portInfo = PortInfo(port = 22, serviceName = null)
        assertEquals("SSH", portInfo.displayName)
    }

    @Test
    fun `PortInfo displayName returns Unknown for unknown port with no serviceName`() {
        val portInfo = PortInfo(port = 99999, serviceName = null)
        assertEquals("Unknown", portInfo.displayName)
    }
}
