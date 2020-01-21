# base recipe: meta-openembedded/meta-oe/recipes-extended/liblockfile/liblockfile_1.14.bb
# base branch: warrior
# base commit: af574eed885a42146458e3a5e3fd83a172b69fa9

SUMMARY = "File locking library"
HOMEPAGE = "http://packages.qa.debian.org/libl/liblockfile.html"
SECTION = "libs"
LICENSE = "LGPLv2+ & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=f4ba6ad04fcb05cc30a4cfa5062c55a3"

inherit debian-package
require recipes-debian/sources/liblockfile.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}"

SRC_URI += " \
    file://configure.patch \
    file://0001-Makefile.in-add-DESTDIR.patch \
    file://0001-Makefile.in-install-nfslock.so-and-nfslock.so.0.patch \
    file://liblockfile-fix-install-so-to-man-dir.patch \
"

inherit autotools-brokensep

# set default mailgroup to mail
# --with-libnfslock specify where to install nfslock.so.NVER
EXTRA_OECONF = "--enable-shared \
                --with-mailgroup=mail \
                --with-libnfslock=${libdir} \
"

# Makefile using DESTDIR as the change in e35f9eabcbba224ecc70b145d5d2a2d81064c195
# at https://github.com/miquels/liblockfile.git
EXTRA_OEMAKE += "DESTDIR=${D}"

FILES_${PN} += "${libdir}/nfslock.so.*"
FILES_${PN}-dev += "${libdir}/nfslock.so"
