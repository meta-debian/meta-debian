SUMMARY = "Hierarchical view of the machine"
DESCRIPTION = "Hardware Locality (hwloc) provides a portable abstraction (across OS, versions,\n\
 architectures, ...) of the hierarchical topology of modern architectures. It\n\
 primarily aims at helping high-performance computing applications with\n\
 gathering information about the hardware so as to exploit it accordingly and\n\
 efficiently.\n\
 .\n\
 hwloc provides a hierarchical view of the machine, NUMA memory nodes,\n\
 sockets, shared caches, cores and simultaneous multithreading. It also gathers\n\
 various attributes such as cache and memory information.\n\
 .\n\
 hwloc supports old kernels not having sysfs topology information,\n\
 with knowledge of cpusets, offline cpus, and Kerrighed support. "
HOMEPAGE = "http://www.open-mpi.org/projects/hwloc/"

inherit debian-package
PV = "1.10.0"

LICENSE = "unfs3"
LIC_FILES_CHKSUM = "file://COPYING;md5=5fab7eadd2eb467906270369f405627f"

inherit autotools pkgconfig

# Base on debian/rules
EXTRA_OECONF += "--enable-plugins --enable-doxygen"

DEPENDS += "libtool libxml2 libpciaccess"

do_install_append() {
	# Base on debian/libhwloc5.link.in
	for i in 0 1 2 3 4; do
		ln -sf libhwloc.so.5 ${D}${libdir}/libhwloc.so.$i
	done
	install -d ${D}${datadir}/menu
	install -m 0644 ${S}/debian/hwloc.menu ${D}${datadir}/menu/hwloc
	rm -rf ${D}${libdir}/*.la
}
PACKAGECONFIG ??= ""
PACKAGECONFIG[opencl] = "--enable-opencl,--disable-opencl,ocl-icd khronos-opencl-headers"

PACKAGES =+ "lib${DPN}-common lib${DPN}-plugins lib${DPN}"
FILES_lib${DPN}-common = "${datadir}/hwloc/hwloc-valgrind.supp \
                         ${datadir}/hwloc/hwloc.dtd"
FILES_lib${DPN}-plugins = "${libdir}/hwloc/hwloc_opencl.so \
                          ${libdir}/hwloc/hwloc_pci.so \
                          ${libdir}/hwloc/hwloc_xml_libxml.so"
FILES_lib${DPN} = "${libdir}/libhwloc${SOLIBS}"
FILES_${PN} += "${datadir}/menu/*"
DEBIANNAME_${PN}-dev = "lib${DPN}-dev"
DEBIANNAME_${PN}-doc = "lib${DPN}-doc"
RPROVIDES_${PN}-dev = "lib${DPN}-dev"
RPROVIDES_${PN}-doc = "lib${DPN}-doc"

# Base on debian control
RDEPENDS_${PN}-dev += "lib${DPN} libltdl-dev"
RRECOMMENDS_lib${DPN} += "lib${DPN}-plugins"
RPROVIDES_lib${DPN} += "libhwloc0 libhwloc1 libhwloc2 libhwloc3 libhwloc4"
RREPLACES_lib${DPN} += "libhwloc0 libhwloc1 libhwloc2 libhwloc3 libhwloc4"
RCONFLICTS_lib${DPN} += "libhwloc0 libhwloc1 libhwloc2 libhwloc3 libhwloc4"
RDEPENDS_lib${DPN}-plugins += "lib${DPN}"
