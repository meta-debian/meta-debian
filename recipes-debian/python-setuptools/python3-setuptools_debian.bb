require python-setuptools.inc

PR = "${INC_PR}.0"
DPN = "python-setuptools"
inherit python3native

PACKAGES =+ "python3-pkg-resources"

FILES_python3-pkg-resources = "${libdir}/${PYTHON_DIR}/*-packages/pkg_resources.py"

RDEPENDS_${PN}_class-target += "python3-pkg-resources"

RCONFLICTS_${PN} = "python-setuptools"

BBCLASSEXTEND = "native nativesdk"

do_install_append() {
	# base on debian/rules
	mv ${D}${bindir}/easy_install \
		${D}${bindir}/easy_install3
}
