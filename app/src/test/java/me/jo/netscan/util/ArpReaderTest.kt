package me.jo.netscan.util

import me.jo.netscan.util.ArpReader
import org.junit.Assert.*
import org.junit.Test

class ArpReaderTest {

    // --- parseProcNetArpLine tests (via reflection) ---

    private fun parseProcNetArpLine(line: String): ArpReader.ArpEntry? {
        val method = ArpReader::class.java.getDeclaredMethod("parseProcNetArpLine", String::class.java)
        method.isAccessible = true
        return method.invoke(ArpReader, line) as? ArpReader.ArpEntry
    }

    @Test
    fun `parseProcNetArpLine parses valid line`() {
        val line = "192.168.1.1     0x1         0x2         aa:bb:cc:dd:ee:ff     *        wlan0"
        val entry = parseProcNetArpLine(line)
        assertNotNull(entry)
        assertEquals("192.168.1.1", entry?.ipAddress)
        assertEquals("0x1", entry?.hwType)
        assertEquals("0x2", entry?.flags)
        assertEquals("aa:bb:cc:dd:ee:ff", entry?.macAddress)
        assertEquals("*", entry?.mask)
        assertEquals("wlan0", entry?.device)
    }

    @Test
    fun `parseProcNetArpLine returns null for too few parts`() {
        val line = "192.168.1.1 0x1"
        assertNull(parseProcNetArpLine(line))
    }

    @Test
    fun `parseProcNetArpLine returns null for empty line`() {
        assertNull(parseProcNetArpLine(""))
    }

    @Test
    fun `parseProcNetArpLine handles multiple spaces`() {
        val line = "192.168.1.100   0x1   0x2   00:11:22:33:44:55   *   eth0"
        val entry = parseProcNetArpLine(line)
        assertNotNull(entry)
        assertEquals("192.168.1.100", entry?.ipAddress)
        assertEquals("00:11:22:33:44:55", entry?.macAddress)
    }

    // --- parseIpNeighLine tests (via reflection) ---

    private fun parseIpNeighLine(line: String): ArpReader.ArpEntry? {
        val method = ArpReader::class.java.getDeclaredMethod("parseIpNeighLine", String::class.java)
        method.isAccessible = true
        return method.invoke(ArpReader, line) as? ArpReader.ArpEntry
    }

    @Test
    fun `parseIpNeighLine parses REACHABLE entry`() {
        val line = "192.168.1.1 dev wlan0 lladdr aa:bb:cc:dd:ee:ff REACHABLE"
        val entry = parseIpNeighLine(line)
        assertNotNull(entry)
        assertEquals("192.168.1.1", entry?.ipAddress)
        assertEquals("aa:bb:cc:dd:ee:ff", entry?.macAddress)
        assertEquals("wlan0", entry?.device)
        assertEquals("0x2", entry?.flags)
    }

    @Test
    fun `parseIpNeighLine parses STALE entry`() {
        val line = "192.168.1.2 dev wlan0 lladdr aa:bb:cc:dd:ee:ff STALE"
        val entry = parseIpNeighLine(line)
        assertNotNull(entry)
        assertEquals("0x2", entry?.flags)
    }

    @Test
    fun `parseIpNeighLine parses FAILED entry`() {
        val line = "192.168.1.3 dev wlan0 FAILED"
        val entry = parseIpNeighLine(line)
        assertNotNull(entry)
        assertEquals("0x0", entry?.flags)
        assertEquals("00:00:00:00:00:00", entry?.macAddress)
    }

    @Test
    fun `parseIpNeighLine parses DELAY entry`() {
        val line = "192.168.1.4 dev eth0 lladdr 11:22:33:44:55:66 DELAY"
        val entry = parseIpNeighLine(line)
        assertNotNull(entry)
        assertEquals("0x2", entry?.flags)
    }

    @Test
    fun `parseIpNeighLine returns null for too few parts`() {
        val line = "192.168.1.1"
        assertNull(parseIpNeighLine(line))
    }

    @Test
    fun `parseIpNeighLine returns null for empty line`() {
        assertNull(parseIpNeighLine(""))
    }

    // --- ArpEntry tests ---

    @Test
    fun `ArpEntry isValid returns true for valid entry`() {
        val entry = ArpReader.ArpEntry("192.168.1.1", "0x1", "0x2", "aa:bb:cc:dd:ee:ff", "*", "wlan0")
        assertTrue(entry.isValid)
    }

    @Test
    fun `ArpEntry isValid returns false for zero MAC`() {
        val entry = ArpReader.ArpEntry("192.168.1.1", "0x1", "0x2", "00:00:00:00:00:00", "*", "wlan0")
        assertFalse(entry.isValid)
    }

    @Test
    fun `ArpEntry isValid returns false for 0x0 flags`() {
        val entry = ArpReader.ArpEntry("192.168.1.1", "0x1", "0x0", "aa:bb:cc:dd:ee:ff", "*", "wlan0")
        assertFalse(entry.isValid)
    }

    @Test
    fun `ArpEntry normalizedMac converts to uppercase`() {
        val entry = ArpReader.ArpEntry("192.168.1.1", "0x1", "0x2", "aa:bb:cc:dd:ee:ff", "*", "wlan0")
        assertEquals("AA:BB:CC:DD:EE:FF", entry.normalizedMac)
    }

    @Test
    fun `ArpEntry normalizedMac replaces dashes with colons`() {
        val entry = ArpReader.ArpEntry("192.168.1.1", "0x1", "0x2", "AA-BB-CC-DD-EE-FF", "*", "wlan0")
        assertEquals("AA:BB:CC:DD:EE:FF", entry.normalizedMac)
    }
}
