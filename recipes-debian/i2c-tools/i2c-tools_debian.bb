#
# base recipe: meta/recipes-devtools/i2c-tools/i2c-tools_3.1.2.bb
# base branch: jethro
#
SUMMARY = "heterogeneous set of I2C tools for Linux"
DESCRIPTION = "\
	This package contains a heterogeneous set of I2C tools for Linux: a bus \
	probing tool, a chip dumper, register-level access helpers, EEPROM \
	decoding scripts, and more. \
"
HOMEPAGE = "http://www.lm-sensors.org"

PR = "r0"
inherit debian-package pythonnative

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
inherit autotools-brokensep

do_compile_append() {
	export BUILD_SYS=${BUILD_SYS}
	export HOST_SYS=${HOST_SYS}
	export STAGING_INCDIR=${STAGING_INCDIR}
	cd py-smbus && \
	CFLAGS="$CFLAGS ${HOST_CC_ARCH} -I../include" \
	${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py build
}

#include/linux/i2c-dev.h file conflict with linux-libc-headers-base
#solve this follow base recipe
do_install() {
	oe_runmake install DESTDIR=${D} prefix=/usr\
	install -d ${D}${includedir}/linux
	install -m 0644 include/linux/i2c-dev.h ${D}${includedir}/linux/i2c-dev-user.h
	rm -f ${D}${includedir}/linux/i2c-dev.h
}
