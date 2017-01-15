PR = "r0"

inherit debian-package
PV = "0.25"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=d9c6a2a0ca6017fda7cd905ed2739b37"

inherit autotools

# Configure follow debian/rules
EXTRA_OECONF += "--with-pluginpath=${base_libdir}/${DPN}"

# Follow debian/rules, change libraries location
do_install_append(){
	install -d ${D}${base_libdir}/${DPN}
	mv ${D}${libdir}/*.so.* ${D}${base_libdir}
	mv ${D}${libdir}/${DPN}/*.so ${D}${base_libdir}/${DPN}

	# Relink library
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	for file in ${D}${libdir}/*.so; do
		ln -sf ${rel_lib_prefix}${base_libdir}/$(basename $(readlink $file)) $file
	done
}

FILES_${PN} += "${base_libdir}/${DPN}/*.so"
FILES_${PN}-dbg += "${base_libdir}/${DPN}/.debug"

DEBIANNAME_${PN} = "${PN}2"
