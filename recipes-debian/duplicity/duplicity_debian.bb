SUMMARY = "encrypted bandwidth-efficient backup"
DESCRIPTION = "Duplicity backs directories by producing encrypted tar-format volumes \
and uploading them to a remote or local file server. Because duplicity \
uses librsync, the incremental archives are space efficient and only \
record the parts of files that have changed since the last backup. \
Because duplicity uses GnuPG to encrypt and/or sign these archives, they \
will be safe from spying and/or modification by the server. \
"
inherit debian-package
require recipes-debian/sources/duplicity.inc

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3715fc61024dbb5d6a30abce33aba311"

DEPENDS += "librsync"
RDEPENDS_${PN} +="python-compression python-misc python-resource \
                  python-fasteners python-logging python-monotonic \
                  python-six python-core gnupg"
RRECOMMENDS_${PN} += "python-paramiko lftp"

inherit setuptools

