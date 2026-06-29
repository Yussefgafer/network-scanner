package me.jo.netscan.network

import io.mockk.mockk
import me.jo.netscan.data.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method

class NetworkScannerTest {

    private lateinit var scanner: NetworkScanner

    @Before
    fun setup() {
        scanner = NetworkScanner(mockk(relaxed = true))
    }

    // --- Helper: invoke private methods via reflection (position-based args) ---

    private fun invokePrivate(name: String, vararg args: Any?): Any? {
        val method = NetworkScanner::class.java.declaredMethods.first { it.name == name }
        method.isAccessible = true
        return method.invoke(scanner, *args)
    }

    // --- extractVersion tests ---

    @Test
    fun `extractVersion returns OpenSSH version from SSH banner`() {
        val result = invokePrivate("extractVersion",
            "SSH-2.0-OpenSSH_9.3p1 Ubuntu-1ubuntu3.3"
        )
        assertEquals("OpenSSH_9.3p1", result)
    }

    @Test
    fun `extractVersion returns nginx version`() {
        val result = invokePrivate("extractVersion", "nginx/1.24.0")
        assertEquals("1.24.0", result)
    }

    @Test
    fun `extractVersion returns Apache version`() {
        val result = invokePrivate("extractVersion", "Apache/2.4.54")
        assertEquals("2.4.54", result)
    }

    @Test
    fun `extractVersion returns OpenSSH version from OpenSSH banner`() {
        val result = invokePrivate("extractVersion", "OpenSSH_8.9p1")
        assertEquals("8.9p1", result)
    }

    @Test
    fun `extractVersion returns MySQL version`() {
        val result = invokePrivate("extractVersion", "MySQL 8.0.33")
        assertEquals("8.0.33", result)
    }

    @Test
    fun `extractVersion returns PostgreSQL version`() {
        val result = invokePrivate("extractVersion", "PostgreSQL 15.3")
        assertEquals("15.3", result)
    }

    @Test
    fun `extractVersion returns IIS version`() {
        val result = invokePrivate("extractVersion", "Microsoft-IIS/10.0")
        assertEquals("10.0", result)
    }

    @Test
    fun `extractVersion returns vsftpd version`() {
        val result = invokePrivate("extractVersion", "vsftpd 3.0.5")
        assertEquals("3.0.5", result)
    }

    @Test
    fun `extractVersion returns null for null banner`() {
        val result = invokePrivate("extractVersion", null as String?)
        assertNull(result)
    }

    @Test
    fun `extractVersion returns null for unrecognized banner`() {
        val result = invokePrivate("extractVersion", "FOOBAR something random")
        assertNull(result)
    }

    // --- detectService tests ---

    @Test
    fun `detectService returns SSH from banner`() {
        val result = invokePrivate("detectService", 22, "SSH-2.0-OpenSSH_8.9p1")
        assertEquals("SSH", result)
    }

    @Test
    fun `detectService returns HTTPS for port 443 with http banner`() {
        val result = invokePrivate("detectService", 443, "HTTP/1.1 200 OK")
        assertEquals("HTTPS", result)
    }

    @Test
    fun `detectService returns HTTP for port 80 with http banner`() {
        val result = invokePrivate("detectService", 80, "HTTP/1.1 200 OK")
        assertEquals("HTTP", result)
    }

    @Test
    fun `detectService returns FTP from banner`() {
        val result = invokePrivate("detectService", 21, "vsftpd 3.0.5")
        assertEquals("FTP", result)
    }

    @Test
    fun `detectService returns MySQL from banner`() {
        val result = invokePrivate("detectService", 3306, "MySQL 8.0.33")
        assertEquals("MySQL", result)
    }

    @Test
    fun `detectService returns Redis from banner`() {
        val result = invokePrivate("detectService", 6379, "redis_version:7.0.0")
        assertEquals("Redis", result)
    }

    @Test
    fun `detectService falls back to CommonPorts when no banner`() {
        val result = invokePrivate("detectService", 22, null)
        assertEquals("SSH", result)
    }

    // --- calculateOsScore tests ---

    @Test
    fun `calculateOsScore returns 4 per strong port match`() {
        val result = invokePrivate(
            "calculateOsScore",
            setOf(135, 445, 3389),  // ports
            "",                       // banners
            setOf(135, 445, 3389),   // strongPorts
            emptySet<Int>(),         // weakPorts
            emptyList<String>()      // keywords
        ) as Int
        assertEquals(12, result) // 3 * 4 = 12
    }

    @Test
    fun `calculateOsScore returns 1 per weak port match`() {
        val result = invokePrivate(
            "calculateOsScore",
            setOf(139),              // ports
            "",                       // banners
            setOf(135, 445),         // strongPorts
            setOf(139),              // weakPorts
            emptyList<String>()      // keywords
        ) as Int
        assertEquals(1, result) // 1 * 1 = 1
    }

