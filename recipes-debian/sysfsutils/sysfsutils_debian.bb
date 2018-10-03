#
# base recipe: meta/recipes-core/sysfsutils/sysfsutils_2.1.0.bb
# base branch: master
# base commit: dbda297fd91aab2727f7a69d3b7d3a32ad4261d2
#

SUMMARY = "Tools for working with sysfs"
DESCRIPTION = "Tools for working with the sysfs virtual filesystem.  The tool 'systool' can query devices by bus, class and topology."
HOMEPAGE = "http://linux-diag.sourceforge.net/Sysfsutils.html"

LICENSE = "GPLv2 & LGPLv2.1"
LICENSE_${PN} = "GPLv2"
LICENSE_libsysfs = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=3d06403ea54c7574a9e581c6478cc393 \
                    file://cmd/GPL;md5=d41d4e2e1e108554e0388ea4aecd8d27 \
                    file://lib/LGPL;md5=b75d069791103ffe1c0d6435deeff72e"

inherit debian-package
require recipes-debian/sources/sysfsutils.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-core/sysfsutils/sysfsutils-2.1.0"
SRC_URI += " \
	file://obsolete_automake_macros.patch \
	file://separatebuild.patch \
"

inherit autotools

PACKAGES =+ "libsysfs"
FILES_libsysfs = "${libdir}/lib*${SOLIBS}"

export libdir = "${base_libdir}"
