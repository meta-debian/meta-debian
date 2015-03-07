require recipes-devtools/apt/apt-native_0.9.9.4.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-devtools/apt/files:\
${COREBASE}/meta/recipes-devtools/apt/apt-0.9.9.4:\
"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

DEPENDS += "db-native"

# Exclude inappropriate patch which for old version 
# db_linking_hack.patch
# Resolve conflict for noconfigure.patch and no-curl.patch
# Note that no-curl.patch make apt does not support https methods
SRC_URI += " \
file://noconfigure_debian.patch \
file://no-curl_debian.patch \
file://apt.conf \
"
# Skip build test for native package
SRC_URI += " \
file://gtest-skip-fix.patch \
"

PARALLEL_MAKE = ""
