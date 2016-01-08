DESCRIPTION = "SELinux runtime shared libraries"

HOMEPAGE =  "http://packages.debian.org/source/sid/libs/libselinux"

PR = "r0"

inherit debian-package python-dir

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84b4d2c6ef954a2d4081e775a270d0d0"

DEPENDS += "libsepol libpcre swig-native python"

#
# Makefile_debian.patch:
#	Patch file to include libsepol.a from sysroot for cross-compiling
SRC_URI += "file://Makefile_debian.patch"

do_compile() {
	export INCLUDEDIR=${STAGING_INCDIR}
	export LIBDIR=${STAGING_LIBDIR}
	sed -i -e "s:##STAGING_LIBDIR##:${STAGING_LIBDIR}:g" ${S}/src/Makefile
	oe_runmake PREFIX="${prefix}" LIBBASE="${base_lib}" all
}

do_install() {
	oe_runmake install install-pywrap install-pywrap DESTDIR="${D}"
	if [ ! `ls -A ${D}${base_sbindir}` ]; then
		rm -r ${D}${base_sbindir}
	fi
}

# Add package follow debian
PACKAGES =+ "selinux-utils python-selinux"

FILES_selinux-utils += "${sbindir}/*"
FILES_python-selinux += "${PYTHON_SITEPACKAGES_DIR}/selinux/*"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/selinux/.debug"

# Correct .deb files
DEBIANAME_${PN} = "${PN}1"
PKG_${PN}-dev = "${PN}1-dev"
