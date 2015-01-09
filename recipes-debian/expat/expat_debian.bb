require recipes-core/expat/${BPN}_2.1.0.bb

inherit debian-package
DEBIAN_SECTION = "text"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1b71f681713d1256e1c23b0890920874"
