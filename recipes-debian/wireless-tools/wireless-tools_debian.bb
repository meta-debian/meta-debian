require recipes-connectivity/wireless-tools/wireless-tools_30.pre9.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-connectivity/wireless-tools/wireless-tools:\
"

inherit debian-package
DEBIAN_SECTION = "net"
DPR = "0"

# Exclude file://man.patch from SRC_URI of wireless-tools_30.pre9.bb because
# man is already installed in share folder
# Exclude file://ldflags.patch from SRC_URI of wireless-tools_30.pre9.bb
# because the file is for fixing a warning that we don't have
SRC_URI += " \
file://wireless-tools.if-pre-up \
file://zzz-wireless.if-pre-up \
file://remove.ldconfig.call.patch \
file://avoid_strip.patch \
"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
