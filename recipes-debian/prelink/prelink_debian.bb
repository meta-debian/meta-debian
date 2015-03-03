require recipes-devtools/prelink/prelink_git.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/prelink/prelink:"

inherit debian-package

DEBIAN_SECTION = "admin"
DPR = "1"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

# Patch prelink_cross.diff was created to enable option --root
SRC_URI += " \
file://prelink.conf \
file://prelink.cron.daily \
file://prelink.default \
file://macros.prelink \
file://prelink_cross.diff \
"

# QA Issue: Unrecognised options in version 0.0.20130503:
# --with-pkgversion --disable-selinux --with-bugurl
# FIXME: hardcodedly remove these options.
EXTRA_OECONF = ""
