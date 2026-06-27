package me.jo.netscan.data

import org.junit.Assert.*
import org.junit.Test

class NetworkInfoTest {

    private fun createNetworkInfo(
        ipAddress: String = "192.168.1.100",
        subnetMask: String = "255.255.255.0",
        networkPrefix: Int = 24
    ) = NetworkInfo(
        interfaceName = "wlan0",
        ssid = "TestNetwork",
        bssid = "AA:BB:CC:DD:EE:FF",
        ipAddress = ipAddress,
        subnetMask = subnetMask,
        gateway = "192.168.1.1",
        networkPrefix = networkPrefix
    )

    // --- networkAddress tests ---

    @Test
    fun `networkAddress calculates correctly for 24-bit prefix`() {
        val info = createNetworkInfo(ipAddress = "192.168.1.100", subnetMask = "255.255.255.0")
        assertEquals("192.168.1.0", info.networkAddress)
    }

    @Test
    fun `networkAddress calculates correctly for 16-bit prefix`() {
        val info = createNetworkInfo(ipAddress = "10.0.5.10", subnetMask = "255.255.0.0")
        assertEquals("10.0.0.0", info.networkAddress)
    }

    @Test
    fun `networkAddress calculates correctly for 8-bit prefix`() {
        val info = createNetworkInfo(ipAddress = "172.16.5.10", subnetMask = "255.0.0.0")
        assertEquals("172.0.0.0", info.networkAddress)
    }

    @Test
    fun `networkAddress calculates correctly for 25-bit prefix`() {
        val info = createNetworkInfo(ipAddress = "192.168.1.200", subnetMask = "255.255.255.128")
        assertEquals("192.168.1.128", info.networkAddress)
    }

    @Test
    fun `networkAddress returns ipAddress for invalid format`() {
        val info = createNetworkInfo(ipAddress = "invalid", subnetMask = "255.255.255.0")
        assertEquals("invalid", info.networkAddress)
    }

    @Test
    fun `networkAddress returns ipAddress for 3-part subnet mask`() {
        val info = createNetworkInfo(ipAddress = "192.168.1.1", subnetMask = "255.255.255")
        assertEquals("192.168.1.1", info.networkAddress)
    }

    // --- cidrNotation tests ---

    @Test
    fun `cidrNotation returns correct format`() {
        val info = createNetworkInfo(networkPrefix = 24)
        assertEquals("192.168.1.0/24", info.cidrNotation)
    }

    @Test
    fun `cidrNotation with 16-bit prefix`() {
        val info = createNetworkInfo(ipAddress = "10.0.5.10", subnetMask = "255.255.0.0", networkPrefix = 16)
        assertEquals("10.0.0.0/16", info.cidrNotation)
    }

    // --- maxHosts tests ---

    @Test
    fun `maxHosts returns 254 for 24-bit prefix`() {
        val info = createNetworkInfo(networkPrefix = 24)
        assertEquals(254, info.maxHosts)
    }

    @Test
    fun `maxHosts returns 65534 for 16-bit prefix`() {
        val info = createNetworkInfo(networkPrefix = 16)
        assertEquals(65534, info.maxHosts)
    }

    @Test
    fun `maxHosts returns 2 for 30-bit prefix`() {
        val info = createNetworkInfo(networkPrefix = 30)
        assertEquals(2, info.maxHosts)
    }

    @Test
    fun `maxHosts returns 6 for 29-bit prefix`() {
        val info = createNetworkInfo(networkPrefix = 29)
        assertEquals(6, info.maxHosts)
    }

    @Test
    fun `maxHosts returns 14 for 28-bit prefix`() {
        val info = createNetworkInfo(networkPrefix = 28)
        assertEquals(14, info.maxHosts)
    }

    // --- ScanResult computed properties ---

    @Test
    fun `ScanResult durationMs calculates correctly`() {
        val start = java.util.Date(1000)
        val end = java.util.Date(5000)
        val result = ScanResult(
            devices = emptyList(),
            scanStartTime = start,
            scanEndTime = end,
            networkInfo = createNetworkInfo(),
            scanStatus = ScanStatus.COMPLETED
        )
        assertEquals(4000L, result.durationMs)
    }

    @Test
    fun `ScanResult onlineCount counts online devices`() {
        val devices = listOf(
            Device(ipAddress = "1.1.1.1", isOnline = true),
            Device(ipAddress = "1.1.1.2", isOnline = true),
            Device(ipAddress = "1.1.1.3", isOnline = false)
        )
        val result = ScanResult(
            devices = devices,
            scanStartTime = java.util.Date(),
            scanEndTime = java.util.Date(),
            networkInfo = createNetworkInfo(),
            scanStatus = ScanStatus.COMPLETED
        )
        assertEquals(2, result.onlineCount)
    }

    @Test
    fun `ScanResult offlineCount counts offline devices`() {
        val devices = listOf(
            Device(ipAddress = "1.1.1.1", isOnline = true),
            Device(ipAddress = "1.1.1.2", isOnline = false),
            Device(ipAddress = "1.1.1.3", isOnline = false)
        )
        val result = ScanResult(
            devices = devices,
            scanStartTime = java.util.Date(),
            scanEndTime = java.util.Date(),
            networkInfo = createNetworkInfo(),
            scanStatus = ScanStatus.COMPLETED
        )
        assertEquals(2, result.offlineCount)
    }
}
