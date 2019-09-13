require ${COREBASE}/meta/recipes-support/shared-mime-info/shared-mime-info.inc

inherit debian-package
require recipes-debian/sources/shared-mime-info.inc
FILESPATH_append = ":${COREBASE}/meta/recipes-support/shared-mime-info/shared-mime-info"

SRC_URI += "file://parallelmake.patch \
	    file://install-data-hook.patch"
