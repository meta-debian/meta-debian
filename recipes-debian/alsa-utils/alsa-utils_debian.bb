require recipes-multimedia/alsa/${PN}_1.0.27.2.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-multimedia/alsa/alsa-utils:"

inherit debian-package
DEBIAN_SECTION = "sound"
DPR = "0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SRC_URI += " \
file://0001-alsactl-don-t-let-systemd-unit-restore-the-volume-wh_debian.patch \
"