    @Test
    fun `calculateOsScore adds 3 per keyword match`() {
        val result = invokePrivate(
            "calculateOsScore",
            emptySet<Int>(),                        // ports
            "running windows server 2022",           // banners
            emptySet<Int>(),                        // strongPorts
            emptySet<Int>(),                        // weakPorts
            listOf("windows", "server")             // keywords (both in banners)
        ) as Int
        assertEquals(6, result) // 2 keywords * 3 = 6
    }

    @Test
    fun `calculateOsScore returns 0 for no matches`() {
        val result = invokePrivate(
            "calculateOsScore",
            emptySet<Int>(),           // ports
            "",                        // banners
            setOf(135, 445),           // strongPorts
            setOf(139),                // weakPorts
            listOf("windows")          // keywords
        ) as Int
        assertEquals(0, result)
    }

    @Test
    fun `calculateOsScore combines strong ports and keywords`() {
        val result = invokePrivate(
            "calculateOsScore",
            setOf(135, 445),           // ports
            "microsoft iis 10.0",      // banners
            setOf(135, 445, 3389),     // strongPorts
            emptySet<Int>(),           // weakPorts
            listOf("microsoft", "iis") // keywords (both in banners)
        ) as Int
        assertEquals(14, result) // 2*4 + 2*3 = 8 + 6 = 14
    }

    // --- detectWindowsVersion tests ---

    @Test
    fun `detectWindowsVersion returns Windows 11_10 for 10_0 build`() {
        val result = invokePrivate("detectWindowsVersion", "microsoft-iis/10.0")
        assertEquals("Windows 11/10", result)
    }

    @Test
    fun `detectWindowsVersion returns Windows 10`() {
        val result = invokePrivate("detectWindowsVersion", "windows 10 enterprise")
        assertEquals("Windows 10", result)
    }

    @Test
    fun `detectWindowsVersion returns Windows Server 2022`() {
        val result = invokePrivate("detectWindowsVersion", "windows server 2022")
        assertEquals("Windows Server 2022", result)
    }

    @Test
    fun `detectWindowsVersion returns Windows Server 2019`() {
        val result = invokePrivate("detectWindowsVersion", "windows server 2019")
        assertEquals("Windows Server 2019", result)
    }

    @Test
    fun `detectWindowsVersion returns Windows Server for generic server`() {
        val result = invokePrivate("detectWindowsVersion", "windows server")
        assertEquals("Windows Server", result)
    }

    @Test
    fun `detectWindowsVersion returns generic Windows for no match`() {
        val result = invokePrivate("detectWindowsVersion", "microsoft something else")
        assertEquals("Windows", result)
    }

    // --- detectLinuxDistro tests ---

    @Test
    fun `detectLinuxDistro returns Ubuntu Linux`() {
        val result = invokePrivate("detectLinuxDistro", "openssh_8.9 ubuntu server")
        assertEquals("Ubuntu Linux", result)
    }

    @Test
    fun `detectLinuxDistro returns Debian Linux`() {
        val result = invokePrivate("detectLinuxDistro", "debian gnu/linux")
        assertEquals("Debian Linux", result)
    }

    @Test
    fun `detectLinuxDistro returns CentOS Linux`() {
        val result = invokePrivate("detectLinuxDistro", "centos linux 8")
        assertEquals("CentOS Linux", result)
    }

    @Test
    fun `detectLinuxDistro returns Fedora Linux`() {
        val result = invokePrivate("detectLinuxDistro", "fedora linux 38")
        assertEquals("Fedora Linux", result)
    }

    @Test
    fun `detectLinuxDistro returns Red Hat Linux`() {
        val result = invokePrivate("detectLinuxDistro", "red hat enterprise linux")
        assertEquals("Red Hat Linux", result)
    }

    @Test
    fun `detectLinuxDistro returns Arch Linux`() {
        val result = invokePrivate("detectLinuxDistro", "arch linux")
        assertEquals("Arch Linux", result)
    }

    @Test
    fun `detectLinuxDistro returns Alpine Linux`() {
        val result = invokePrivate("detectLinuxDistro", "alpine linux")
        assertEquals("Alpine Linux", result)
    }

    @Test
    fun `detectLinuxDistro returns generic Linux for unknown distro`() {
        val result = invokePrivate("detectLinuxDistro", "some custom linux build")
        assertEquals("Linux", result)
    }

    // --- detectRouterType tests ---

    @Test
    fun `detectRouterType returns MikroTik RouterOS`() {
        val result = invokePrivate("detectRouterType", "routeros mikrotik v7.2")
        assertEquals("MikroTik RouterOS", result)
    }

