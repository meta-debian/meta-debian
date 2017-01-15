#
# base-recipe: meta/recipes-kernel/dtc/dtc_git.bb
# base-branch: daisy
#

PR = "r0"

inherit debian-package autotools-brokensep
PV = "1.4.0+dfsg"

LICENSE = "GPLv2 | BSD"
LIC_FILES_CHKSUM = " \
file://GPL;md5=94d55d512a9ba36caa9b7df079bae19f \
file://libfdt/libfdt.h;beginline=3;endline=52;md5=fb360963151f8ec2d6c06b055bcbb68c \
"

DPN = "device-tree-compiler" 
S = "${WORKDIR}/git"
DEPENDS = "flex-native bison-native"

SRC_URI += " \
file://make_install.patch \
"

EXTRA_OEMAKE='PREFIX="${prefix}" LIBDIR="${libdir}"'

PACKAGES =+ "${PN}-misc"
FILES_${PN}-misc = "${bindir}/convert-dtsv0 ${bindir}/ftdump ${bindir}/dtdiff"

BBCLASSEXTEND = "native nativesdk"

# alway try to apply debian patches by quilt
DEBIAN_PATCH_TYPE = "quilt"
