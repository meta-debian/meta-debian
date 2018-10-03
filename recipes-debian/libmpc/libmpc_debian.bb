#
# Base recipe: meta/recipes-support/libmpc/libmpc_1.1.0.bb
# Base branch: master
# Base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#

require recipes-support/libmpc/libmpc.inc

inherit debian-package
require recipes-debian/sources/mpclib3.inc
BPN = "mpclib3"
DEBIAN_UNPACK_DIR = "${WORKDIR}/mpc-${PV}"

LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS = "gmp mpfr"

# There is no debian patches
DEBIAN_PATCH_TYPE = "nopatch"

BBCLASSEXTEND = "native nativesdk"
