SUMMARY = "Compression and decompression in the LZMA format - command line utility"
DESCRIPTION = "LZMA is a general compression method. LZMA provides high \
               compression ratio and very fast decompression."
HOMEPAGE = "http://freecode.com/projects/lcab"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://lzma.txt;beginline=18;endline=31;md5=a6f4e9d10359abb5044bb121f5a051e9"

PR = "r0"

inherit debian-package
PV = "9.22"

export SRC_DIR_C="C/Util/Lzma"
export SRC_DIR_CPP="CPP/7zip/Bundles/LzmaCon"

EXTRA_OEMAKE = "-f makefile.gcc"

do_compile() {
        oe_runmake CXX_C='${CC} ${CFLAGS}' CXX='${CXX} ${CXXFLAGS}' \
         -C ${S}/${SRC_DIR_C}
        oe_runmake CXX_C='${CC} ${CFLAGS}' CXX='${CXX} ${CXXFLAGS}' \
         -C ${S}/${SRC_DIR_CPP}
}

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${S}/${SRC_DIR_CPP}/lzmp ${D}${bindir}/lzmp
        install -m 0755 ${S}/${SRC_DIR_CPP}/lzma_alone ${D}${bindir}/lzma_alone

        install -d ${D}${includedir}/lzma
        install -m 0644 ${S}/C/LzFind.* ${D}${includedir}/lzma/
        install -m 0644 ${S}/C/LzmaDec.* ${D}${includedir}/lzma/
        install -m 0644 ${S}/C/LzmaEnc.* ${D}${includedir}/lzma/
        install -m 0644 ${S}/C/7zVersion.h ${D}${includedir}/lzma/7zVersion.h
        install -m 0644 ${S}/C/Types.h ${D}${includedir}/lzma/Types.h
}

PACKAGES =+ "lzma-alone"
FILES_lzma-alone = "${bindir}/lzma_alone"
