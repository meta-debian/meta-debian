#
# base recipe: meta/recipes-multimedia/alsa/alsa-lib_1.0.27.2.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34 \
	file://src/conf/sndo-mixer.alisp;beginline=4;endline=5;md5=74fd906517c182ba9b673a01fd1cc412 \
"

# make Check-if-wordexp-function-is-supported.patch compatible with
# debian version
#
# exclude Update-iatomic.h-functions-definitions-for-mips.patch
# because atomic_add, atomic_sub, atomic_add_return, atomic_sub_return
# were already removed from iatomic.h
#
# exclude fix-tstamp-declaration.patch because there is no error
# "error: field 'tstamp' has incomplete type" 
SRC_URI += " \
file://Check-if-wordexp-function-is-supported_debian.patch \
"

# configure.in sets -D__arm__ on the command line for any arm system
# (not just those with the ARM instruction set), this should be removed,
# (or replaced by a permitted #define).
#FIXME: remove the following
ARM_INSTRUCTION_SET = "arm"

inherit autotools pkgconfig

EXTRA_OECONF = " \
	${@get_alsa_fpu_setting(bb, d)} \
	--disable-static --disable-python \
"

EXTRA_OECONF_append_libc-uclibc = " --with-versioned=no "

PACKAGES =+ "alsa-server libasound alsa-conf-base alsa-conf alsa-doc alsa-dev"
FILES_${PN} += "${libdir}/${BPN}/smixer/*.so"
FILES_${PN}-dbg += "${libdir}/${BPN}/smixer/.debug"
FILES_${PN}-dev += "${libdir}/${BPN}/smixer/*.la"
FILES_libasound = "${libdir}/libasound.so.*"
FILES_alsa-server = "${bindir}/*"
FILES_alsa-conf = "${datadir}/alsa/"
FILES_alsa-dev += "${libdir}/pkgconfig/ ${includedir}/alsa ${datadir}/aclocal/*"
FILES_alsa-conf-base = "\
${datadir}/alsa/alsa.conf \
${datadir}/alsa/cards/aliases.conf \
${datadir}/alsa/pcm/default.conf \
${datadir}/alsa/pcm/dmix.conf \
${datadir}/alsa/pcm/dsnoop.conf"

RDEPENDS_libasound = "alsa-conf-base"

def get_alsa_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU', True) in [ 'soft' ]:
        return "--with-softfloat"
    return ""

BBCLASSEXTEND = "native nativesdk"
