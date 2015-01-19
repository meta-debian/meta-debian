require recipes-extended/bc/${BPN}_1.06.bb

inherit debian-package
DEBIAN_SECTION = "math"
DPR = "0"

LICENSE = "MIT & GPL-2.0+ & LGPL-2.1+"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=b492e6ce406929d0b0a96c4ae7abcccf \
file://COPYING.LIB;md5=bf0962157c971350d4701853721970b4 \
file://install-sh;beginline=6;endline=32;md5=2ab67672a6ca4781a8291d8e11f5ccaf \
"
