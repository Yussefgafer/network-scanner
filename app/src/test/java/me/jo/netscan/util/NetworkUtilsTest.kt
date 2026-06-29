package me.jo.netscan.util

import org.junit.Assert.*
import org.junit.Test

class NetworkUtilsTest {

    // --- isValidIpAddress tests ---

    @Test
    fun `isValidIpAddress returns true for valid IP`() {
        assertTrue(NetworkUtils.isValidIpAddress("192.168.1.1"))
    }

    @Test
    fun `isValidIpAddress returns true for 0_0_0_0`() {
        assertTrue(NetworkUtils.isValidIpAddress("0.0.0.0"))
    }

    @Test
    fun `isValidIpAddress returns true for 255_255_255_255`() {
        assertTrue(NetworkUtils.isValidIpAddress("255.255.255.255"))
    }

    @Test
    fun `isValidIpAddress returns false for empty string`() {
        assertFalse(NetworkUtils.isValidIpAddress(""))
    }

    @Test
    fun `isValidIpAddress returns false for non-IP string`() {
        assertFalse(NetworkUtils.isValidIpAddress("not-an-ip"))
    }

    @Test
    fun `isValidIpAddress returns false for 5 octets`() {
        assertFalse(NetworkUtils.isValidIpAddress("192.168.1.1.1"))
    }

    @Test
    fun `isValidIpAddress returns false for octet over 255`() {
        assertFalse(NetworkUtils.isValidIpAddress("192.168.1.256"))
    }

    @Test
    fun `isValidIpAddress returns false for negative octet`() {
        assertFalse(NetworkUtils.isValidIpAddress("192.168.1.-1"))
    }

    @Test
    fun `isValidIpAddress returns false for injection attempt`() {
        assertFalse(NetworkUtils.isValidIpAddress("192.168.1.1; rm -rf /"))
    }

    // --- ipAddressToInt tests ---

    @Test
    fun `ipAddressToInt converts 192_168_1_1 correctly`() {
        assertEquals(0x0101A8C0.toInt(), NetworkUtils.ipAddressToInt("192.168.1.1"))
    }

    @Test
    fun `ipAddressToInt converts 10_0_0_1 correctly`() {
        assertEquals(0x0100000A, NetworkUtils.ipAddressToInt("10.0.0.1"))
    }

    @Test
    fun `ipAddressToInt converts 0_0_0_0 to 0`() {
        assertEquals(0, NetworkUtils.ipAddressToInt("0.0.0.0"))
    }

    @Test
    fun `ipAddressToInt returns 0 for invalid format`() {
        assertEquals(0, NetworkUtils.ipAddressToInt("invalid"))
    }

    // --- intToIpAddress tests ---

    @Test
    fun `intToIpAddress converts correctly`() {
        assertEquals("192.168.1.1", NetworkUtils.intToIpAddress(0x0101A8C0.toInt()))
    }

    @Test
    fun `intToIpAddress converts 10_0_0_1`() {
        assertEquals("10.0.0.1", NetworkUtils.intToIpAddress(0x0100000A))
    }

    @Test
    fun `intToIpAddress converts 0 to 0_0_0_0`() {
        assertEquals("0.0.0.0", NetworkUtils.intToIpAddress(0))
    }

    @Test
    fun `ipAddressToInt and intToIpAddress are inverses`() {
        val ip = "192.168.1.100"
        assertEquals(ip, NetworkUtils.intToIpAddress(NetworkUtils.ipAddressToInt(ip)))
    }

    // --- calculateNetworkPrefix tests ---

    @Test
    fun `calculateNetworkPrefix returns 24 for 255_255_255_0`() {
        assertEquals(24, NetworkUtils.calculateNetworkPrefix("255.255.255.0"))
    }

    @Test
    fun `calculateNetworkPrefix returns 16 for 255_255_0_0`() {
        assertEquals(16, NetworkUtils.calculateNetworkPrefix("255.255.0.0"))
    }

    @Test
    fun `calculateNetworkPrefix returns 8 for 255_0_0_0`() {
        assertEquals(8, NetworkUtils.calculateNetworkPrefix("255.0.0.0"))
    }

    @Test
    fun `calculateNetworkPrefix returns 0 for 0_0_0_0`() {
        assertEquals(0, NetworkUtils.calculateNetworkPrefix("0.0.0.0"))
    }

    @Test
    fun `calculateNetworkPrefix returns 32 for 255_255_255_255`() {
        assertEquals(32, NetworkUtils.calculateNetworkPrefix("255.255.255.255"))
    }

    @Test
    fun `calculateNetworkPrefix returns 25 for 255_255_255_128`() {
        assertEquals(25, NetworkUtils.calculateNetworkPrefix("255.255.255.128"))
    }

