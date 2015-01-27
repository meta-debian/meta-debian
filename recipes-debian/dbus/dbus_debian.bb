require recipes-core/dbus/dbus_1.6.18.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-core/dbus/${BPN}:"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

LICENSE = "AFL-2.1 & GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=10dded3b58148f3f1fd804b26354af3e"

# Excluded: 
# Set-correct-address-when-using-address-systemd.patch
# fixed-memory-freeing-if-error-during-listing-service.patch
SRC_URI += " \
file://tmpdir.patch \
file://dbus-1.init \
file://os-test.patch \                                               
file://clear-guid_from_server-if-send_negotiate_unix_f.patch \       
"

# To fix QA issue that the file have not ship to any package
FILES_${PN} += "${bindir}/dbus-run-session"
