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

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

do_install_append() {
	rm ${D}${libdir}/python2.7/site-packages/${DPN}/*.pyc
}

RDEPENDS_${PN} += " \
    python-ctypes \
    python-curses \
    python-pprint \
    python-shell \
    python-subprocess \
    python-textutils \
"
