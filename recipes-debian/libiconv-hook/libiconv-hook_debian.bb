SUMMARY = "extension of iconv for libapache-mod-encoding"
DESCRIPTION = "This code is iconv compatible interface routine for mod_encoding.\n\
Taisuke Yamada writes sample code for hooking iconv() for mod_encoding.\n\
.\n\
In mod_encoding configuration directive,\n\
supports following encoding names additionally:\n\
.\n\
 MSSJIS\n\
 - This is almost same as SJIS, but is a Microsoft variant of it.\n\
.\n\
 JA-AUTO-SJIS-MS\n\
 - This is a special converter which does autodetection between\n\
   UTF-8/JIS/MSSJIS/SJIS/EUC-JP. This itself does not do conversion."

PR = "r0"

inherit debian-package
PV = "0.0.20021209"
DPN = "libapache-mod-encoding"

LICENSE = "Apache-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=6fda47c05855813c8b31ec8b0057eb05"

S = "${DEBIAN_UNPACK_DIR}/lib"

inherit autotools-brokensep

do_debian_patch() {
	cd ${DEBIAN_UNPACK_DIR}
	for p in patches/*; do
		patch -s -N -p1 < $p
	done
	cd -
}
