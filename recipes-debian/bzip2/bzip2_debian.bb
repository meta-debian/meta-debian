#
# base recipe: meta/recipes-extended/bzip2/bzip2_1.0.6.bb
# base recipe: daisy
#

PR = "r1"

inherit debian-package
PV = "1.0.6"

LICENSE = "bzip2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ddeb76cd34e791893c0f539fdab879bb"

SRC_URI += " \
file://configure.ac \
file://Makefile.am \
file://run-ptest \
"

PACKAGES =+ "libbz2 libbz2-dev libbz2-staticdev"

CFLAGS_append = " -fPIC -fpic -Winline -fno-strength-reduce -D_FILE_OFFSET_BITS=64"

inherit autotools ptest

EXTRA_OECONF = "--bindir=${base_bindir}"

#install binaries to bzip2-native under sysroot for replacement-native
EXTRA_OECONF_append_class-native = " --bindir=${STAGING_BINDIR_NATIVE}/${PN}"
do_extraunpack () {
	cp ${WORKDIR}/configure.ac ${S}/
	cp ${WORKDIR}/Makefile.am ${S}/
}

addtask extraunpack after do_unpack before do_patch

do_install_append_class-target() {
	# Install bzexe
	install -m 755 ${S}/bzexe ${D}${base_bindir}/
	cp ${S}/bzexe.1 ${D}${mandir}/man1
}

do_install_ptest () {
	cp -f ${B}/Makefile ${D}${PTEST_PATH}/Makefile
	sed -i -e "s|^Makefile:|_Makefile:|" ${D}${PTEST_PATH}/Makefile
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "bunzip2 bzcat bzip2"
ALTERNATIVE_LINK_NAME[bunzip2] = "${base_bindir}/bunzip2"
ALTERNATIVE_LINK_NAME[bzcat] = "${base_bindir}/bzcat"
ALTERNATIVE_LINK_NAME[bzip2] = "${base_bindir}/bzip2"

FILES_libbz2 = "${libdir}/lib*${SOLIBS}"

FILES_libbz2-dev = "${includedir} ${libdir}/lib*${SOLIBSDEV}"
SECTION_libbz2-dev = "devel"
RDEPENDS_libbz2-dev = "libbz2 (= ${EXTENDPKGV})"

FILES_libbz2-staticdev = "${libdir}/*.a"
SECTION_libbz2-staticdev = "devel"
RDEPENDS_libbz2-staticdev = "libbz2-dev (= ${EXTENDPKGV})"

PROVIDES_append_class-native = " bzip2-replacement-native"
BBCLASSEXTEND = "native nativesdk"
