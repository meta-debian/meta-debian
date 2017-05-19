#
# toolchain configuration for Debian
#
# most definitions are same as tcmode-default.inc,
# except PREFERRED_VERSION definitions are removed
#

PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}binutils = "binutils-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc-initial = "gcc-cross-initial-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc = "gcc-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}g++ = "gcc-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}compilerlibs = "gcc-runtime"
PREFERRED_PROVIDER_gdb = "gdb"

# Default libc config
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}libc-for-gcc = "${TCLIBC}"
PREFERRED_PROVIDER_virtual/nativesdk-${SDK_PREFIX}libc-for-gcc ?= "nativesdk-${TCLIBC}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}libc-initial = "${TCLIBC}-initial"
PREFERRED_PROVIDER_virtual/nativesdk-${SDK_PREFIX}libc-initial = "nativesdk-${TCLIBC}-initial"
PREFERRED_PROVIDER_virtual/gettext ??= "gettext"

PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}binutils-crosssdk = "binutils-crosssdk-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc-initial-crosssdk = "gcc-crosssdk-initial-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc-crosssdk = "gcc-crosssdk-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc++-crosssdk = "gcc-crosssdk-${TARGET_ARCH}"
