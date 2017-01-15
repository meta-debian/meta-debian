#
# base recipe: meta/recipes-extended/mingetty/mingetty_1.08.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "1.08"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0c56db0143f4f80c369ee3af7425af6e"

# Define CFLAGS follow debian/rules
EXTRA_OEMAKE = " \
	CC='${CC}' \
	CFLAGS='${CFLAGS} -D_PATH_LOGIN=${base_bindir}/login -D_GNU_SOURCE=1' \
"

do_install(){
	install -d ${D}${mandir}/man8 ${D}/${base_sbindir}
	oe_runmake install DESTDIR=${D}
}
