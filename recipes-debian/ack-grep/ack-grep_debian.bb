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
PV = "2.14"

LICENSE = "Artistic-2.0 & GPL-2+ & (Artistic-1.0 | GPL-1+)"
LIC_FILES_CHKSUM = "\
	file://LICENSE.md;md5=f19ee102af436a61316db71e613ded2c \
	file://t/swamp/c-source.c;beginline=27;endline=49;md5=e62ea93e397468c8fc0f902203acf8c9 \
	file://t/swamp/c-header.h;beginline=1;endline=9;md5=e855db0451099d570f57f22349c3a809"

do_install() {
	oe_runmake install INSTALLVENDORLIB=${datadir}/perl5 DESTDIR=${D}
	ln -s ack ${D}${bindir}/ack-grep
}
FILES_${PN} += "${datadir}"
RDEPENDS_${PN} += "libfile-next-perl perl"
