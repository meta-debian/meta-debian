#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-java/tree/recipes-core/fastjar/fastjar_0.98.bb
# base branch: master
# base commit: 5b5a40333ad25151f736741c97e5712975970b8e
#

PR = "r0"
DESCRIPTION = ".jar creation program written in C."

inherit debian-package
PV = "0.98"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "zlib"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools update-alternatives

PROVIDES = "virtual/jar"
ALTERNATIVE_${PN} = "jar"
ALTERNATIVE_LINK = "${bindir}/jar"
ALTERNATIVE_TARGET = "${bindir}/fastjar"

# update-aternatives does not work for native class
do_install_append_class-native () {
	ln -s fastjar ${D}${bindir}/jar
}

BBCLASSEXTEND = "native"
