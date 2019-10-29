# base recipe: meta/recipes-extended/cracklib/cracklib_2.9.5.bb
# base branch: warrior

SUMMARY = "Password strength checker library"
HOMEPAGE = "https://github.com/cracklib/cracklib"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

inherit debian-package
require recipes-debian/sources/cracklib2.inc

DEPENDS = "cracklib-native"

inherit autotools gettext

EXTRA_OECONF = "--without-python --libdir=${base_libdir}"

do_install_append_class-target() {
	create-cracklib-dict -o ${D}${datadir}/cracklib/pw_dict ${D}${datadir}/cracklib/cracklib-small
}

BBCLASSEXTEND = "native nativesdk"
