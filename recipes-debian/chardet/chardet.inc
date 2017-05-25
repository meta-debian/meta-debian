HOMEPAGE = "https://github.com/chardet/chardet"
DESCRIPTION = "\
	Chardet takes a sequence of bytes in an unknown character encoding, and \
	attempts to determine the encoding. \
	Supported encodings: \
		* ASCII, UTF-8, UTF-16 (2 variants), UTF-32 (4 variants) \
		* Big5, GB2312, EUC-TW, HZ-GB-2312, ISO-2022-CN (Traditional and Simplified \
		Chinese) \
		* EUC-JP, SHIFT_JIS, ISO-2022-JP (Japanese) \
		* EUC-KR, ISO-2022-KR (Korean) \
		* KOI8-R, MacCyrillic, IBM855, IBM866, ISO-8859-5, windows-1251 (Cyrillic) \
		* ISO-8859-2, windows-1250 (Hungarian) \
		* ISO-8859-5, windows-1251 (Bulgarian) \
		* windows-1252 (English) \
		* ISO-8859-7, windows-1253 (Greek) \
		* ISO-8859-8, windows-1255 (Visual and Logical Hebrew) \
		* TIS-620 (Thai) "

INC_PR = "r0"

inherit debian-package
PV = "2.3.0"
DPN = "chardet"

LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6f89e2100d9b6cdffcea4f398e37343"

# Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS
