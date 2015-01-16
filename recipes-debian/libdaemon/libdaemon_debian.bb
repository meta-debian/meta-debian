require recipes-support/libdaemon/libdaemon_0.14.bb

inherit debian-package

DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = " \
file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
file://libdaemon/daemon.h;beginline=9;endline=21;md5=bd9fbe57cd96d1a5848a8ba12d9a6bf4 \
"