    @Test
    fun `calculateNetworkPrefix returns 24 for invalid format`() {
        assertEquals(24, NetworkUtils.calculateNetworkPrefix("invalid"))
    }

    // --- isLocallyAdministeredMac tests ---

    @Test
    fun `isLocallyAdministeredMac returns true for locally administered MAC`() {
        assertTrue(NetworkUtils.isLocallyAdministeredMac("02:00:00:00:00:01"))
    }

    @Test
    fun `isLocallyAdministeredMac returns false for globally unique MAC`() {
        assertFalse(NetworkUtils.isLocallyAdministeredMac("00:03:93:AA:BB:CC"))
    }

    @Test
    fun `isLocallyAdministeredMac returns false for null`() {
        assertFalse(NetworkUtils.isLocallyAdministeredMac(null))
    }

    @Test
    fun `isLocallyAdministeredMac returns false for empty string`() {
        assertFalse(NetworkUtils.isLocallyAdministeredMac(""))
    }

    @Test
    fun `isLocallyAdministeredMac handles uppercase MAC`() {
        assertTrue(NetworkUtils.isLocallyAdministeredMac("0A:00:00:00:00:01"))
    }

    // --- inferInterfaceType tests (via reflection) ---

    private fun inferInterfaceType(interfaceName: String): me.jo.netscan.util.InterfaceType {
        val method = NetworkUtils::class.java.getDeclaredMethod("inferInterfaceType", String::class.java)
        method.isAccessible = true
        return method.invoke(NetworkUtils, interfaceName) as me.jo.netscan.util.InterfaceType
    }

    @Test
    fun `inferInterfaceType returns WIFI for wlan`() {
        assertEquals(me.jo.netscan.util.InterfaceType.WIFI, inferInterfaceType("wlan0"))
    }

    @Test
    fun `inferInterfaceType returns WIFI for wifi`() {
        assertEquals(me.jo.netscan.util.InterfaceType.WIFI, inferInterfaceType("wifi0"))
    }

    @Test
    fun `inferInterfaceType returns ETHERNET for eth`() {
        assertEquals(me.jo.netscan.util.InterfaceType.ETHERNET, inferInterfaceType("eth0"))
    }

    @Test
    fun `inferInterfaceType returns ETHERNET for en`() {
        assertEquals(me.jo.netscan.util.InterfaceType.ETHERNET, inferInterfaceType("enp0s3"))
    }

    @Test
    fun `inferInterfaceType returns VPN for tun`() {
        assertEquals(me.jo.netscan.util.InterfaceType.VPN, inferInterfaceType("tun0"))
    }

    @Test
    fun `inferInterfaceType returns VPN for tap`() {
        assertEquals(me.jo.netscan.util.InterfaceType.VPN, inferInterfaceType("tap0"))
    }

    @Test
    fun `inferInterfaceType returns VPN for ppp`() {
        assertEquals(me.jo.netscan.util.InterfaceType.VPN, inferInterfaceType("ppp0"))
    }

    @Test
    fun `inferInterfaceType returns CELLULAR for rmnet`() {
        assertEquals(me.jo.netscan.util.InterfaceType.CELLULAR, inferInterfaceType("rmnet_data0"))
    }

    @Test
    fun `inferInterfaceType returns OTHER for unknown`() {
        assertEquals(me.jo.netscan.util.InterfaceType.OTHER, inferInterfaceType("br0"))
    }

    // --- interfaceTypePriority tests (via reflection) ---

    private fun interfaceTypePriority(type: me.jo.netscan.util.InterfaceType): Int {
        val method = NetworkUtils::class.java.getDeclaredMethod("interfaceTypePriority", me.jo.netscan.util.InterfaceType::class.java)
        method.isAccessible = true
        return method.invoke(NetworkUtils, type) as Int
    }

    @Test
    fun `interfaceTypePriority WIFI is 0`() {
        assertEquals(0, interfaceTypePriority(me.jo.netscan.util.InterfaceType.WIFI))
    }

    @Test
    fun `interfaceTypePriority ETHERNET is 1`() {
        assertEquals(1, interfaceTypePriority(me.jo.netscan.util.InterfaceType.ETHERNET))
    }

    @Test
    fun `interfaceTypePriority VPN is 2`() {
        assertEquals(2, interfaceTypePriority(me.jo.netscan.util.InterfaceType.VPN))
    }

    @Test
    fun `interfaceTypePriority CELLULAR is 3`() {
        assertEquals(3, interfaceTypePriority(me.jo.netscan.util.InterfaceType.CELLULAR))
    }

    @Test
    fun `interfaceTypePriority OTHER is 4`() {
        assertEquals(4, interfaceTypePriority(me.jo.netscan.util.InterfaceType.OTHER))
    }
}
