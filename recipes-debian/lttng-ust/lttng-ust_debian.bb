# 
# Base recipe: meta/recipes-kernel/lttng/lttng-ust_git.bb 
# Base branch: jethro
#
SUMMARY = "Linux Trace Toolkit Userspace Tracer 2.x"
DESCRIPTION = "The LTTng UST 2.x package contains the userspace tracer library to trace userspace codes."
HOMEPAGE = "http://lttng.org/ust"
BUGTRACKER = "https://bugs.lttng.org/projects/lttng-ust"

LICENSE = "LGPLv2.1+ & MIT & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c963eb366b781252b0bf0fdf1624d9e9 \
                    file://snprintf/snprintf.c;endline=32;md5=d3d544959d8a3782b2e07451be0a903c \
                    file://snprintf/various.h;endline=31;md5=89f2509b6b4682c4fc95255eec4abe44"

PR = "r0"

inherit debian-package autotools lib_package

DPN = "ust"

DEPENDS = "liburcu util-linux"

do_install_append() {
	# Remove unwanted files
	if [ -d ${D}${includedir}/uruc/ ]; then
		rm -rf ${D}${includedir}/uruc
	fi
}

PACKAGES =+ "lib${PN}-ctl2"

FILES_lib${PN}-ctl2 += "${libdir}/lib${PN}-ctl.so.*"

# Correct the package name
PKG_${PN}-dev = "lib${PN}-dev"
PKG_${PN} = "lib${PN}0"
