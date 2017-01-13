#
# No base recipe
#

PR = "r0"

inherit debian-package
PV = "0.9.7"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b8c006c6415de700eaa7b10661ebbaab"

inherit autotools

# Souce format is 3.0 but there is no debian patch files
DEBIAN_QUILT_PATCHES = ""

do_install_append () {
	#Create softlink follow Debian
	ln -sf pkgconf ${D}${bindir}/pkg-config
}
