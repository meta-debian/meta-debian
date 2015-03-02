# Default repository uri
LINUX_REPO ?= "git://github.com/ystk"

# Default source directory, use Long Term Support Initiative Linux
LINUX_SRC ?= "linux-ltsi"

# Default branch
LINUX_SRCREV ?= "master"
SRCREV = "${LINUX_SRCREV}"

SRC_URI = "${LINUX_REPO}/${LINUX_SRC}.git;branch=${SRCREV};protocol=git"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# Required by KERNEL_PRIORITY, see kernel.bbclass
LINUX_VERSION ?= "3.10.0"
PV = "${LINUX_VERSION}+git${SRCPV}"
