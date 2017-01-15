#
# base recipe: meta/recipes-devtools/m4/m4.inc
#              meta/recipes-devtools/m4/m4-1.4.17.inc
# base branch: daisy
#

inherit debian-package
PV = "1.4.17"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://examples/COPYING;md5=4031593b2166d6c47cae282d944a7ede \
"

inherit autotools

SRC_URI += " \
	file://ac_config_links.patch \
"

# Follow debian/rules
EXTRA_OECONF += "--disable-rpath"

EXTRA_OEMAKE += "'infodir=${infodir}'"
LDFLAGS_prepend_libc-uclibc = " -lrt "
