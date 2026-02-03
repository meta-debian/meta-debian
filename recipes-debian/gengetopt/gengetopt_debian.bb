# base recipe: meta-openembedded/meta-oe/recipes-support/gengetopt/gengetopt_2.23.bb
# base branch: dunfell
# base commit: 4078a203e8de353d61f42c72fd82d9acba58c198

SUMMARY = "skeleton main.c generator"
DESCRIPTION = "Gengetopt is a tool to write command line option parsing code for C programs."
SECTION = "utils"
HOMEPAGE = "https://www.gnu.org/software/gengetopt/gengetopt.html"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=ff95bfe019feaf92f524b73dd79e76eb"

inherit debian-package
require recipes-debian/sources/gengetopt.inc

DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-${REPACK_PV}.orig"

inherit autotools texinfo

BBCLASSEXTEND = "native nativesdk"
