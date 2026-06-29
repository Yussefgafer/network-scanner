package me.jo.netscan.util

import org.junit.Assert.*
import org.junit.Test

class MacVendorLookupTest {

    // --- lookup tests ---

    @Test
    fun `lookup returns Apple for Apple MAC`() {
        assertEquals("Apple", MacVendorLookup.lookup("00:03:93:AA:BB:CC"))
    }

    @Test
    fun `lookup returns Samsung for Samsung MAC`() {
        assertEquals("Samsung", MacVendorLookup.lookup("00:00:F0:11:22:33"))
    }

    @Test
    fun `lookup returns Google for Google MAC`() {
        assertEquals("Google", MacVendorLookup.lookup("00:1A:11:22:33:44"))
    }

    @Test
    fun `lookup returns Amazon for Amazon MAC`() {
        assertEquals("Amazon", MacVendorLookup.lookup("00:FC:8B:11:22:33"))
    }

    @Test
    fun `lookup returns Intel for Intel MAC`() {
        assertEquals("Intel", MacVendorLookup.lookup("00:02:B3:11:22:33"))
    }

    @Test
    fun `lookup returns Microsoft for Microsoft MAC`() {
        assertEquals("Microsoft", MacVendorLookup.lookup("00:03:FF:11:22:33"))
    }

    @Test
    fun `lookup returns Netgear for Netgear MAC`() {
        assertEquals("Netgear", MacVendorLookup.lookup("00:09:5B:11:22:33"))
    }

    @Test
    fun `lookup returns TP-Link for TP-Link MAC`() {
        assertEquals("TP-Link", MacVendorLookup.lookup("00:27:19:11:22:33"))
    }

    @Test
    fun `lookup returns ASUS for ASUS MAC`() {
        assertEquals("ASUS", MacVendorLookup.lookup("00:0C:6E:11:22:33"))
    }

    @Test
    fun `lookup returns Cisco for Cisco MAC`() {
        assertEquals("Cisco", MacVendorLookup.lookup("00:00:0C:11:22:33"))
    }

    @Test
    fun `lookup returns D-Link for D-Link MAC`() {
        assertEquals("D-Link", MacVendorLookup.lookup("00:05:5D:11:22:33"))
    }

    @Test
    fun `lookup returns Sony for Sony MAC`() {
        assertEquals("Sony", MacVendorLookup.lookup("00:01:4A:11:22:33"))
    }

    @Test
    fun `lookup returns Dell for Dell MAC`() {
        assertEquals("Dell", MacVendorLookup.lookup("00:06:5B:11:22:33"))
    }

    @Test
    fun `lookup returns HP for HP MAC`() {
        assertEquals("HP", MacVendorLookup.lookup("00:01:E6:11:22:33"))
    }

    @Test
    fun `lookup returns Lenovo for Lenovo MAC`() {
        assertEquals("Lenovo", MacVendorLookup.lookup("00:06:1B:11:22:33"))
    }

    @Test
    fun `lookup returns Huawei for Huawei MAC`() {
        assertEquals("Huawei", MacVendorLookup.lookup("00:0F:E2:11:22:33"))
    }

    @Test
    fun `lookup returns Xiaomi for Xiaomi MAC`() {
        assertEquals("Xiaomi", MacVendorLookup.lookup("00:9E:C8:11:22:33"))
    }

    @Test
    fun `lookup returns Raspberry Pi for RPi MAC`() {
        assertEquals("Raspberry Pi", MacVendorLookup.lookup("B8:27:EB:11:22:33"))
    }

    @Test
    fun `lookup returns Espressif for ESP32 MAC`() {
        assertEquals("Espressif", MacVendorLookup.lookup("24:0A:C4:11:22:33"))
    }

    // --- MAC format variations ---

    @Test
    fun `lookup works with dash-separated MAC`() {
        assertEquals("Apple", MacVendorLookup.lookup("00-03-93-AA-BB-CC"))
    }

    @Test
    fun `lookup works with no separators`() {
        assertEquals("Apple", MacVendorLookup.lookup("000393AABBCC"))
    }

    @Test
    fun `lookup works with lowercase MAC`() {
        assertEquals("Apple", MacVendorLookup.lookup("00:03:93:aa:bb:cc"))
    }

    // --- Randomized MAC rejection ---

    @Test
    fun `lookup returns null for locally administered MAC`() {
        // 0x02 bit set in first octet (e.g., x2:xx:xx:xx:xx:xx)
        assertNull(MacVendorLookup.lookup("02:03:93:AA:BB:CC"))
    }

    @Test
    fun `lookup returns null for another locally administered MAC`() {
        assertNull(MacVendorLookup.lookup("0A:03:93:AA:BB:CC"))
    }

    // --- Null and edge cases ---

    @Test
    fun `lookup returns null for null MAC`() {
        assertNull(MacVendorLookup.lookup(null))
    }

    @Test
    fun `lookup returns null for empty string`() {
        assertNull(MacVendorLookup.lookup(""))
    }

    @Test
    fun `lookup returns null for too short MAC`() {
        assertNull(MacVendorLookup.lookup("AA:BB"))
    }

    @Test
    fun `lookup returns null for unknown OUI`() {
        assertNull(MacVendorLookup.lookup("FF:FF:FF:AA:BB:CC"))
    }

    // --- isKnownVendor tests ---

    @Test
    fun `isKnownVendor returns true for known vendor`() {
        assertTrue(MacVendorLookup.isKnownVendor("00:03:93:AA:BB:CC"))
    }

    @Test
    fun `isKnownVendor returns false for unknown vendor`() {
        assertFalse(MacVendorLookup.isKnownVendor("FF:FF:FF:AA:BB:CC"))
    }

    @Test
    fun `isKnownVendor returns false for null`() {
        assertFalse(MacVendorLookup.isKnownVendor(null))
    }

    // --- searchVendors tests ---

    @Test
    fun `searchVendors returns Apple entries`() {
        val results = MacVendorLookup.searchVendors("Apple")
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.second == "Apple" })
    }

    @Test
    fun `searchVendors is case insensitive`() {
        val results = MacVendorLookup.searchVendors("samsung")
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.second == "Samsung" })
    }

    @Test
    fun `searchVendors returns empty for no match`() {
        val results = MacVendorLookup.searchVendors("NonExistentVendor123")
        assertTrue(results.isEmpty())
    }
}
