SUMMARY = "NetBSD make"
DESCRIPTION = "bmake is a program designed to simplify the maintenance of other \
programs. Its input is a list of specifications as to the files upon \
which programs and other files depend.  mkdep, a program to construct \
Makefile dependency lists, is also included."
HOMEPAGE = "http://www.crufty.net/help/sjg/bmake.html"

inherit debian-package
PV = "20140620"

LICENSE = "BSD-3-Clause & BSD-4-Clause"
LIC_FILES_CHKSUM = "file://make.h;beginline=4;endline=70;md5=d6d25d369ff15e28ebaee4a6306a2e57"

inherit autotools native

do_configure() {
	oe_runconf
}

# bmake is stripped by default, this causes QA warning while stripping it
# from do_populate_sysroot()
export STRIP_FLAG = ""

do_install_append (){
	install -m 0755 ${S}/debian/mkdep ${D}${bindir}
	ln -sf bmake ${D}${bindir}/pmake
}
# Disable parallel make
PARALLEL_MAKE = ""
