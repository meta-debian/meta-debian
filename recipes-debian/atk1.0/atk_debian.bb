require recipes-support/atk/atk_2.10.0.bb

BPN = "atk1.0"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "LGPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"
