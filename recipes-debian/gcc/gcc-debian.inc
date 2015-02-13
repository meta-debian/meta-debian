inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"
DPN = "gcc-4.9"

DEBIAN_PATCH_TYPE = "quilt"

S = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/git"
