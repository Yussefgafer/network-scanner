package me.jo.netscan.data

import org.junit.Assert.*
import org.junit.Test

class DeviceTypeTest {

    @Test
    fun `identify returns UNKNOWN when all inputs are null`() {
        assertEquals(DeviceType.UNKNOWN, DeviceType.identify())
    }

    @Test
    fun `identify returns UNKNOWN for empty search terms`() {
        assertEquals(DeviceType.UNKNOWN, DeviceType.identify(hostname = "", vendor = ""))
    }

    // --- Keyword matching (Phase 1) ---

    @Test
    fun `identify returns ROUTER from hostname`() {
        assertEquals(DeviceType.ROUTER, DeviceType.identify(hostname = "my-router"))
    }

    @Test
    fun `identify returns SMARTPHONE from vendor Samsung`() {
        assertEquals(DeviceType.SMARTPHONE, DeviceType.identify(vendor = "Samsung"))
    }

    @Test
    fun `identify returns LAPTOP from hostname macbook`() {
        assertEquals(DeviceType.LAPTOP, DeviceType.identify(hostname = "macbook-pro"))
    }

    @Test
    fun `identify returns LAPTOP from vendor Dell`() {
        assertEquals(DeviceType.LAPTOP, DeviceType.identify(vendor = "Dell"))
    }

    @Test
    fun `identify returns GAME_CONSOLE from hostname xbox`() {
        assertEquals(DeviceType.GAME_CONSOLE, DeviceType.identify(hostname = "xbox-live"))
    }

    @Test
    fun `identify returns GAME_CONSOLE from hostname playstation`() {
        assertEquals(DeviceType.GAME_CONSOLE, DeviceType.identify(hostname = "ps5-bedroom"))
    }

    @Test
    fun `identify returns SMART_SPEAKER from hostname alexa`() {
        assertEquals(DeviceType.SMART_SPEAKER, DeviceType.identify(hostname = "alexa-living"))
    }

    @Test
    fun `identify returns SMART_HOME from hostname nest`() {
        assertEquals(DeviceType.SMART_HOME, DeviceType.identify(hostname = "nest-thermostat"))
    }

    @Test
    fun `identify returns NAS from hostname synology`() {
        assertEquals(DeviceType.NAS, DeviceType.identify(hostname = "synology-diskstation"))
    }

    @Test
    fun `identify returns SERVER from hostname ubuntu`() {
        assertEquals(DeviceType.SERVER, DeviceType.identify(hostname = "ubuntu-server"))
    }

    @Test
    fun `identify returns WEARABLE from hostname fitbit`() {
        assertEquals(DeviceType.WEARABLE, DeviceType.identify(hostname = "fitbit-watch"))
    }

    @Test
    fun `identify returns TV from hostname roku`() {
        assertEquals(DeviceType.TV, DeviceType.identify(hostname = "roku-livingroom"))
    }

    @Test
    fun `identify returns DESKTOP from hostname workstation`() {
        assertEquals(DeviceType.DESKTOP, DeviceType.identify(hostname = "office-workstation"))
    }

    @Test
    fun `identify returns TABLET from hostname ipad`() {
        assertEquals(DeviceType.TABLET, DeviceType.identify(hostname = "ipad-kitchen"))
    }

    @Test
    fun `identify returns PRINTER from hostname with printer keyword`() {
        assertEquals(DeviceType.PRINTER, DeviceType.identify(hostname = "office-printer"))
    }

    @Test
    fun `identify returns PRINTER from vendor epson`() {
        assertEquals(DeviceType.PRINTER, DeviceType.identify(vendor = "Epson"))
    }

    // --- Keyword priority: hostname > vendor ---

    @Test
    fun `identify returns SMARTPHONE when hostname has samsung and vendor has samsung`() {
        assertEquals(DeviceType.SMARTPHONE, DeviceType.identify(hostname = "samsung-phone", vendor = "Samsung"))
    }

    @Test
    fun `identify returns ROUTER when hostname has router and vendor has cisco`() {
        assertEquals(DeviceType.ROUTER, DeviceType.identify(hostname = "office-router", vendor = "Cisco"))
    }

    // --- Keyword substring matching behavior ---

    @Test
    fun `identify returns LAPTOP for hp-printer because hp matches LAPTOP keyword first`() {
        // "hp" is a keyword in both LAPTOP and PRINTER, but LAPTOP comes first in enum order
        assertEquals(DeviceType.LAPTOP, DeviceType.identify(hostname = "hp-printer"))
    }

    // --- mDNS service type (Phase 2) ---

    @Test
    fun `identify returns TV from mDNS _airplay`() {
        assertEquals(DeviceType.TV, DeviceType.identify(mdnsServiceType = "_airplay._tcp"))
    }

    @Test
    fun `identify returns SMART_SPEAKER from mDNS _raop`() {
        assertEquals(DeviceType.SMART_SPEAKER, DeviceType.identify(mdnsServiceType = "_raop._tcp"))
    }

    @Test
    fun `identify returns SMARTPHONE from mDNS _androidtvremote2 because android matches keyword`() {
        // "_androidtvremote2" lowercased contains "android" which is a SMARTPHONE keyword
        assertEquals(DeviceType.SMARTPHONE, DeviceType.identify(mdnsServiceType = "_androidtvremote2._tcp"))
    }

    @Test
    fun `identify returns PRINTER from mDNS _printer`() {
        assertEquals(DeviceType.PRINTER, DeviceType.identify(mdnsServiceType = "_printer._tcp"))
    }

    @Test
    fun `identify returns PRINTER from mDNS _ipp`() {
        assertEquals(DeviceType.PRINTER, DeviceType.identify(mdnsServiceType = "_ipp._tcp"))
    }

    @Test
    fun `identify returns NAS from mDNS _smb`() {
        assertEquals(DeviceType.NAS, DeviceType.identify(mdnsServiceType = "_smb._tcp"))
    }

    @Test
    fun `identify returns NAS from mDNS _afpovertcp`() {
        assertEquals(DeviceType.NAS, DeviceType.identify(mdnsServiceType = "_afpovertcp._tcp"))
    }

    @Test
    fun `identify returns SERVER from mDNS _ssh`() {
        assertEquals(DeviceType.SERVER, DeviceType.identify(mdnsServiceType = "_ssh._tcp"))
    }

    @Test
    fun `identify returns SERVER from mDNS _sftp`() {
        assertEquals(DeviceType.SERVER, DeviceType.identify(mdnsServiceType = "_sftp._tcp"))
    }

    @Test
    fun `identify returns UNKNOWN from mDNS _googlecast intentionally omitted`() {
        assertEquals(DeviceType.UNKNOWN, DeviceType.identify(mdnsServiceType = "_googlecast._tcp"))
    }

    @Test
    fun `identify returns UNKNOWN for unrecognized mDNS service`() {
        assertEquals(DeviceType.UNKNOWN, DeviceType.identify(mdnsServiceType = "_custom-service._tcp"))
    }

    // --- SSDP device type (Phase 3) ---

    @Test
    fun `identify returns TV from SSDP MediaRenderer`() {
        assertEquals(DeviceType.TV, DeviceType.identify(ssdpDeviceType = "urn:schemas-upnp-org:device:MediaRenderer:1"))
    }

    @Test
    fun `identify returns SERVER from SSDP MediaServer because server matches keyword`() {
        // "MediaServer" lowercased contains "server" which is a SERVER keyword
        assertEquals(DeviceType.SERVER, DeviceType.identify(ssdpDeviceType = "urn:schemas-upnp-org:device:MediaServer:1"))
    }

    @Test
    fun `identify returns ROUTER from SSDP InternetGatewayDevice`() {
        assertEquals(DeviceType.ROUTER, DeviceType.identify(ssdpDeviceType = "urn:schemas-upnp-org:device:InternetGatewayDevice:1"))
    }

    @Test
    fun `identify returns PRINTER from SSDP Printer`() {
        assertEquals(DeviceType.PRINTER, DeviceType.identify(ssdpDeviceType = "urn:schemas-upnp-org:device:Printer:1"))
    }

    @Test
    fun `identify returns UNKNOWN for unrecognized SSDP type`() {
        assertEquals(DeviceType.UNKNOWN, DeviceType.identify(ssdpDeviceType = "urn:schemas-upnp-org:device:UnknownDevice:1"))
    }

    // --- Phase priority: keyword beats mDNS beats SSDP ---

    @Test
    fun `identify keyword beats mDNS service type`() {
        assertEquals(DeviceType.ROUTER, DeviceType.identify(
            hostname = "cisco-router",
            mdnsServiceType = "_printer._tcp"
        ))
    }

    @Test
    fun `identify mDNS beats SSDP`() {
        assertEquals(DeviceType.PRINTER, DeviceType.identify(
            mdnsServiceType = "_printer._tcp",
            ssdpDeviceType = "urn:schemas-upnp-org:device:MediaRenderer:1"
        ))
    }

    // --- Case sensitivity ---

    @Test
    fun `identify is case insensitive for keywords`() {
        assertEquals(DeviceType.ROUTER, DeviceType.identify(hostname = "MY-ROUTER"))
    }

    @Test
    fun `identify mDNS checks are case sensitive`() {
        // The mDNS Phase 2 checks use the raw parameter, not lowercased
        // "_AIRPLAY._TCP" does NOT contain lowercase "_airplay"
        assertEquals(DeviceType.UNKNOWN, DeviceType.identify(mdnsServiceType = "_AIRPLAY._TCP"))
    }

    // --- Samsung Galaxy edge case ---

    @Test
    fun `identify returns SMARTPHONE for Samsung Galaxy`() {
        assertEquals(DeviceType.SMARTPHONE, DeviceType.identify(vendor = "Samsung", hostname = "galaxy-s21"))
    }
}
