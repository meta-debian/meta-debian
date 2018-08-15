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

PREFERRED_PROVIDER_virtual/${SDK_PREFIX}binutils-crosssdk ?= "binutils-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/${SDK_PREFIX}gcc-initial = "gcc-crosssdk-initial-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/${SDK_PREFIX}gcc = "gcc-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/${SDK_PREFIX}g++ = "gcc-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/${SDK_PREFIX}compilerlibs = "nativesdk-gcc-runtime"

# Default libc config
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}libc-for-gcc = "${TCLIBC}"
PREFERRED_PROVIDER_virtual/nativesdk-${SDK_PREFIX}libc-for-gcc ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}libc-initial = "${TCLIBC}-initial"
PREFERRED_PROVIDER_virtual/nativesdk-${SDK_PREFIX}libc-initial ?= "nativesdk-glibc-initial"
PREFERRED_PROVIDER_virtual/gettext ??= "gettext"
