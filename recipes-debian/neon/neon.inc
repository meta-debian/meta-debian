SUMMARY = "An HTTP and WebDAV client library with a C interface"
HOMEPAGE = "http://www.webdav.org/neon/"
SECTION = "libs"

PR = "r0"

inherit debian-package
PV = "0.30.1"
DPN = "neon27"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://src/COPYING.LIB;md5=f30a9716ef3762e3467a2f62bf790f0a \
                    file://src/ne_utils.h;beginline=1;endline=20;md5=2caca609538eddaa6f6adf120a218037"
DEPENDS = "zlib libxml2 expat"
DEPENDS_class-native = "zlib-native libxml2-native expat-native"

BBCLASSEXTEND = "native"

BINCONFIG = "${bindir}/neon-config"

inherit autotools binconfig-disabled lib_package pkgconfig

EXTRA_OECONF = "--enable-shared                 \
                --enable-static                 \
                --prefix=/usr                   \
                --sysconfdir=/etc               \
                --infodir=/usr/share/info       \
                --mandir=/usr/share/man         \
                --enable-threadsafe-ssl=posix   \
                --with-gssapi                   \
                --with-libxml2                  \
"

do_compile_append() {
	oe_runmake -C test
}

do_install_append() {
	sed -i -e "s:${STAGING_DIR_TARGET}::g" ${D}${libdir}/pkgconfig/*.pc
}
