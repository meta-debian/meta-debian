require lxml.inc
DPN = "lxml"
inherit python3native

do_install_append() {
	# remove unwanted files
	rm -rf ${D}${libdir}/python*/*/${DPN}/__pycache__ \
	       ${D}${libdir}/python*/*/${DPN}/*/__pycache__
}

# Provide python3-lxml-native
BBCLASSEXTEND = "native"
