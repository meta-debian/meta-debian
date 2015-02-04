require recipes-devtools/docbook-utils/${PN}_0.6.14.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-devtools/docbook-utils/docbook-utils-0.6.14:\
"

inherit debian-package
DEBIAN_SECTION = "text"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI += " \
file://re.patch \
"
