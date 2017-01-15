# 
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/meta-oe/recipes-core/jpeg/libjpeg-turbo_svn.bb?h=dylan
# base branch: dylan
#

DESCRIPTION = "libjpeg-turbo is a derivative of libjpeg that uses SIMD instructions \
(MMX, SSE2, NEON) to accelerate baseline JPEG compression and decompression"
HOMEPAGE = "http://libjpeg-turbo.org/"

PR = "r0"
inherit debian-package
PV = "1.3.1"

LICENSE ="BSD-3-Clause"
LIC_FILES_CHKSUM = "file://cdjpeg.h;endline=10;md5=1bcf902368f1944039dccd2a3b1b07eb \
                    file://jpeglib.h;endline=14;md5=a08bb0a80f782a9f8da313cc4015ed6f \
                    file://djpeg.c;endline=25;md5=ffaea149a9776cd9da74d6b61a451fa1 \
    "

inherit autotools pkgconfig

EXTRA_OECONF = " --without-simd "

do_install_append (){
	install -m 644 ${S}/debian/extra/exifautotran ${D}${bindir}/exifautotran
}

PACKAGES =+ "libturbojpeg libturbojpeg-dev ${PN}-progs"

FILES_${PN}-progs =  "${bindir}/*"
FILES_libturbojpeg = "${libdir}/libturbojpeg${SOLIBS}"
FILES_libturbojpeg-dev = " \
    ${includedir}/turbojpeg.h \
    ${libdir}/libturbojpeg${SOLIBSDEV} \
"

DEBIANNAME_${PN} = "libjpeg62-turbo"
DEBIANNAME_${PN}-dev = "libjpeg62-turbo-dev"
DEBIANNAME_${PN}-dbg = "libjpeg62-turbo-dbg"
DEBIANNAME_libturbojpeg-dev = "libturbojpeg1-dev"

# Prevent libjpeg-turbo-progs change to libjpeg-progs
DEBIAN_NOAUTONAME_${PN}-progs = "1"

BBCLASSEXTEND = "native"
