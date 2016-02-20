SUMMARY = "grep-like program specifically for large source trees"
DESCRIPTION = "\
	Ack is designed as an alternative for 99% of the uses of grep. ack is 	\
	intelligent about the files it searches. It knows about certain file 	\
	types, based on both the extension on the file and, in some cases, the 	\
	contents of the file. \
	Ack ignores backup files and files under CVS and .svn directories. It 	\ 
	also highlights matches to help you see where the match was. Ack uses	\ 
	perl regular expressions."
HOMEPAGE = "http://beyondgrep.com/"
PR = "r0"
inherit debian-package cpan

LICENSE = "Artistic-2.0 & GPL-2+ | GPL-1+"
LIC_FILES_CHKSUM = "\
	file://LICENSE.md;md5=f19ee102af436a61316db71e613ded2c \
	file://t/swamp/c-source.c;md5=4434b7963eee9c9432761f5124202e1c \
	file://t/swamp/c-header.h;md5=e419183d1776291d257bb6c47e35bfe4"

do_install() {
	oe_runmake install INSTALLVENDORLIB=${datadir}/perl5 DESTDIR=${D}
	ln -s ack ${D}${bindir}/ack-grep
}
FILES_${PN} += "${datadir}"
