SUMMARY = "Simple top-like I/O monitor"
DESCRIPTION = "iotop does for I/O usage what top(1) does for CPU usage. \
    It watches I/O usage information output by the Linux kernel and displays \
    a table of current I/O usage by processes on the system."
HOMEPAGE = "http://guichaz.free.fr/iotop/"

PR = "r0"
inherit debian-package
PV = "0.6"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

inherit distutils
#Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

KEEP_NONARCH_BASELIB = "1"

# required by distutils/sysconfig.py to get config dir
export DEB_HOST_MULTIARCH

do_install_append() {
	rm ${D}${PYTHON_SITEPACKAGES_DIR}/${DPN}/*.pyc
}

RDEPENDS_${PN} += " \
    python-ctypes \
    python-curses \
    python-pprint \
    python-shell \
    python-subprocess \
    python-textutils \
"
