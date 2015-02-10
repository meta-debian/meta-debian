require recipes-core/gettext/gettext_0.18.3.2.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/gettext/gettext-0.18.3.2:\
"

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "GPLv3+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# There is no file git-version-gen in released archive, temporarily get 
# current version from ChangeLog with do-not-use-git-version-gen.patch
SRC_URI += " \
file://parallel.patch \
file://do-not-use-git-version-gen.patch \
"
