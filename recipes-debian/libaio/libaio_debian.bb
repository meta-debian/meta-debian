PR = "r0"

inherit debian-package
PV = "0.3.110"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

# Set install directory follow debian/rules
EXTRA_OEMAKE =+ "libdevdir=${libdir} libdir=${base_libdir}"
do_install () {
	oe_runmake install DESTDIR=${D}

	# Do not use absolute path for linking library
	libname=`readlink ${D}${libdir}/libaio.so | xargs basename`
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	ln -sf ${rel_lib_prefix}${base_libdir}/${libname} ${D}${libdir}/libaio.so
}
