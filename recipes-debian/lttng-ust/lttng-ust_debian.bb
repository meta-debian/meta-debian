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

inherit debian-package autotools
PV = "2.5.0"

DPN = "ust"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""

DEPENDS = "liburcu util-linux"

do_install_append() {
	# Remove unwanted files
	if [ -d ${D}${includedir}/uruc/ ]; then
		rm -rf ${D}${includedir}/uruc
	fi
}

PACKAGES =+ "lib${PN}-ctl"

FILES_lib${PN}-ctl = "${libdir}/lib${PN}-ctl.so.*"
FILES_${PN}-dev += "${bindir}/*"

LEAD_SONAME = "liblttng-ust.so.*"

# Correct the package name
DEBIANNAME_lib${PN}-ctl = "lib${PN}-ctl2"
