#
# Base recipes: recipes-support/liburcu/liburcu_0.8.7.bb
# Base bracnh: jethro
#

SUMMARY = "Userspace RCU (read-copy-update) library"
HOMEPAGE = "http://lttng.org/urcu"
BUGTRACKER = "http://lttng.org/project/issues"

PR = "r0"

inherit debian-package autotools
PV = "0.8.5"

LICENSE = "LGPLv2.1+ & MIT-style"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f060c30a27922ce9c0d557a639b4fa3 \
                    file://urcu.h;beginline=4;endline=32;md5=4de0d68d3a997643715036d2209ae1d9 \
                    file://urcu/uatomic/x86.h;beginline=4;endline=21;md5=220552f72c55b102f2ee35929734ef42"

CFLAGS_append_libc-uclibc = " -D_GNU_SOURCE"

# Correct the package name
PKG_${PN} = "${PN}2"
