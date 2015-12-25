#
# base recipe: https://github.com/openembedded/openembedded-core/blob/master/
# meta/recipes-support/libcap-ng/libcap-ng_0.7.7.bb
# base branch: master
#

PR = "r0"

inherit debian-package

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
		    file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

inherit lib_package autotools pythonnative

DEPENDS += "swig-native python"

PACKAGES =+ "libcap-ng-utils"

FILES_${PN}-utils = "${bindir}/captest ${bindir}/filecap ${bindir}/netcap ${bindir}/pscap ${datadir}/man/man8"

BBCLASSEXTEND = "native"