    @Test
    fun `detectRouterType returns Cisco IOS`() {
        val result = invokePrivate("detectRouterType", "cisco ios software")
        assertEquals("Cisco IOS", result)
    }

    @Test
    fun `detectRouterType returns Ubiquiti`() {
        val result = invokePrivate("detectRouterType", "ubiquiti edge router")
        assertEquals("Ubiquiti", result)
    }

    @Test
    fun `detectRouterType returns OpenWrt`() {
        val result = invokePrivate("detectRouterType", "openwrt 23.05")
        assertEquals("OpenWrt", result)
    }

    @Test
    fun `detectRouterType returns DD-WRT`() {
        val result = invokePrivate("detectRouterType", "dd-wrt v3.0")
        assertEquals("DD-WRT", result)
    }

    @Test
    fun `detectRouterType returns generic Router for unknown`() {
        val result = invokePrivate("detectRouterType", "unknown router firmware")
        assertEquals("Router", result)
    }

    // --- detectPrinterType tests (input must be lowercase like production code) ---

    @Test
    fun `detectPrinterType returns HP Printer`() {
        val result = invokePrivate("detectPrinterType", "hp laserjet pro mfp")
        assertEquals("HP Printer", result)
    }

    @Test
    fun `detectPrinterType returns Epson Printer`() {
        val result = invokePrivate("detectPrinterType", "epson workforce wf-3640")
        assertEquals("Epson Printer", result)
    }

    @Test
    fun `detectPrinterType returns Canon Printer`() {
        val result = invokePrivate("detectPrinterType", "canon imagerunner c3325i")
        assertEquals("Canon Printer", result)
    }

    @Test
    fun `detectPrinterType returns Brother Printer`() {
        val result = invokePrivate("detectPrinterType", "brother hl-l2350dw")
        assertEquals("Brother Printer", result)
    }

    @Test
    fun `detectPrinterType returns Xerox Printer`() {
        val result = invokePrivate("detectPrinterType", "xerox workcentre 6515")
        assertEquals("Xerox Printer", result)
    }

    @Test
    fun `detectPrinterType returns Lexmark Printer`() {
        val result = invokePrivate("detectPrinterType", "lexmark ms310dn")
        assertEquals("Lexmark Printer", result)
    }

    @Test
    fun `detectPrinterType returns generic Printer for unknown`() {
        val result = invokePrivate("detectPrinterType", "some unknown printer")
        assertEquals("Printer", result)
    }

    // --- sanitizeMdnsName tests ---

    @Test
    fun `sanitizeMdnsName strips hex UUID suffix`() {
        val result = invokePrivate("sanitizeMdnsName",
            "DAWLANCE-GSMART-4KTV-6d5e7bc166c22d21c08e111944d3"
        )
        assertEquals("DAWLANCE GSMART 4KTV", result)
    }

    @Test
    fun `sanitizeMdnsName replaces hyphens with spaces`() {
        val result = invokePrivate("sanitizeMdnsName", "My-Living-Room-Speaker")
        assertEquals("My Living Room Speaker", result)
    }

    @Test
    fun `sanitizeMdnsName handles name without UUID`() {
        val result = invokePrivate("sanitizeMdnsName", "Simple-Name")
        assertEquals("Simple Name", result)
    }

    @Test
    fun `sanitizeMdnsName handles name with short hex suffix`() {
        val result = invokePrivate("sanitizeMdnsName", "Printer-a1b2c3d4")
        assertEquals("Printer", result)
    }

    @Test
    fun `sanitizeMdnsName handles clean name`() {
        val result = invokePrivate("sanitizeMdnsName", "CleanName")
        assertEquals("CleanName", result)
    }

    // --- extractXmlTag tests ---

    @Test
    fun `extractXmlTag extracts friendlyName from UPnP XML`() {
        val xml = """<root><device><friendlyName>Living Room TV</friendlyName><manufacturer>Samsung</manufacturer></device></root>"""
        val result = invokePrivate("extractXmlTag", xml, "friendlyName")
        assertEquals("Living Room TV", result)
    }

    @Test
    fun `extractXmlTag extracts manufacturer`() {
        val xml = "<root><manufacturer>Samsung</manufacturer></root>"
        val result = invokePrivate("extractXmlTag", xml, "manufacturer")
        assertEquals("Samsung", result)
    }

    @Test
    fun `extractXmlTag returns null for missing tag`() {
        val result = invokePrivate("extractXmlTag", "<root/>", "friendlyName")
        assertNull(result)
    }

    @Test
    fun `extractXmlTag returns null for empty tag content`() {
        val result = invokePrivate("extractXmlTag", "<root><friendlyName></friendlyName></root>", "friendlyName")
        assertNull(result)
    }

