DESCRIPTION = "Rdesktop rdp client for X"
HOMEPAGE = "http://www.rdesktop.org"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

PR = "r0"
inherit debian-package

DEPENDS = "virtual/libx11 openssl libgssglue alsa-lib"

inherit autotools

# Currently, we have no recipe for pcsclite, so temporary disable smartcard.
EXTRA_OECONF = "--with-openssl=${STAGING_LIBDIR}/.. \
	--with-ipv6 --with-sound=alsa \
	--disable-smartcard \
"

INSANE_SKIP_rdesktop_forcevariable = " already-stripped"
