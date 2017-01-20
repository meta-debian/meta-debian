SUMMARY = "GL widget library for Athena and Motif"
HOMEPAGE = "http://mesa3d.sourceforge.net/"

inherit debian-package
PV = "8.0.0"

LICENSE = "SGI-1"
LIC_FILES_CHKSUM = "file://README;beginline=22;md5=79f8740be8fa859f7e489a288be7fa2a"

DEPENDS = "virtual/libgl virtual/libx11 libxt motif"

DEBIAN_PATCH_TYPE = "quilt"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-motif"

do_install_append() {
	# Remove unused files
	rm -f ${D}${includedir}/GL/*P.h
}

DEBIANNAME_${PN} = "lib${PN}1-mesa"
DEBIANNAME_${PN}-dev = "lib${PN}1-mesa-dev"
RPROVIDES_${PN} += "lib${PN}-mesa"
RPROVIDES_${PN}-dev += "lib${PN}-mesa-dev"
