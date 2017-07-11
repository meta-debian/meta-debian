HOMEPAGE = "Homepage: http://urllib3.readthedocs.org"
DESCRIPTION = " \
	urllib3 supports features left out of urllib and urllib2 libraries. \
		* Re-use the same socket connection for multiple requests \
		(HTTPConnectionPool and HTTPSConnectionPool) \
		(with optional client-side certificate verification). \
		* File posting (encode_multipart_formdata). \
		* Built-in redirection and retries (optional). \
		* Supports gzip and deflate decoding. \
		* Thread-safe and sanity-safe. \
		* Small and easy to understand codebase perfect for extending \
		and building upon."

INC_PR = "r0"

inherit debian-package
PV = "1.9.1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3be3707c5f24a69709682265e29566fe"

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS
