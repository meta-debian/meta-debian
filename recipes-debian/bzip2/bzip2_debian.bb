#
# base recipe: meta/recipes-extended/bzip2/bzip2_1.0.6.bb
# base branch: master
# base commit: a5d1288804e517dee113cb9302149541f825d316
#

SUMMARY = "Very high-quality data compression program"
DESCRIPTION = "bzip2 compresses files using the Burrows-Wheeler block-sorting text compression algorithm, and \
Huffman coding. Compression is generally considerably better than that achieved by more conventional \
LZ77/LZ78-based compressors, and approaches the performance of the PPM family of statistical compressors."
HOMEPAGE = "http://www.bzip.org/"

inherit debian-package
require recipes-debian/sources/bzip2.inc

LICENSE = "bzip2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ddeb76cd34e791893c0f539fdab879bb"

FILESPATH_append = ":${COREBASE}/meta/recipes-extended/bzip2/bzip2-1.0.6"
SRC_URI += " \
file://configure.ac;subdir=${S} \
file://Makefile.am;subdir=${S} \
file://run-ptest \
"

PACKAGES =+ "libbz2"

CFLAGS_append = " -fPIC -fpic -Winline -fno-strength-reduce -D_FILE_OFFSET_BITS=64"

inherit autotools ptest relative_symlinks

#install binaries to bzip2-native under sysroot for replacement-native
EXTRA_OECONF_append_class-native = " --bindir=${STAGING_BINDIR_NATIVE}/${PN}"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "bunzip2 bzcat bzip2"

do_install_ptest () {
	sed -i -e "s|^Makefile:|_Makefile:|" ${D}${PTEST_PATH}/Makefile
}

FILES_libbz2 = "${libdir}/lib*${SOLIBS}"

PROVIDES_append_class-native = " bzip2-replacement-native"
BBCLASSEXTEND = "native nativesdk"
