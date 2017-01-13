# base recipe: meta-openembedded/meta-oe/recipes-support/tree/tree_1.7.0.bb
# base branch: master
# base commit: e5f75c657e22adc69712846f8fd704fd01fbdf6a

SUMMARY = "A recursive directory listing command"
HOMEPAGE = "http://mama.indstate.edu/users/ice/tree/"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=393a5ca445f6965873eca0259a17f833"

PR = "r0"
inherit debian-package
PV = "1.7.0"

# tree's default CFLAGS for Linux
CFLAGS += "-Wall -DLINUX -D_LARGEFILE64_SOURCE -D_FILE_OFFSET_BITS=64"

EXTRA_OEMAKE = "CC='${CC}' CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/${BPN} ${D}${bindir}/
}

BBCLASSEXTEND = "native"
