#
# base recipe: meta/recipes-extended/libsolv/libsolv_0.7.3.bb
# base branch: warrior
# base commit: a4031efc0a4474a169d1b4f6be526383f0e02e60
#

SUMMARY = "Library for solving packages and reading repositories"
HOMEPAGE = "https://github.com/openSUSE/libsolv"
BUGTRACKER = "https://github.com/openSUSE/libsolv/issues"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=62272bd11c97396d4aaf1c41bc11f7d8"

inherit debian-package
require recipes-debian/sources/libsolv.inc

DEPENDS = "expat zlib"

SRC_URI += " \
           file://0001-build-use-GNUInstallDirs.patch \
"

inherit cmake

PACKAGECONFIG ??= "rpm"
PACKAGECONFIG[rpm] = "-DENABLE_RPMMD=ON -DENABLE_RPMDB=ON,,rpm"

EXTRA_OECMAKE = "-DMULTI_SEMANTICS=ON -DENABLE_COMPLEX_DEPS=ON"

PACKAGES =+ "${PN}-tools ${PN}ext"

FILES_${PN}-tools = "${bindir}/*"
FILES_${PN}ext = "${libdir}/${PN}ext.so.*"

BBCLASSEXTEND = "native nativesdk"
