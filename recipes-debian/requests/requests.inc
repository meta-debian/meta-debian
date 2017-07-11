SUMMARY = "Elegant and simple HTTP library for Python3, built for human beings"
DISCRIPTION = " \
	Requests allow you to send HTTP/1.1 requests. You can add headers, \
	form data, multipart files, and parameters with simple Python \
	dictionaries, and access the response data in the same way. \
	It's powered by httplib and urllib3, but it does all the hard work \
	and crazy hacks for you. \
	Features: \
		* International Domains and URLs \
		* Keep-Alive & Connection Pooling \
		* Sessions with Cookie Persistence \
		* Browser-style SSL Verification \
		* Basic/Digest Authentication \
		* Elegant Key/Value Cookies \
		* Automatic Decompression \
		* Unicode Response Bodies \
		* Multipart File Uploads \
		* Connection Timeouts "
HOMEPAGE = "http://python-requests.org" 

INC_PR = "r0"

inherit debian-package
PV = "2.4.3"
DPN = "requests"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7869e52c8275537186de35e3cd5f9ec"

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS
