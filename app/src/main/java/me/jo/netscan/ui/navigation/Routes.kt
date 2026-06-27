package me.jo.netscan.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class DeviceDetail(val deviceId: String)

@Serializable
object Settings

@Serializable
object CustomPorts
