require recipes-core/sysvinit/sysvinit_2.88dsf.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/sysvinit/sysvinit:\
"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI += " \
file://rcS-default \
file://rc \
file://rcS \
file://bootlogd.init \
"

# Fix LIBDIR so libcrypt.a can be seen
do_compile_prepend() {
	sed -i -e "s:^LIBDIR=.*:LIBDIR=${STAGING_LIBDIR}:" ${S}/src/Makefile
}
