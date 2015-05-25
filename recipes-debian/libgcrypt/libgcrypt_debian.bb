require libgcrypt.inc

SRC_URI[md5sum] = "3ccf8f1bf758a08e924cf5a36754f564"
SRC_URI[sha256sum] = "9dd2f359c16d7b8128d53f019c685cdedbcdcd1888904228a272d4769d9bf4e6"

#
#Meta-debian
#
inherit debian-package
DPR = "0"
DPN = "libgcrypt20"

SRC_URI += " \
           file://add-pkgconfig-support.patch \
           file://libgcrypt-fix-building-error-with-O2-in-sysroot-path.patch"
