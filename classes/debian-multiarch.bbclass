#
# debian-multiarch.bbclass
#

GNU_SUFFIX = "${@get_gnu_suffix(d.getVar('TARGET_ARCH', True), d.getVar('TUNE_FEATURES', True))}"
DEB_HOST_MULTIARCH = "${@arch_to_multiarch(d.getVar('TARGET_ARCH', True))}-${TARGET_OS}${GNU_SUFFIX}"

# libdir will be change automatically to multiarch libdir.
# Set this variable to 1 in recipe if you want to handle libdir by yourself.
#   1: baselib = lib
#   0: baselib = lib/${DEB_HOST_MULTIARCH}
KEEP_NONARCH_BASELIB ?= "0"

# NOTE: native, nativesdk and crosssdk define baselib to 'lib' in their class
#       where we can't overwrite.
baselib = "${@base_conditional('KEEP_NONARCH_BASELIB','0','lib/${DEB_HOST_MULTIARCH}','lib',d)}"

PKG_CONFIG_PATH_APPEND = "${@base_conditional('KEEP_NONARCH_BASELIB','0',\
':${STAGING_EXECPREFIXDIR}/lib/pkgconfig',\
':${STAGING_LIBDIR}/${DEB_HOST_MULTIARCH}/pkgconfig',\
d)}"
PKG_CONFIG_PATH_APPEND_class-native = ":${STAGING_LIBDIR}/${DEB_HOST_MULTIARCH}/pkgconfig"
PKG_CONFIG_PATH_APPEND_class-nativesdk = ":${STAGING_LIBDIR}/${DEB_HOST_MULTIARCH}/pkgconfig"
PKG_CONFIG_PATH_APPEND_class-crosssdk = ":${STAGING_LIBDIR}/${DEB_HOST_MULTIARCH}/pkgconfig"
PKG_CONFIG_PATH .= "${PKG_CONFIG_PATH_APPEND}"


def arch_to_multiarch(arch):
    import re
    if re.match(r'i[4567]86',arch):
        return "i386"
    else:
        return arch


def get_gnu_suffix(arch, tune):
    tune_features = tune.split()
    # If arch is arm, TARGET_OS already contains "-gnueabi" itself
    if arch.startswith("arm"):
        # If arch is armel, suffix will be "-gnueabi"
        # if armhf, suffix will be "-gnueabihf"
        return ['', 'hf']['callconvention-hard' in tune_features]
    else:
        return '-gnu'
