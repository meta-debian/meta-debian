SUMMARY = "Utilities to work withpatches"
DESCRIPTION = "This package includes the following utilities:\n\
 - combinediff creates a cumulative patch from two incremental patches\n\
 - dehtmldiff extracts a diff from an HTML page\n\
 - filterdiff extracts or excludes diffs from a diff file\n\
 - fixcvsdiff fixes diff files created by CVS that "patch" mis-interprets\n\
 - flipdiff exchanges the order of two patches\n\
 - grepdiff shows which files are modified by a patch matching a regex\n\
 - interdiff shows differences between two unified diff files\n\
 - lsdiff shows which files are modified by a patch\n\
 - recountdiff recomputes counts and offsets in unified context diffs\n\
 - rediff and editdiff fix offsets and counts of a hand-edited diff\n\
 - splitdiff separates out incremental patches\n\
 - unwrapdiff demangles patches that have been word-wrapped"
HOMEPAGE = "http://cyberelk.net/tim/patchutils/index.html"

PR = "r0"

inherit debian-package
PV = "0.3.3"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

# remove-manpage.patch:
#	We don't need manpage for native, \
#	so do not build man page to reduce number of dependency.
SRC_URI += " \
    file://remove-manpage.patch \
"

inherit autotools native perlnative

# Follow debian/rules, the Makefile runs some stuff twice with -jN
PARALLEL_MAKE = "-j1"
