SUMMARY = "conservative garbage collector for C and C++"
DESCRIPTION = "oehm-Demers-Weiser's GC is a garbage collecting storage allocator that is\n\
 intended to be used as a plug-in replacement for C's malloc or C++'s new().\n\
 .\n\
 It allows you to allocate memory basically as you normally would without\n\
 explicitly deallocating memory that is no longer useful. The collector\n\
 automatically recycles memory when it determines that it can no longer be\n\
 used.\n\
 .\n\
 This version of the collector is thread safe, has C++ support and uses the\n\
 defaults for everything else. However, it does not work as a drop-in malloc(3)\n\
 replacement."
HOMEPAGE = "http://www.hpl.hp.com/personal/Hans_Boehm/gc/"

PR = "r0"
inherit debian-package
PV = "7.2d"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://doc/README;endline=37;md5=9f4fd307082acf7a8af3e3897245163b"
inherit autotools pkgconfig

# base on debian/rules
EXTRA_OECONF += "--enable-cplusplus -disable-dependency-tracking \
                 --with-tags=CXX --datadir=${docdir}"
PACKAGECONFIG ??= ""
PACKAGECONFIG[libatomic-ops] = "--with-libatomic-ops=yes,--with-libatomic-ops=no,libatomic-ops"

do_install_append() {
	# remove unwanted files
	rm -rf ${D}${libdir}/libcord*
}
PKG_${PN} = "${PN}1c2"
RPROVIDES_${PN} += "${PN}1c2"
BBCLASSEXTEND = "native"
