#
# base recipe: meta/recipes-core/glibc/cross-localedef-native_2.29.bb
# base branch: warrior
#

SUMMARY = "Cross locale generation tool for glibc"
HOMEPAGE = "http://www.gnu.org/software/libc/libc.html"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
file://LICENSES;md5=cfc0ed77a9f62fa62eded042ebe31d72 \
file://posix/rxspencer/COPYRIGHT;md5=dc5485bb394a13b2332ec1c785f5d83a \
"


inherit debian-package
require recipes-debian/sources/glibc.inc
BPN = "glibc"

# Tell autotools that we're working in the localedef directory
AUTOTOOLS_SCRIPT_PATH = "${S}/localedef"

inherit native
inherit autotools

SRCREV_localedef ?= "cd9f958c4c94a638fa7b2b4e21627364f1a1a655"

FILESPATH_append = ":${FILE_DIRNAME}/glibc:${COREBASE}/meta/recipes-core/glibc/glibc"
SRC_URI += " \
    git://github.com/kraj/localedef;branch=master;name=localedef;destsuffix=${BP}/localedef;protocol=https \
    file://0016-timezone-re-written-tzselect-as-posix-sh.patch \
    file://0017-Remove-bash-dependency-for-nscd-init-script.patch \
    file://0022-eglibc-Forward-port-cross-locale-generation-support.patch \
    file://0024-localedef-add-to-archive-uses-a-hard-coded-locale-pa.patch \
"

EXTRA_OECONF = "--with-glibc=${S}"
CFLAGS += "-fgnu89-inline -std=gnu99 -DIS_IN\(x\)='0'"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/localedef ${D}${bindir}/cross-localedef
}
