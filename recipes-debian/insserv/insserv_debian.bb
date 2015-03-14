require recipes-devtools/insserv/insserv_1.14.0.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-devtools/insserv/insserv:\
${COREBASE}/meta/recipes-devtools/insserv/files:\
"

inherit debian-package
DEBIAN_SECTION = "misc"

DPR = "0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

# disable_runtests.patch no need to apply in 
# this version since runtest has disable already.
SRC_URI += "\
	file://makefile_debian.patch \
	file://insserv.conf \
	file://run-ptest \
"
