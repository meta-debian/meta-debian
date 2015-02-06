require recipes-extended/xz/xz_5.1.3alpha.bb

DPN = "xz-utils"
inherit debian-package
DEBIAN_SECTION = "utils"
DPR = "0"

LICENSE = "GPLv2+ & GPLv3+ & LGPLv2.1+ & PD"
LICENSE_${PN} = "GPLv2+"
LICENSE_${PN}-dev = "GPLv2+"
LICENSE_${PN}-staticdev = "GPLv2+"
LICENSE_${PN}-doc = "GPLv2+"
LICENSE_${PN}-dbg = "GPLv2+"
LICENSE_${PN}-locale = "GPLv2+"
LICENSE_liblzma = "PD"
LICENSE_liblzma-dev = "PD"
LICENSE_liblzma-staticdev = "PD"
LICENSE_liblzma-dbg = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=c475b6c7dca236740ace4bba553e8e1c"

# generate build-aux/config.rpath so autoreconf can see it
do_configure_prepend() {
	cd ${S}
	./autogen.sh && cd -
}
