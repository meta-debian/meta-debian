DESCRIPTION = "Remove cpp '#ifdef' lines from files \
 The unifdef utility selectively processes conditional cpp(1) directives. \
 It removes from a file both the directives and any additional text that \
 they specify should be removed, while otherwise leaving the file alone."
HOMEPAGE = "http://dotat.at/prog/unifdef/"

inherit native debian-package autotools
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=4da83e7128fb3e762bd4678e7e2f358d"

# Fix install dir
do_configure_prepend() {
	sed -i -e "s:^prefix =.*:prefix = ${prefix}:" ${S}/Makefile
}
