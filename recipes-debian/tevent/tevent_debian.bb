SUMMARY = "talloc-based event loop library"
DESCRIPTION = "tevent is a simple library that can handle the main event loop for an\n\
application. It supports three kinds of events: timed events, file\n\
descriptors becoming readable or writable and signals.\n\
.\n\
Talloc is used for memory management, both internally and for private\n\
data provided by users of the library."
HOMEPAGE = "http://tevent.samba.org/"

inherit debian-package

PV= "0.9.28"

LICENSE = "LGPLv3+ & BSD-3-Clause & ISC & PostgreSQL"
LIC_FILES_CHKSUM = "\
	file://tevent.h;endline=26;md5=4e458d658cb25e21efc16f720e78b85a \
	file://lib/replace/inet_pton.c;endline=16;md5=d202009b255fffe6953a0d61c2d6fab0 \
	file://lib/replace/timegm.c;endline=32;md5=e1ed216ea15ed1643a625c6725e00e40 \
	file://lib/replace/getaddrinfo.c;endline=26;md5=2a9c0f540bb750338375017f470050c5 \
"

inherit waf-samba pkgconfig

DEPENDS = "talloc libaio"

EXTRA_OECONF += " \
	--disable-rpath \
	--disable-rpath-install \
	--with-libiconv=${STAGING_DIR_HOST}${prefix} \
	--minimum-library-version=${MIN_VER}"

do_configure_prepend () {
	# Get minimum-library-version follow debian/rules
	MIN_VER=$(./debian/autodeps.py --minimum-library-version .)
}

do_install_append() {
	# No python for now
	rm -rf ${D}${libdir}/${PYTHON_DIR}
}

RPROVIDES_${PN} += "libtevent0"
RPROVIDES_${PN}-dev += "libtevent-dev"
RDEPENDS_${PN}-dev += "libtalloc-dev"

BBCLASSEXTEND = "native"
