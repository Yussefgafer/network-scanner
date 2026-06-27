package me.jo.netscan.data

import org.junit.Assert.*
import org.junit.Test

class DeviceTest {

    // --- displayName tests ---

    @Test
    fun `displayName returns customName when set`() {
        val device = Device(ipAddress = "192.168.1.1", customName = "My Router")
        assertEquals("My Router", device.displayName)
    }

    @Test
    fun `displayName returns hostname when no customName`() {
        val device = Device(ipAddress = "192.168.1.1", hostname = "living-room-tv")
        assertEquals("living-room-tv", device.displayName)
    }

    @Test
    fun `displayName skips hostname that equals ipAddress`() {
        val device = Device(ipAddress = "192.168.1.1", hostname = "192.168.1.1")
        assertEquals("192.168.1.1", device.displayName)
    }

    @Test
    fun `displayName skips blank hostname`() {
        val device = Device(ipAddress = "192.168.1.1", hostname = "  ")
        assertEquals("192.168.1.1", device.displayName)
    }

    @Test
    fun `displayName returns vendor with last octet when no customName or hostname`() {
        val device = Device(ipAddress = "192.168.1.42", vendor = "Apple")
        assertEquals("Apple (42)", device.displayName)
    }

    @Test
    fun `displayName returns ipAddress when nothing else available`() {
        val device = Device(ipAddress = "10.0.0.5")
        assertEquals("10.0.0.5", device.displayName)
    }

    @Test
    fun `displayName priority is customName over hostname`() {
        val device = Device(
            ipAddress = "192.168.1.1",
            hostname = "some-host",
            customName = "My Device"
        )
        assertEquals("My Device", device.displayName)
    }

    // --- shortId tests ---

    @Test
    fun `shortId returns last 8 chars of MAC when macAddress is set`() {
        val device = Device(ipAddress = "192.168.1.1", macAddress = "AA:BB:CC:DD:EE:FF")
        assertEquals("DD:EE:FF", device.shortId)
    }

    @Test
    fun `shortId returns last octet of IP when no macAddress`() {
        val device = Device(ipAddress = "192.168.1.42")
        assertEquals("42", device.shortId)
    }

    // --- hasDetails tests ---

    @Test
    fun `hasDetails returns false when all optional fields are null or empty`() {
        val device = Device(ipAddress = "192.168.1.1")
        assertFalse(device.hasDetails)
    }

    @Test
    fun `hasDetails returns true when hostname is set`() {
        val device = Device(ipAddress = "192.168.1.1", hostname = "my-host")
        assertTrue(device.hasDetails)
    }

    @Test
    fun `hasDetails returns true when vendor is set`() {
        val device = Device(ipAddress = "192.168.1.1", vendor = "Samsung")
        assertTrue(device.hasDetails)
    }

    @Test
    fun `hasDetails returns true when mdnsServices is not empty`() {
        val device = Device(ipAddress = "192.168.1.1", mdnsServices = listOf("_http._tcp"))
        assertTrue(device.hasDetails)
    }

    @Test
    fun `hasDetails returns true when ssdpInfo is set`() {
        val device = Device(ipAddress = "192.168.1.1", ssdpInfo = SsdpDeviceInfo(friendlyName = "TV"))
        assertTrue(device.hasDetails)
    }

    @Test
    fun `hasDetails returns true when netBiosInfo is set`() {
        val device = Device(ipAddress = "192.168.1.1", netBiosInfo = NetBiosInfo(hostname = "DESKTOP"))
        assertTrue(device.hasDetails)
    }

    // --- uniqueId tests ---

    @Test
    fun `uniqueId returns macAddress when available`() {
        val device = Device(ipAddress = "192.168.1.1", macAddress = "AA:BB:CC:DD:EE:FF")
        assertEquals("AA:BB:CC:DD:EE:FF", device.uniqueId)
    }

    @Test
    fun `uniqueId returns ipAddress when no macAddress`() {
        val device = Device(ipAddress = "192.168.1.1")
        assertEquals("192.168.1.1", device.uniqueId)
    }

    // --- equals tests ---

    @Test
    fun `devices are equal when all fields match`() {
        val date = java.util.Date()
        val d1 = Device(ipAddress = "192.168.1.1", macAddress = "AA:BB:CC:DD:EE:FF", hostname = "host", lastSeen = date, firstSeen = date)
        val d2 = Device(ipAddress = "192.168.1.1", macAddress = "AA:BB:CC:DD:EE:FF", hostname = "host", lastSeen = date, firstSeen = date)
        assertEquals(d1, d2)
    }

    @Test
    fun `devices are not equal when hostname differs`() {
        val d1 = Device(ipAddress = "192.168.1.1", hostname = "host1")
        val d2 = Device(ipAddress = "192.168.1.1", hostname = "host2")
        assertNotEquals(d1, d2)
    }

    @Test
    fun `devices are not equal when isOnline differs`() {
        val d1 = Device(ipAddress = "192.168.1.1", isOnline = true)
        val d2 = Device(ipAddress = "192.168.1.1", isOnline = false)
        assertNotEquals(d1, d2)
    }

    @Test
    fun `devices are not equal when latencyMs differs`() {
        val d1 = Device(ipAddress = "192.168.1.1", latencyMs = 10)
        val d2 = Device(ipAddress = "192.168.1.1", latencyMs = 20)
        assertNotEquals(d1, d2)
    }

    @Test
    fun `devices are equal regardless of lastSeen and firstSeen`() {
        val d1 = Device(ipAddress = "192.168.1.1", lastSeen = java.util.Date(1000), firstSeen = java.util.Date(2000))
        val d2 = Device(ipAddress = "192.168.1.1", lastSeen = java.util.Date(9999), firstSeen = java.util.Date(8888))
        assertEquals(d1, d2)
    }

    @Test
    fun `device is not equal to null`() {
        val device = Device(ipAddress = "192.168.1.1")
        assertNotEquals(null, device)
    }

    @Test
    fun `device is not equal to non-Device object`() {
        val device = Device(ipAddress = "192.168.1.1")
        assertNotEquals("not a device", device)
    }

    @Test
    fun `same instance is equal`() {
        val device = Device(ipAddress = "192.168.1.1")
        assertEquals(device, device)
    }
}
