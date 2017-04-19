DESCRIPTION = "Rdesktop rdp client for X"
HOMEPAGE = "http://www.rdesktop.org"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

PR = "r1"
inherit debian-package
PV = "1.8.2"

DEPENDS = "virtual/libx11 openssl libgssglue alsa-lib"

inherit autotools-brokensep pkgconfig

# Currently, we have no recipe for pcsclite, so temporary disable smartcard.
EXTRA_OECONF = "--with-openssl=${STAGING_LIBDIR}/.. \
	--with-ipv6 --with-sound=alsa \
	--disable-smartcard \
"
# We are cross compiling, set 'fu_cv_sys_stat_statvfs64=cross'
# to prevent sys_stat_statvfs64 is 'yes' by running test code in configure,
# because it make error when build for qemuppc target:
# 	| disk.c:726:18: error: storage size of 'stat_fs' isn't known
# 	|   struct STATFS_T stat_fs;
CACHED_CONFIGUREVARS += "fu_cv_sys_stat_statvfs64=cross"

INSANE_SKIP_rdesktop_forcevariable = " already-stripped"
