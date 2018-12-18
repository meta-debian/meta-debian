require lxml.inc
DPN = "lxml"
inherit python3native

DEBIAN_PATCH_TYPE = "quilt" 

do_install_append() {
	# remove unwanted files
	rm -rf ${D}${libdir}/python*/*/${DPN}/__pycache__ \
	       ${D}${libdir}/python*/*/${DPN}/*/__pycache__
}

do_debian_patch_prepend() {
       rm -fr ${S}/.pc
}

# Provide python3-lxml-native
BBCLASSEXTEND = "native"
