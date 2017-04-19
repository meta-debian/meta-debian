# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/\
#tree/meta-oe/recipes-support/libutempter/libutempter.bb?h=master
# base branch: master

SUMMARY = "A privileged helper for utmp/wtmp updates"
DESCRIPTION = "\
This library provides interface for terminal emulators such as \
screen and xterm to record user sessions to utmp and wtmp files."
HOMEPAGE = "ftp://ftp.altlinux.org/pub/people/ldv/utempter"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

PR = "r0"
inherit debian-package
PV = "1.1.5"

# patch file for fix macro error
# compile error when build on almost all architectures
SRC_URI += "file://0001-Fix-macro-error.patch"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

CFLAGS += "-DLIBEXECDIR=${libexecdir}"

do_compile() {
	oe_runmake                      \
		libdir=${libdir}            \
		libexecdir=${libexecdir}
}

do_install() {
	oe_runmake install              \
		DESTDIR=${D}                \
		libdir="${libdir}"          \
		libexecdir="${libexecdir}"  \
		includedir=${includedir}    \
		mandir=${mandir}
}

FILES_${PN} += " \
	${libdir}/*.so.* \
	${libexecdir}/utempter/utempter \
    "

FILES_${PN}-dbg += "${libexecdir}/utempter/.debug/utempter"
