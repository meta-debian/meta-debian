#
# tiny-init
#
# tiny-init provides a tiny init script which depends on
# only busybox and several kernel features.
# This recipe is based on tiny-init.bb in meta-yocto.
#

SUMMARY = "tiny init"
DESCRIPTION = "Tiny init script which depends on only busybox"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PR = "r0"

RDEPENDS_${PN} = "busybox"

SRC_URI = "file://init"

do_configure() {
	:
}

do_compile() {
	:
}

do_install() {
	install -m 0755 ${WORKDIR}/init ${D}
}

FILES_${PN} = "/init"
