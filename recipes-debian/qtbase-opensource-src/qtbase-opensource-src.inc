DESCRIPTION = "Qt is a cross-platform C++ application framework. Qt's primary feature \
is its rich set of widgets that provide standard GUI functionality."
HOMEPAGE = "http://qt-project.org/"

inherit debian-package
PV = "5.3.2+dfsg"
INC_PR = "r1"

LICENSE = "GFDL-1.3 & (LGPL-2.1 | GPL-3.0)"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=2abca611d56c23eb99943bde21c80a0d \
                    file://LICENSE.GPL;md5=d32239bcb673463ab874e80d47fae504 \
                    file://LGPL_EXCEPTION.txt;md5=0145c4d1b6f96a661c2c139dfb268fb6 \
                    file://LICENSE.FDL;md5=6d9f2a9af4c8b8c3c769f6cc1b6aaf7e"

SRC_URI += "\
    file://0001-Add-linux-oe-g-platform.patch \
    file://0002-qlibraryinfo-allow-to-set-qt.conf-from-the-outside-u.patch \
    file://0003-Add-external-hostbindir-option.patch \
    file://0004-configureapp-Prefix-default-LIBDIRS-and-INCDIRS-with.patch \
    file://0005-qt_module-Fix-pkgconfig-and-libtool-replacements.patch \
    file://0008-configure-bump-path-length-from-256-to-512-character.patch \
    file://0010-Revert-Use-the-gcc-feature-in-simd.prf.patch \
"

inherit qmake5_base

# we don't want conflicts with qt4
OE_QMAKE_PATH_HEADERS = "${OE_QMAKE_PATH_QT_HEADERS}"
OE_QMAKE_PATH_ARCHDATA = "${OE_QMAKE_PATH_QT_ARCHDATA}"
OE_QMAKE_PATH_DATA = "${OE_QMAKE_PATH_QT_DATA}"
OE_QMAKE_PATH_BINS = "${OE_QMAKE_PATH_QT_BINS}"
OE_QMAKE_PATH_TRANSLATIONS = "${OE_QMAKE_PATH_QT_TRANSLATIONS}"
OE_QMAKE_PATH_DOCS = "${OE_QMAKE_PATH_QT_DOCS}"
OE_QMAKE_PATH_SETTINGS = "${OE_QMAKE_PATH_QT_SETTINGS}"
OE_QMAKE_PATH_EXAMPLES = "${OE_QMAKE_PATH_QT_EXAMPLES}"
OE_QMAKE_PATH_TESTS = "${OE_QMAKE_PATH_QT_TESTS}"

# qtbase is exception, configure script is using our get(X)QEvalMakeConf and setBootstrapEvalVariable functions to read it from shell
export OE_QMAKE_COMPILER
export OE_QMAKE_CC
export OE_QMAKE_CFLAGS
export OE_QMAKE_CXX
export OE_QMAKE_CXXFLAGS
export OE_QMAKE_LINK
export OE_QMAKE_LDFLAGS
export OE_QMAKE_AR
export OE_QMAKE_STRIP

# Regenerate header files when they are included in source tarball
# Otherwise cmake files don't set PRIVATE_HEADERS correctly
do_configure_prepend() {
	rm -rf ${S}/include
	mkdir -p ${S}/.git || true
}
