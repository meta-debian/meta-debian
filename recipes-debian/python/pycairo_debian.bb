SUMMARY = "Python bindings for the Cairo vector graphics library"
DISCRIPTION = "This package contains modules that allow you to use the Cairo vector \
 graphics library in Python programs"
HOMEPAGE = "http://cairographics.org/pycairo"

inherit debian-package
LICENSE = "LGPLv2.1 & MPL-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=f2e071ab72978431b294a0d696327421 \
                    file://COPYING-LGPL-2.1;md5=fad9b3332be894bab9bc501572864b29 \
                    file://COPYING-MPL-1.1;md5=bfe1f75d606912a4111c90743d6c7325"

DEPENDS = "cairo"
PR = "r0"

inherit distutils pkgconfig

do_install_append () {
	mv ${D}${datadir}/lib/* ${D}${libdir}
	mv ${D}${datadir}/include ${D}${prefix}
	rm -rf ${D}${datadir}
}
BBCLASSEXTEND = "native"
