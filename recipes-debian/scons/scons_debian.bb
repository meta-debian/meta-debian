SUMMARY = "replacement for make"
DESCRIPTION = "SCons is a make replacement providing a range of enhanced features such \
 as automated dependency generation and built in compilation cache \
 support.  SCons rule sets are Python scripts so as well as the features \
 it provides itself SCons allows you to use the full power of Python \
 to control compilation."
HOMEPAGE = "http://www.scons.org/"

PR = "r0"
inherit debian-package
PV = "2.3.1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=28ee19c803f81f2faa8d53edc319bfc4"

inherit distutils

DISTUTILS_INSTALL_ARGS += "--no-version-script --standalone-lib --no-install-man"

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

do_install_append() {
	# remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${datadir}
	
}
do_install_append_class-native() {
	create_wrapper ${D}${bindir}/scons SCONS_LIB_DIR='${STAGING_DIR_HOST}/${PYTHON_SITEPACKAGES_DIR}'
}
BBCLASSEXTEND = "native"
