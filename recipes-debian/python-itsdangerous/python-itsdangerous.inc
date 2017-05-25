SUMMARY = "Various helpers to pass trusted data to untrusted environment"
DESCRIPTION = "itsdangerous provides a module that is a port of the django signing\n\
 module.  It's not directly copied but some changes were applied to\n\
 make it work better on its own.\n\
 .\n\
 itsdangerous allows web applications to use a key only it knows to\n\
 cryptographically sign data and hand it over to someone else (e.g. a\n\
 user).  When it gets the data back it can easily ensure that nobody\n\
 tampered with it."
HOMEPAGE = "http://pythonhosted.org/itsdangerous/"

DPN = "python-itsdangerous"
INC_PR = "r0"
inherit debian-package
PV = "0.24+dfsg1"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b61841e2bf5f07884148e2a6f1bcab0c"
inherit setuptools

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

do_install_append() {
	# remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/__pycache__
}
