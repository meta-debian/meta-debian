SUMMARY = "hierarchical pool based memory allocator"
DESCRIPTION = "A hierarchical pool based memory allocator with destructors. It uses \
reference counting to determine when memory should be freed."
HOMEPAGE = "http://talloc.samba.org/"

inherit debian-package

PV = "2.1.2"

LICENSE = "LGPLv3+ & ISC & BSD-3-Clause & PostgreSQL"
LIC_FILES_CHKSUM = "\
	file://lib/replace/inet_ntop.c;endline=16;md5=d202009b255fffe6953a0d61c2d6fab0 \
	file://lib/replace/inet_pton.c;endline=16;md5=d202009b255fffe6953a0d61c2d6fab0 \
	file://lib/replace/timegm.c;endline=32;md5=e1ed216ea15ed1643a625c6725e00e40 \
	file://lib/replace/getaddrinfo.c;endline=26;md5=2a9c0f540bb750338375017f470050c5 \
"

inherit waf-samba

EXTRA_OECONF += " \
	--with-libiconv=${STAGING_DIR_HOST}${prefix} \
	--disable-rpath \
	--disable-rpath-install \
"

PACKAGES =+ "python-talloc-dev python-talloc"

FILES_python-talloc-dev = "\
	${includedir}/pytalloc.h \
	${libdir}/pkgconfig/pytalloc-util.pc \
	${libdir}/libpytalloc-util.so"
FILES_python-talloc = "\
	${libdir}/${PYTHON_DIR}/dist-packages/talloc.so \
	${libdir}/libpytalloc-util${SOLIBS}"

DEBIAN_NOAUTONAME_python-talloc-dev = "1"
DEBIAN_NOAUTONAME_python-talloc = "1"
RPROVIDES_${PN} += "libtalloc2"
RPROVIDES_${PN}-dev += "libtalloc-dev"
