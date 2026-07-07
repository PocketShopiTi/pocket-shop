package com.iti.pocketshop.core.utils

interface PermissionChecker {
    fun hasLocationPermission(): Boolean
    fun hasContactsPermission(): Boolean
}