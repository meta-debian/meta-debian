PR = "${INC_PR}.0"
require lvm2.inc

inherit native

do_install_append(){
	# Change link location from absolute path to relative path
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	for file in ${D}${libdir}/*.so; do
		ln -sf ${rel_lib_prefix}${base_libdir}/$(basename $(readlink $file)) $file
	done
}
