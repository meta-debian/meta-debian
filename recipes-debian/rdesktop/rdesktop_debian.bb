require rdesktop.inc

#PR = "${INC_PR}.2"

#SRC_URI += " file://audio-2008.patch"
#SRC_URI_append_ossystems = " file://rdesktop-addin.patch"

inherit autotools

EXTRA_OECONF = "--with-openssl=${STAGING_EXECPREFIXDIR} --disable-credssp --disable-smartcard"

#SRC_URI[md5sum] = "c6fcbed7f0ad7e60ac5fcb2d324d8b16"
#SRC_URI[sha256sum] = "35026eaa8e14ca8bd0ba3730926f14222f8452f2ac662623bbf1909d8b060979"
#
# Meta-debian
#
inherit debian-package
DPR = "0"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

INSANE_SKIP_rdesktop_forcevariable = " already-stripped"
