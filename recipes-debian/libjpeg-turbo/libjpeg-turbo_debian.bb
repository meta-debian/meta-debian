# 
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/meta-oe/recipes-core/jpeg/libjpeg-turbo_svn.bb?h=dylan
# base branch: dylan
#

DESCRIPTION = "libjpeg-turbo is a derivative of libjpeg that uses SIMD instructions \
(MMX, SSE2, NEON) to accelerate baseline JPEG compression and decompression"
HOMEPAGE = "http://libjpeg-turbo.org/"

PR = "r0"
inherit debian-package

LICENSE ="BSD-3-Clause"
LIC_FILES_CHKSUM = "file://cdjpeg.h;endline=10;md5=1bcf902368f1944039dccd2a3b1b07eb \
                    file://jpeglib.h;endline=14;md5=a08bb0a80f782a9f8da313cc4015ed6f \
                    file://djpeg.c;endline=25;md5=ffaea149a9776cd9da74d6b61a451fa1 \
    "
# Drop-in replacement for jpeg
PROVIDES = "jpeg"
RPROVIDES_${PN} += "jpeg"
RREPLACES_${PN} += "jpeg"
RCONFLICTS_${PN} += "jpeg"

inherit autotools pkgconfig

EXTRA_OECONF = " --without-simd "

do_install_append (){
	install -m 644 ${S}/debian/extra/exifautotran ${D}${bindir}/exifautotran
}

PACKAGES =+ " libturbojpeg1-dev ${PN}-progs libjpeg62-turbo libjpeg62-turbo-dev libturbojpeg1 "

FILES_${PN}-progs =  "${bindir}/*"

FILES_libjpeg62-turbo = "${libdir}/libjpeg.so.62*"

FILES_libjpeg62-turbo-dev = "${includedir}/* \
	${libdir}/libjpeg.so \
	${libdir}/pkgconfig/libjpeg.pc \	
    "

FILES_libturbojpeg1 = " ${libdir}/libturbojpeg.so.1* "

FILES_libturbojpeg1-dev = " ${includedir}/turbojpeg.h \
	${libdir}/libturbojpeg.so \
   " 

BBCLASSEXTEND = "native"

LEAD_SONAME = "libjpeg.so.62"
