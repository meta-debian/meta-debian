#
# base recipe: meta/recipes-core/glib-2.0/glib-2.0_2.56.1.bb
# base branch: master
# base commit: 63a4ff7cf5f7d1671ab85800bc2212dd9cd9748d
#

require recipes-core/glib-2.0/glib.inc

inherit debian-package
PV = "2.56.1"
DPR = "-2"
BPN = "glib2.0"
DSC_URI = "${DEBIAN_MIRROR}/main/g/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=f3d172717d2319bb83b4bb5d19adae0b"
DEBIAN_UNPACK_DIR = "${WORKDIR}/glib-2.56.1"

FILESPATH_append = ":${COREBASE}/meta/recipes-core/glib-2.0/glib-2.0"
SRC_URI += " \
    file://configure-libtool.patch \
    file://run-ptest \
    file://uclibc_musl_translation.patch \
    file://allow-run-media-sdX-drive-mount-if-username-root.patch \
    file://0001-Install-gio-querymodules-as-libexec_PROGRAM.patch \
    file://0001-Test-for-pthread_getname_np-before-using-it.patch \
    file://0010-Do-not-hardcode-python-path-into-various-tools.patch \
"
SRC_URI_append_class-natve = "file://relocate-modules.patch"
