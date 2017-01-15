#
# base recipe: meta/recipes-devtools/ossp-uuid/ossp-uuid_1.6.2.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "1.6.2"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README;beginline=30;endline=55;md5=b394fadb039bbfca6ad9d9d769ee960e \
	   file://uuid_md5.c;beginline=1;endline=28;md5=9c1f4b2218546deae24c91be1dcf00dd"

SRC_URI += "\
	file://uuid-libtool.patch \
	file://uuid-nostrip.patch \
	file://install-pc.patch \
"

inherit autotools

EXTRA_OECONF = " --without-perl --without-dce --with-cxx \
	--without-perl-compat --without-php --without-pgsql \
	--includedir=${includedir}/ossp"

do_configure_prepend() {
	# This package has a completely custom aclocal.m4, which should be acinclude.m4.
	if [ ! -e ${S}/acinclude.m4 ]; then
		mv ${S}/aclocal.m4 ${S}/acinclude.m4
	fi

	rm -f ${S}/libtool.m4
}

do_install_append() {
	mkdir -p  ${D}${includedir}/ossp
	mv ${D}${libdir}/pkgconfig/uuid.pc ${D}${libdir}/pkgconfig/ossp-uuid.pc
}

PACKAGES =+ "uuid"
FILES_uuid = "${bindir}/uuid"
FILES_${PN} = " \
	${libdir}/libossp-uuid.so.16* \
	${libdir}/libossp-uuid++.so.16* \
"
FILES_${PN}-dev += "${bindir}/uuid-config"

BBCLASSEXTEND = "native nativesdk"
