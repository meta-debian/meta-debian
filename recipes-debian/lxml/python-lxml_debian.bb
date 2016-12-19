require lxml.inc
DPN = "lxml"

do_install_append() {
	# remove unwanted files
	rm -rf ${D}${libdir}/python*/*/${DPN}/*.pyc \
	       ${D}${libdir}/python*/*/${DPN}/*/*.pyc
}
