SUMMARY = "Host packages for the standalone SDK or external toolchain"
PR = "r0"
LICENSE = "MIT"

inherit packagegroup nativesdk

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

#
# minimal package set of SDK native tools
#
# autoconf, automake, libtool, pkgconfig:
#   basic tools required to build general applications
# bc:
#   Necessary for building new kernels.
#   Usually, SDK is used for building kernels
#
RDEPENDS_${PN} = "nativesdk-autoconf \
                  nativesdk-automake \
                  nativesdk-libtool \
                  nativesdk-pkgconfig \
                  nativesdk-bc \
                  nativesdk-libncurses5-dev \
                 "
