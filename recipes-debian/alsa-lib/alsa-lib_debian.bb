#
# base recipe: meta/recipes-multimedia/alsa/alsa-lib_1.0.27.2.bb
# base branch: daisy
#

PR = "r1"

inherit debian-package
PV = "1.0.28"

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
	--with-plugindir=${libdir}/${DPN} \
"

EXTRA_OECONF_append_libc-uclibc = " --with-versioned=no "

do_install_append() {
	# Remove unneeded files
	find ${D} -type f -name "*.la" -exec rm -f {} \;
	rm -f ${D}${bindir}/aserver
	rmdir --ignore-fail-on-non-empty ${D}${bindir}
}

PACKAGES =+ "libasound2-data"
FILES_libasound2-data = "${datadir}/alsa"

RDEPENDS_${PN}_class-target += "libasound2-data"

RPROVIDES_${PN} += "libasound2"
RPROVIDES_${PN}-dbg += "libasound2-dbg"
RPROVIDES_${PN}-dev += "libasound2-dev"
RPROVIDES_${PN}-doc += "libasound2-doc"
PKG_${PN} = "libasound2"
PKG_${PN}-dbg = "libasound2-dbg"
PKG_${PN}-dev = "libasound2-dev"
PKG_${PN}-doc = "libasound2-doc"

def get_alsa_fpu_setting(bb, d):
    if d.getVar('TARGET_FPU', True) in [ 'soft' ]:
        return "--with-softfloat"
    return ""

BBCLASSEXTEND = "native nativesdk"
