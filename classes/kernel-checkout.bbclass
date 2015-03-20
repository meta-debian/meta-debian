# Default repository uri
LINUX_REPO ??= "git://github.com/ystk"

# Default source directory, use Long Term Support Initiative Linux
LINUX_SRC ??= "linux-poky-debian"

# Default branch
LINUX_SRCREV ??= "linux-3.10.y-ltsi"
SRCREV = "${LINUX_SRCREV}"

SRC_URI = "${LINUX_REPO}/${LINUX_SRC}.git;branch=${SRCREV};protocol=git"

S ?= "${WORKDIR}/git"
B ?= "${WORKDIR}/build"

# Required by KERNEL_PRIORITY, see kernel.bbclass
LINUX_VERSION ??= "3.10.36"
PV = "${LINUX_VERSION}+git${SRCPV}"
