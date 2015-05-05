include dbus.inc

SRC_URI[md5sum] = "b02e9c95027a416987b81f9893831061"
SRC_URI[sha256sum] = "7085a0895a9eb11a952394cdbea6d8b4358e17cb991fed0e8fb85e2b9e686dcd"

#
# Meta-debian
# 
inherit debian-package
DPR = "0"
DEBIAN_SECTION = "admin"

# To fix QA issue that the file have not ship to any package
FILES_${PN} += "${bindir}/dbus-run-session"
