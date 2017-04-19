require python-setuptools.inc
PR = "${INC_PR}.0"

PROVIDES = "python-distribute"

PACKAGES =+ "python-pkg-resources"

FILES_python-pkg-resources = "${libdir}/${PYTHON_DIR}/*-packages/pkg_resources.py"

RDEPENDS_${PN}_class-target += "python-pkg-resources"

RREPLACES_${PN} = "python-distribute"
RPROVIDES_${PN} = "python-distribute"
RCONFLICTS_${PN} = "python-distribute"

BBCLASSEXTEND = "native nativesdk"