    @Test
    fun `extractXmlTag handles nested XML`() {
        val xml = "<root><device><model>TestModel</model></device></root>"
        val result = invokePrivate("extractXmlTag", xml, "model")
        assertEquals("TestModel", result)
    }

    // --- buildNetBiosRequest tests ---

    @Test
    fun `buildNetBiosRequest returns 50-byte packet`() {
        val result = invokePrivate("buildNetBiosRequest") as ByteArray
        assertEquals(50, result.size)
    }

    @Test
    fun `buildNetBiosRequest has correct transaction ID`() {
        val result = invokePrivate("buildNetBiosRequest") as ByteArray
        assertEquals(0x13, result[0].toInt() and 0xFF)
        assertEquals(0x37, result[1].toInt() and 0xFF)
    }

    @Test
    fun `buildNetBiosRequest has correct QDCOUNT of 1`() {
        val result = invokePrivate("buildNetBiosRequest") as ByteArray
        assertEquals(0x00, result[4].toInt() and 0xFF)
        assertEquals(0x01, result[5].toInt() and 0xFF)
    }

    @Test
    fun `buildNetBiosRequest has correct QTYPE NBSTAT at offset 46`() {
        val result = invokePrivate("buildNetBiosRequest") as ByteArray
        assertEquals(0x00, result[46].toInt() and 0xFF)
        assertEquals(0x21, result[47].toInt() and 0xFF)
    }

    @Test
    fun `buildNetBiosRequest has correct QCLASS IN at offset 48`() {
        val result = invokePrivate("buildNetBiosRequest") as ByteArray
        assertEquals(0x00, result[48].toInt() and 0xFF)
        assertEquals(0x01, result[49].toInt() and 0xFF)
    }

    // --- PING_RTT_PATTERN tests ---

    @Test
    fun `PING_RTT_PATTERN extracts RTT from ping output`() {
        val pattern = NetworkScanner::class.java.getDeclaredField("PING_RTT_PATTERN").apply {
            isAccessible = true
        }.get(null) as Regex

        val match = pattern.find("64 bytes from 192.168.1.1: icmp_seq=1 ttl=64 time=12.3 ms")
        assertNotNull(match)
        assertEquals("12.3", match?.groupValues?.get(1))
    }

    @Test
    fun `PING_RTT_PATTERN extracts RTT with equals sign`() {
        val pattern = NetworkScanner::class.java.getDeclaredField("PING_RTT_PATTERN").apply {
            isAccessible = true
        }.get(null) as Regex

        val match = pattern.find("time=0.56 ms")
        assertNotNull(match)
        assertEquals("0.56", match?.groupValues?.get(1))
    }

    @Test
    fun `PING_RTT_PATTERN returns null for unreachable host output`() {
        val pattern = NetworkScanner::class.java.getDeclaredField("PING_RTT_PATTERN").apply {
            isAccessible = true
        }.get(null) as Regex

        assertNull(pattern.find("100% packet loss"))
    }

    // --- detectOs tests ---

    @Test
    fun `detectOs identifies Windows from ports 135 445 3389`() {
        val ports = listOf(
            PortInfo(port = 135, serviceName = "MSRPC"),
            PortInfo(port = 445, serviceName = "SMB"),
            PortInfo(port = 3389, serviceName = "RDP"),
            PortInfo(port = 80, serviceName = "HTTP")
        )

        val result = invokePrivate("detectOs", "192.168.1.100", ports) as? OsInfo

        assertNotNull(result)
        assertEquals(OsFamily.WINDOWS, result?.family)
    }

    @Test
    fun `detectOs identifies Linux from openssh banner`() {
        val ports = listOf(
            PortInfo(port = 22, serviceName = "SSH", banner = "SSH-2.0-OpenSSH_8.9p1 Ubuntu-1ubuntu3.3"),
            PortInfo(port = 80, serviceName = "HTTP", banner = "Apache/2.4.54"),
            PortInfo(port = 443, serviceName = "HTTPS")
        )

        val result = invokePrivate("detectOs", "192.168.1.50", ports) as? OsInfo

        assertNotNull(result)
        assertEquals(OsFamily.LINUX, result?.family)
    }

    @Test
    fun `detectOs returns null for empty port list`() {
        val result = invokePrivate("detectOs", "192.168.1.100", emptyList<PortInfo>())
        assertNull(result)
    }

    @Test
    fun `detectOs identifies printer from port 9100`() {
        val ports = listOf(
            PortInfo(port = 9100, serviceName = "JetDirect"),
            PortInfo(port = 515, serviceName = "LPD")
        )

        val result = invokePrivate("detectOs", "192.168.1.200", ports) as? OsInfo

        assertNotNull(result)
        assertEquals(OsFamily.PRINTER_OS, result?.family)
    }
}
