PR = "r0"

inherit debian-package
PV = "1.2.20"

LICENSE = "NCSA"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=61cbac6719ae682ce6cd45b5c11e21af \
	file://COPYRIGHT;md5=61cbac6719ae682ce6cd45b5c11e21af \
"

inherit autotools

# Avoid strip
EXTRA_OEMAKE += "INSTALL_PROGRAM='${INSTALL}'"

do_install_append (){
	# Debian don't need binary from libtar
	rm -rf ${D}${bindir}
}
