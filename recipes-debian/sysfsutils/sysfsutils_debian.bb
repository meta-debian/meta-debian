SUMMARY = "Tools for working with sysfs"
DESCRIPTION = "Tools for working with the sysfs virtual filesystem.  The tool 'systool' can query devices by bus, class and topology."
HOMEPAGE = "http://linux-diag.sourceforge.net/Sysfsutils.html"

LICENSE = "GPLv2 & LGPLv2.1"
LICENSE_${PN} = "GPLv2"
LICENSE_libsysfs = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=3d06403ea54c7574a9e581c6478cc393 \
                    file://cmd/GPL;md5=d41d4e2e1e108554e0388ea4aecd8d27 \
                    file://lib/LGPL;md5=b75d069791103ffe1c0d6435deeff72e"
PR = "r1"
inherit debian-package
PV = "2.1.0+repack"

SRC_URI += " \
	file://obsolete_automake_macros.patch \
	file://separatebuild.patch \
"

inherit autotools

# Follow Debian
EXTRA_OECONF += "--libdir=${base_libdir}"
do_install_append() {
	# Follow debian/rules
	mkdir -p ${D}${libdir}
	rm ${D}${base_libdir}/libsysfs.so
	ln -s ../..${base_libdir}/libsysfs.so.2 ${D}${libdir}/libsysfs.so
	mv ${D}${base_libdir}/libsysfs.a ${D}${libdir}

	chrpath -d ${D}${bindir}/*

	# Correct libsysfs.la after moving libsysfs.so
	sed -i -e "s:^libdir=.*:libdir=${libdir}:g" ${D}${base_libdir}/libsysfs.la

	mkdir -p ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/sysfsutils.init ${D}${sysconfdir}/init.d/sysfsutils

	# Follow debian/sysfsutils.install
	install -m 0644 ${S}/debian/sysfs.conf ${D}${sysconfdir}/

	# Follow debian/sysfsutils.dirs
	install -d ${D}${sysconfdir}/sysfs.d

	# Follow debian/libsysfs-dev.install
	install -d ${D}${docdir}/libsysfs-dev/ ${D}${libdir}/pkgconfig/
	cp ${S}/docs/libsysfs.txt ${D}${docdir}/libsysfs-dev/
	cp ${S}/debian/local/libsysfs.pc ${D}${libdir}/pkgconfig/
}

PACKAGES =+ "libsysfs"
FILES_libsysfs = "${base_libdir}/lib*${SOLIBS}"

DEBIANNAME_${PN}-dev = "libsysfs-dev"
