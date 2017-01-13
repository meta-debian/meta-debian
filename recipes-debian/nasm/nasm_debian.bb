#
# base recipe: meta/recipes-devtools/nasm/nasm_2.11.08.bb
# base branch: jethro
#

SUMMARY = "General-purpose x86 assembler"
DESCRIPTION = "Netwide Assembler.  NASM will currently output flat-form binary files,\n\
a.out, COFF and ELF Unix object files, and Microsoft 16-bit DOS and\n\
Win32 object files.\n\
.\n\
Also included is NDISASM, a prototype x86 binary-file disassembler\n\
which uses the same instruction table as NASM."
HOMEPAGE = "http://nasm.sourceforge.net/"

inherit debian-package
PV = "2.11.05"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90904486f8fbf1861cf42752e1a39efe"

DEPENDS = "groff-native"

COMPATIBLE_HOST = '(x86_64|i.86).*-(linux|freebsd.*)'

inherit autotools-brokensep

do_configure_prepend() {
	if [ -f ${S}/aclocal.m4 ] && [ ! -f ${S}/acinclude.m4 ]; then
		mv ${S}/aclocal.m4 ${S}/acinclude.m4
	fi
}

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${mandir}/man1

	oe_runmake 'INSTALLROOT=${D}' install install_rdf
}

BBCLASSEXTEND = "native"
