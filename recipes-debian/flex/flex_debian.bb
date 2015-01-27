require recipes-devtools/flex/${BPN}_2.5.38.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/flex/files:"

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4742cf92e89040b39486a6219b68067"

SRC_URI += " \
file://do_not_create_pdf_doc.patch \
"
