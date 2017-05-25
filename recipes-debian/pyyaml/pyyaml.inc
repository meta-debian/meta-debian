HOMEPAGE = "http://pyyaml.org/"
DPN = "pyyaml"
INC_PR = "r0"
inherit debian-package
PV = "3.11"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6015f088759b10e0bc2bf64898d4ae17"

inherit distutils

DEPENDS += "libyaml"

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS
