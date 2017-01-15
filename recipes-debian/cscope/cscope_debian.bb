SUMMARY = "interactively examine a C program source"
DESCRIPTION = "Cscope is an interactive text screen based source browsing tool."
HOMEPAGE = "http://freecode.com/projects/cscope"

PR = "r0"

DEPENDS = "ncurses"

inherit debian-package
PV = "15.8a"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=d4667b67b483823043fcffa489ea343b"

inherit autotools
