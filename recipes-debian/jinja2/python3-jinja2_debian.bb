require jinja2.inc
PR = "${INC_PR}.0"

inherit python3native
DEPENDS += "python3-setuptools-native"
RDEPENDS_${PN} += "python3-markupsafe"
RRECOMMENDS_${PN} += "python3-pkg-resources"
