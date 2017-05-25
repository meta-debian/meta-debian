SUMMARY = "Lightweight SOAP client for Python"
DISCRIPTION = "\
	The suds project is a Python soap web services client lib. \
	Suds leverages Python meta programming to provide an intuitive API \
	for consuming web services. Objectification of types defined in \
	the WSDL is provided without class generation. Programmers rarely \
	need to read the WSDL since services and WSDL based objects can be \
	easily inspected. Supports pluggable soap bindings."

LICENSE = "LGPL-3.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=847e96bce86d8774f491a92924343a29"

PR = "r0"
DPN = "suds"

DEPENDS += "python-setuptools-native"

inherit debian-package
inherit distutils
PV = "0.4.1"

DISTUTILS_INSTALL_ARGS += " \
	--root='${D}' \
	--prefix='${prefix}' \
	--install-lib='${libdir}/${PYTHON_DIR}/dist-packages' \
	--install-data='${datadir}'"

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

do_install_append() {
	# Remove unwanted files and folders
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	find ${D}${libdir} -type f -name "*.pyo" -exec rm -f {} \;
	find ${D}${libdir} -type f -name "SOURCES.txt" -exec rm -f {} \;
	rm -r ${D}${libdir}/${PYTHON_DIR}/site-packages
}

BBCLASSEXTEND = "native"
