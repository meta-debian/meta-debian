SUMMARY = "Stream EDitor (text filtering utility)"
HOMEPAGE = "http://www.gnu.org/software/sed/"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e \
                    file://sed/sed.h;beginline=1;endline=17;md5=8841c3ba72de6f3ac9657381b98a604e"
SECTION = "console/utils"

inherit debian-package
require recipes-debian/sources/sed.inc
#FILESPATH_append = ":${COREBASE}/meta/recipes-extended/sed/sed-4.2.2"
#FILESEXTRAPATHS_prepend := "${THISDIR}/sed:"

# source format is 3.0 (quilt) but there is no debian/patches
DEBIAN_QUILT_PATCHES = ""

inherit autotools texinfo update-alternatives gettext

EXTRA_OECONF = "--disable-acl"

do_install () {
	autotools_do_install
	install -d ${D}${base_bindir}
	if [ ! ${D}${bindir} -ef ${D}${base_bindir} ]; then
	    mv ${D}${bindir}/sed ${D}${base_bindir}/sed
	    rmdir ${D}${bindir}/
	fi
}

ALTERNATIVE_${PN} = "sed"
ALTERNATIVE_LINK_NAME[sed] = "${base_bindir}/sed"
ALTERNATIVE_PRIORITY = "100"
