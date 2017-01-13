SUMMARY = "GNU grep, egrep and fgrep"
DESCRIPTION = "\
	grep is a utility to search for text in files; it can be used from the 	\
	command line or in scripts.  Even if you don't want to use it, other packages \
	on your system probably will. 						\
	The GNU family of grep utilities may be the 'fastest grep in the west'. \
	GNU grep is based on a fast lazy-state deterministic matcher (about 	\
	twice as fast as stock Unix egrep) hybridized with a Boyer-Moore-Gosper \
	search for a fixed string that eliminates impossible text from being 	\
	considered by the full regexp matcher without necessarily having to 	\
	look at every character. The result is typically many times faster 	\
	than Unix grep or egrep. (Regular expressions containing backreferencing\
	will run more slowly, however.)						\
"
HOMEPAGE = "http://www.gnu.org/software/grep/"
PR = "r0"
inherit debian-package
PV = "2.20"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=8006d9c814277c1bfc4ca22af94b59ee"
inherit autotools-brokensep pkgconfig gettext
DEPENDS += "libpcre"

#--disable-gcc-warnings to building without -Werror, fix ARM cast-align Error
EXTRA_OECONF += "--disable-gcc-warnings --without-included-regex"

#install follow Debian jessie
do_install_append() {
	oe_runmake -C ${S}/po install-data-yes DESTDIR=${D}

	install -d ${D}${base_bindir}
	mv ${D}${bindir}/egrep ${D}${bindir}/fgrep ${D}${bindir}/grep \
		${D}${base_bindir}
	install -m 0755 ${S}/debian/rgrep ${D}${bindir}/
}
FILES_${PN} += "${datadir}/locale/*"
