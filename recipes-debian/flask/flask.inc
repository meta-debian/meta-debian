SUMMARY = "micro web framework based on Werkzeug, Jinja2 and good intentions"
DESCRIPTION = "Flask is a micro web framework for Python based on Werkzeug, Jinja 2 and good\n\
intentions. A minimal Flask application looks like that:\n\
 .\n\
   from flask import Flask\n\
   app = Flask(__name__)\n\
 .\n\
   @app.route("/")\n\
   def hello():\n\
       return "Hello World!"\n\
 .\n\
   if__name__ == '__main__':\n\
       app.run()"
HOMEPAGE = "http://flask.pocoo.org/"

DPN = "flask"
INC_PR = "r0"
inherit debian-package
PV = "0.10.1"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=79aa8b7bc4f781210d6b5c06d6424cb0"
inherit setuptools

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

do_install_append() {
	# remove unwanted files
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/flask/*.pyc \
	       ${D}${PYTHON_SITEPACKAGES_DIR}/flask/testsuite
}
FILES_${PN} += "${datadir}/python*"
