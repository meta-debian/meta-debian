INC_PR = "r0"

inherit debian-package
PV = "3.2"
DPN = "pexpect"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c25d9a0770ba69a9965acc894e9f3644"

DEBIAN_QUILT_PATCHES = ""
# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS
