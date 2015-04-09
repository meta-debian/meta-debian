require recipes-connectivity/openssl/openssl_1.0.1m.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-connectivity/openssl/openssl:"

inherit debian-package

DEBIAN_SECTION = "utils"
DPR = "2"

# "openssl | SSLeay" dual license
LICENSE = "openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9a8f968107345e0b75aa8c2ecaa7ec8"

#Remove depend on cryptodev-linux-native
DEPENDS_remove = "cryptodev-linux-native"

# remove patches which already exist in debian/patches directory:
# engines-install-in-libdir-ssl.patch (equal debian/patches/engines-path.patch)
# debian/version-script.patch
# debian/pic.patch
# debian/c_rehash-compat.patch
# debian/ca.patch
# debian/no-rpath.patch
# debian/man-dir.patch
# debian/man-section.patch
# debian/no-symbolic.patch
# debian/debian-targets.patch
# openssl_fix_for_x32.patch
SRC_URI += " \
file://configure-targets.patch \
file://shared-libs.patch \
file://oe-ldflags.patch \
file://openssl-fix-link.patch \
file://debian/make-targets.patch \
file://fix-cipher-des-ede3-cfb1.patch \
file://openssl-avoid-NULL-pointer-dereference-in-EVP_DigestInit_ex.patch \
file://openssl-avoid-NULL-pointer-dereference-in-dh_pub_encode.patch \
file://initial-aarch64-bits.patch \
file://find.pl \
file://openssl-fix-des.pod-error.patch \
file://Makefiles-ptest.patch \
file://ptest-deps.patch \
file://run-ptest \
"

# By engines-install-in-libdir-ssl.patch, the path is "${libdir}/ssl/engines/*.so"
# but by debian/patches/engines-path.patch, the path is "${libdir}/openssl-1.0.0/engines/*.so"
FILES_${PN}-engines = "${libdir}/openssl-1.0.0/engines/*.so ${libdir}/engines"
FILES_${PN}-engines-dbg = "${libdir}/openssl-1.0.0/engines/.debug"

# Override CFLAG since we don't want to depend on cryptodev-linux-native
# anymore.
CFLAG_remove = "-DHAVE_CRYPTODEV -DUSE_CRYPTODEV_DIGESTS"

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://openssl-native-test/run_native_test_openssl \
        file://openssl-native-test/run_version_command \
        file://openssl-native-test/run_help_command \
        file://openssl-native-test/run_cipher_command \
        file://openssl-native-test/run_speed_command \
        file://openssl-native-test/run_certificate_command \
"

DEBIAN_NATIVE_TESTS = "run_native_test_openssl"
TEST_DIR = "${B}/native-test"
