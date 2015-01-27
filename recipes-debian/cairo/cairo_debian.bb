require recipes-graphics/cairo/${BPN}_1.12.16.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-graphics/cairo/${BPN}:"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "LGPL-2.1 & MPL-1.1"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=e73e999e0c72b5ac9012424fa157ad77 \
file://COPYING-LGPL-2.1;md5=c9bb0ee6dbe833915b94063d594c4bfc \
file://COPYING-MPL-1.1;md5=bfe1f75d606912a4111c90743d6c7325 \
"
