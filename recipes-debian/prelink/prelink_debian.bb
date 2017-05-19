#
# Base recipe: meta/recipes-devtools/prelink/prelink_git.b
# Base branch: daisy
#

SUMMARY = "An ELF prelinking utility"
DESCRIPTION = "The prelink package contains a utility which modifies ELF shared \
libraries and executables, so that far fewer relocations need to be resolved at \
runtime and thus programs come up faster."

PR = "r1"

inherit debian-package
PV = "0.0.20130503"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"
# file://configure-allow-to-disable-selinux-support_debian.patch:
#	this patch allow configure to enable or disable selinux support
# Patch prelink_cross.diff was created to enable option --root
SRC_URI += " \
file://configure-allow-to-disable-selinux-support_debian.patch \
file://prelink_cross.diff \
"

inherit autotools
DEPENDS = "elfutils binutils"

# Follow debian/rules and disable selinux support
EXTRA_OECONF = "--disable-libtool-lock --disable-dependency-tracking --disable-selinux"

# Install package follow debian/rules
do_install_append () {
	install -d ${D}${sysconfdir}/cron.daily ${D}${sysconfdir}/default
	install -m 0644 ${S}/debian/prelink.conf ${D}${sysconfdir}/prelink.conf
	install -m 0644 ${S}/debian/prelink.cron.daily ${D}${sysconfdir}/default/prelink
	install -m 0644 ${S}/debian/prelink.default ${D}${sysconfdir}/default/prelink
}

do_install_append_class-target () {
	mv ${D}${sbindir}/prelink ${D}${sbindir}/prelink.bin
	install -m 0755 ${S}/debian/prelink.sh ${D}${sbindir}/prelink
}

BBCLASSEXTEND = "native"

# If we're using image-prelink, we want to skip this on the host side
# but still do it if the package is installed on the target...
pkg_postinst_prelink() {
#!/bin/sh

if [ "x$D" != "x" ]; then
  ${@bb.utils.contains('USER_CLASSES', 'image-prelink', 'exit 0', 'exit 1', d)}
fi

prelink -a
}

pkg_prerm_prelink() {
#!/bin/sh

if [ "x$D" != "x" ]; then
  exit 1
fi

prelink -au
}

#Add package follow Debian
PACKAGES =+ "execstack"
FILES_execstack = "${bindir}/execstack"
FILES_execstack-doc = "${docdir}/execstack/* ${mandir}/man8/execstack.8"

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://prelink-native-test/run_native_test_prelink \
        file://prelink-native-test/execstack_run_with-s \
        file://prelink-native-test/execstack_run_with-c \
        file://prelink-native-test/execstack_run_with-q \
        file://prelink-native-test/execstack_run_version \
        file://prelink-native-test/execstack_run_help \
        file://prelink-native-test/execstack_run_usage \
"

DEBIAN_NATIVE_TESTS = "run_native_test_prelink"
TEST_DIR = "${B}/native-test"
